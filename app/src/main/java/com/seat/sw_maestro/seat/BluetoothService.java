package com.seat.sw_maestro.seat;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private static final String SeatName = "wonjun";    // 방석의 블루투스 이름을 입력한다.
    BluetoothSPP bt;
    private Messenger mRemote;  // 서비스와 액티비티 간에 통신을 하기 위해서 쓰는 메신저

    // 생성자
    BluetoothService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "서비스가 바인딩 됨");
        return new Messenger(new RemoteHandler()).getBinder();
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

    // Service handler 추가  액티비티 -> 서비스로 받아오는 경우. 우리의 앱 경우에는 없다.
    private class RemoteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    mRemote = (Messenger) msg.obj;
                    break;
                case 1 :
                    mRemote = (Messenger) msg.obj;
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
                    Log.d(TAG, "연결된 상태");
                    remoteSendMessage_Tab1("1");
                } else if (state == BluetoothState.STATE_CONNECTING) {
                    Log.d(TAG, "연결중인 상태");
                    remoteSendMessage_Tab1("0");
                } else if (state == BluetoothState.STATE_LISTEN) {
                    Log.d(TAG, "리슨 상태");
                    remoteSendMessage_Tab1("0");
                } else if (state == BluetoothState.STATE_NONE) {
                    Log.d(TAG, "아무것도 아닌 상태");
                    remoteSendMessage_Tab1("0");
                }
            }
        });

        // Service -> Tab1 보냄
        // 주기적으로 방석의 상태를 보내주는 일을 한다. 이것을 안하면 바뀌는 순간에만 텍스트뷰를 바꾸니... 바뀐 순간에 그 화면을 안보면 안바꿔짐
        TimerTask timerTask = new TimerTask() {
            public void run() {
                if (bt != null && bt.getServiceState() == 3)   // 3이면 블루투스에서 연결상태임
                    remoteSendMessage_Tab1("1"); // 연결되었다고 보내자.
                else
                    remoteSendMessage_Tab1("0");
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 1000, 3000);


        // Service -> Tab3 보냄
        // 자세의 결과를 보내준다.
        TimerTask timerTask2 = new TimerTask() {
            public void run() {
                if (bt != null && bt.getServiceState() == 3)   // 3이면 블루투스에서 연결상태임
                    remoteSendMessage_Tab3("연결상태 3으로 보낸다"); // 연결되었다고 보내자.
                else
                    remoteSendMessage_Tab3("연결안댐 3으로 보낸다");
            }
        };
        Timer timer2 = new Timer();
        timer2.schedule(timerTask2, 1000, 3000);


        Log.d(TAG,"서비스가 시작되었습니다.");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"서비스가 종료되었습니다.");
        super.onDestroy();
        bt.stopService();
    }
}
