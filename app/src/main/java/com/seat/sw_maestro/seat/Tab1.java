package com.seat.sw_maestro.seat;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class Tab1 extends Fragment {

    private static final String TAG = "Tab1";
    private Messenger mRemote;  // 블루투스 서비스로부터 받아오는 메시지. 블루투스 연결 상태를 확인하기 위해서

    TextView textView_bluetoothState;

    private ServiceConnection mConnection = new ServiceConnection() {   // 서비스와 핸들러를 연결해주는 부분
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemote = new Messenger(service);   // service 하고 연결될때

            if (mRemote != null) {  // Activity handler를 service에 전달하기
                Message msg = new Message();    // 새로운 메시지를 만들고
                msg.what = 0;
                msg.obj = new Messenger(new RemoteHandler());   // 액티비티의 핸들러를 전달한다.
                try {
                    mRemote.send(msg);  // 전달
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemote = null; // service 하고 연결이 끊길때
        }
    };

    // 서비스로부터 메시지 받았을 때 어떻게 처리할 것인가? - 우리는 방석의 블루투스 연결 상태를 바꿔준다.
    private class RemoteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {    // 핸들러 메시지로는 1이면 연결상태, 0이면 연결안된 상태가 들어온다.
            if(msg.obj.toString() == "1")
                changeBluetoothState(1);
            else
                changeBluetoothState(0);
            super.handleMessage(msg);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        BluetoothSPP bt = new BluetoothSPP(getContext());
        Log.d(TAG, "방석 상태 : " + bt.getServiceState());

        textView_bluetoothState = (TextView) getActivity().findViewById(R.id.bluetoothState);  // 블루투스 연결 상태를 보여주는 텍스트뷰

        // service 연결 시도
        if(bt.isBluetoothEnabled()) {   // 블루투스가 켜져있을때만 바인드를 시도함.
            Intent serviceIntent = new Intent(getContext(), BluetoothService.class);
            getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void changeBluetoothState(int state){    // 1이면 연결된 상태, 다른 값은 연결되지 않은 상태로 한다.
        if(state == 1)
            textView_bluetoothState.setText("방석이 연결되었습니다.");
        else
            textView_bluetoothState.setText("방석이 연결되지 않았습니다.");
    }
}