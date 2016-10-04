package com.seat.sw_maestro.seat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private static final String SeatName = "wonjun";    // 방석의 블루투스 이름을 입력한다.
    BluetoothSPP bt;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        bt = new BluetoothSPP(getApplicationContext());

        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);
        bt.autoConnect(SeatName);

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                //Log.d(TAG, "블루투스 데이터 받았다 -> " + message);
                //bt.send("1",true);
            }
        });

        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if(state == BluetoothState.STATE_CONNECTED){
                    Log.d(TAG, "연결된 상태");
                }
                else if(state == BluetoothState.STATE_CONNECTING) {
                    Log.d(TAG, "연결중인 상태");
                }
                else if(state == BluetoothState.STATE_LISTEN) {
                    Log.d(TAG, "리슨 상태");
                }
                else if(state == BluetoothState.STATE_NONE) {
                    Log.d(TAG, "아무상태도아님");
                }
            }
        });

        Log.d(TAG,"서비스가 시작되었습니다.");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"서비스가 종료되었습니다.");
        super.onDestroy();
    }
}
