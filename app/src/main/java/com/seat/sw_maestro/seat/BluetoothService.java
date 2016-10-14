package com.seat.sw_maestro.seat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

/*
    이 서비스에서는 주기적으로 방석으로 데이터를 보낸다. (실시간모드 or 일반모드)
    각 모드가 보내지는 경우는 다음과 같다.

    실시간모드 - 사용자가 Tab3(실시간)을 보고 있을 때
    일반모드 - 그 외의 경우

    실시간모드는 방석에 실시간모드용 데이터 요청을 하며, 일반모드는 일반모드용 데이터 요청을 한다.
    실시간모드의 경우에는 방석의 데이터를 실시간으로 받으며, 일반모드에는 쌓인 데이터가 있는 경우라면 쌓인 데이터를 받고, 없는 경우라면 받은 당시 데이터 받음.

    받아온 데이터는 DB에 저장되어야하는데 일반모드의 경우에는 1(변경가능)분에 한 번씩 데이터를 요청하며, 매 정각에 데이터를 정산한다.
    정산의 내용은 다음과 같다.
    몇 번 데이터를 받았는지 카운트하여, 1시간동안 몇 분을 앉아있었는지 체크한다.
    그 카운트된 시점마다 정확도를 분석하여, 카운트 된 동안의 평균을 구한다. 점수도 구함.
    DB에 넣는다. 현재시간, 정확도, 점수, 현재 날짜.

    매 정각 실행되는 그것을 만들어보자.
*/

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private static final String SeatName = "wonjun";    // 방석의 블루투스 이름을 입력한다.
    BluetoothSPP bt;
    private Messenger mRemote;  // 서비스와 액티비티 간에 통신을 하기 위해서 쓰는 메신저
    Timer timer;    // 일정시간마다 일을 하기 위해서 .. 타이머
    private AlarmManager alarmManager;  // 매 정각 일을 하기위해...

    // 생성자
    BluetoothService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "서비스가 bind 됨");
        return new Messenger(new RemoteHandler()).getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG,"서비스가 unbind 됨");

        return super.onUnbind(intent);
    }

    // what 값에 따라서 액티비티에서 받았을 때 처리가 달라진다.
    // 현재 Service -> Tab1 간에 통신은  what 값 0을 사용 (방석의 상태)
    // 현재 Service -> Tab3 간에 통신은  what 값 1을 사용 (자세의 결과)

    public void remoteSendMessage_Tab1(String data) {    // 액티비티로 메시지 전달. 방석의 연결 유무 상태를 나타내주기 위해 사용
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = data; // 오브젝트로 String data를 보낸다.
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void remoteSendMessage_Tab3(String data) {    // 액티비티로 메시지 전달. 자세의 결과
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = data; // 오브젝트로 String data를 보낸다.
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    // Service handler 추가  액티비티 -> 서비스로 받아오는 경우.
    private class RemoteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mRemote = (Messenger) msg.obj;
                    Log.d(TAG, "서비스와 탭1이 연결되었다.");

                    if(timer != null){  // 기존 타이머에 등록됬던 것 다 삭제
                        timer.cancel();
                        timer = null;
                    }

                    // 탭 1과 연결된 경우 동작을 여기다 둔다.
                    // Service -> Tab1 보냄
                    // 주기적으로 방석의 상태를 보내주는 일을 한다. 이것을 안하면 바뀌는 순간에만 텍스트뷰를 바꾸니... 바뀐 순간에 그 화면을 안보면 안바꿔짐
                    TimerTask timerTask = new TimerTask() {
                        public void run() {
                            Log.d(TAG, "TimerTask 1 실행 됨");
                            if (bt != null && bt.getServiceState() == 3)   // 3이면 블루투스에서 연결상태임
                                remoteSendMessage_Tab1("1"); // 연결되었다고 보내자.
                            else
                                remoteSendMessage_Tab1("0");
                        }
                    };

                    timer = new Timer();
                    timer.schedule(timerTask, 1000, 3000);  // 1초 후 3초마다 실행
                    break;

                case 1 :
                    mRemote = (Messenger) msg.obj;
                    Log.d(TAG, "서비스와 탭3이 연결되었다.");

                    if(timer != null){  // 기존 타이머에 등록됬던 것 다 삭제
                        timer.cancel();
                        timer = null;
                    }

                    // 탭 3과 연결된 경우 동작을 여기다 둔다.
                    // Service -> Tab3 보냄
                    // 자세의 결과를 보내준다.
                    TimerTask timerTask2 = new TimerTask() {
                        public void run() {
                            Log.d(TAG, "TimerTask 2 실행 됨");
                            if (bt != null && bt.getServiceState() == 3)   // 3이면 블루투스에서 연결상태임
                                remoteSendMessage_Tab3("자세의 결과"); // 연결되었다고 보내자.
                            else
                                remoteSendMessage_Tab3("자세의 결과");
                        }
                    };
                    timer = new Timer();
                    timer.schedule(timerTask2, 1000, 1000);
                    break;

                default :
                    Log.d(TAG, "등록되지 않은 곳에서 메시지가 옴");
                    break;
            }
        }
    }

    @Override
    public void onCreate() {

        bt = new BluetoothSPP(getApplicationContext());

        // 자동연결부분
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);
        bt.autoConnect(SeatName);

        // 블루투스 리스너
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                    // Do something when data incoming
                    // Log.d(TAG, "블루투스 데이터 받았다 -> " + message);
                    //Toast.makeText(getApplicationContext(), "데이터를 받았다.", Toast.LENGTH_SHORT).show();
                    //bt.send("1",true);
            }
        });

        // 블루투스 상태 리스너. 상태가 바뀌면 Tab1으로 보내준다. (딱히 필요는 없다... 3초마다 확인하긴 하니까...)
        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    //Log.d(TAG, "연결된 상태");
                    remoteSendMessage_Tab1("1");
                } else if (state == BluetoothState.STATE_CONNECTING) {
                    //Log.d(TAG, "연결중인 상태");
                    remoteSendMessage_Tab1("0");
                } else if (state == BluetoothState.STATE_LISTEN) {
                    //Log.d(TAG, "리슨 상태");
                    remoteSendMessage_Tab1("0");
                } else if (state == BluetoothState.STATE_NONE) {
                    //Log.d(TAG, "아무것도 아닌 상태");
                    remoteSendMessage_Tab1("0");
                }
            }
        });
        //Log.d(TAG,"서비스가 시작되었습니다.");

        setAlarm(); // 서비스가 실행될 때 알람을 실행한다. 1시간마다 실행하며 값을 정산해서 DB에 넣는 부분.
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //Log.d(TAG,"서비스가 종료되었습니다.");
        super.onDestroy();
        bt.stopService();
    }

    public void setAlarm(){
        Log.d(TAG, "알람이 설정");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 10); // 현재 시간으로부터 10초 뒤

        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);   // 알람 매니저
        Intent intent = new Intent(getApplication(), AlarmReceiver.class);  // 알람 리시버로 인텐트
        PendingIntent sender = PendingIntent.getBroadcast(getApplication(), 0, intent, 0);

        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1 * 1 * 5 * 1000, sender);
        // 위는 테스트용, 인자는 시간타입, 언제 실행할 건가?, 간격은?, 인텐트. 5초마다 AlarmReceiver 실행.

        //alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1 * 60 * 60 * 1000, sender);
        // 이게 진짜 실전용 1시간마다 동작하는 알람.

        // 24 * 60 * 60 * 1000 -> 하루 24시간, 60분, 60초
        //AlarmManager.INTERVAL_HOUR 1시간 간격??

    }
}
