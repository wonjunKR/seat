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
        return new Messenger(new RemoteHandler()).getBinder();
    }

    // 다른 핸들러를 만들려면 what을 바꿔서 또 만들자.
    public void remoteSendMessage(String data) {    // 액티비티로 메시지 전달. 방석의 연결 유무 상태를 나타내주기 위해 사용
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

    // Service handler 추가
    private class RemoteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) { // 메시지의 what의 타입에 따라서 보내는게 달라진다. 여러 액티비티에 보내야하는 경우에는 나눠야겠지?
                case 0 :    // 0은 방석 연결 유무용
                    // Register activity hander
                    //Log.d(TAG, "handleMessage");
                    mRemote = (Messenger) msg.obj;
                    break;
                default :
                    remoteSendMessage("TEST");
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

        // 리스너
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                //Log.d(TAG, "블루투스 데이터 받았다 -> " + message);
                //Toast.makeText(getApplicationContext(), "데이터를 받았다.", Toast.LENGTH_SHORT).show();
                //bt.send("1",true);
            }
        });

        // 블루투스 상태 리스너
        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if(state == BluetoothState.STATE_CONNECTED){
                    Log.d(TAG, "연결된 상태");
                    remoteSendMessage("1");
                }
                else if(state == BluetoothState.STATE_CONNECTING) {
                    Log.d(TAG, "연결중인 상태");
                    remoteSendMessage("0");
                }
                else if(state == BluetoothState.STATE_LISTEN) {
                    Log.d(TAG, "리슨 상태");
                    remoteSendMessage("0");
                }
                else if(state == BluetoothState.STATE_NONE) {
                    Log.d(TAG, "아무것도 아닌 상태");
                    remoteSendMessage("0");
                }
            }
        });

        // 주기적으로 방석의 상태를 보내주는 일을 한다. 이것을 안하면 바뀌는 순간에만 텍스트뷰를 바꾸니... 바뀐 순간에 그 화면을 안보면 안바꿔짐
        TimerTask timerTask = new TimerTask() {
            public void run() {
                //Log.d(TAG,"반복 실행");
                //Log.d(TAG,"state : " + bt.getServiceState());
                if(bt != null && bt.getServiceState() == 3)   // 3이면 블루투스에서 연결상태임
                    remoteSendMessage("1"); // 연결되었다고 보내자.
                else
                    remoteSendMessage("0");
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,1000,3000);

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
