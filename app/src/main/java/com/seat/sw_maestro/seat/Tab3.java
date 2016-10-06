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

public class Tab3 extends Fragment {

    private static final String TAG = "Tab3";
    private Messenger mRemote;  // 블루투스 서비스로부터 받아오는 메시지. 실시간 자세를 받아오기 위해서

    TextView position;

    private ServiceConnection mConnection = new ServiceConnection() {   // 서비스와 핸들러를 연결해주는 부분
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemote = new Messenger(service);   // service 하고 연결될때

            if (mRemote != null) {  // Activity handler를 service에 전달하기
                Message msg = new Message();    // 새로운 메시지를 만들고
                msg.what = 1;   // Tab3 에서는 메시지에서 what을 1로 사용한다. 블루투스 서비스에서 메시지가 어디서부터 왔는지 구분 위해
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

    // 서비스로부터 메시지 받았을 때 어떻게 처리할 것인가? - Tab3에서는 실시간 자세를 보여준다.
    private class RemoteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {    // 핸들러 메시지로 어떤 자세인지 받으면 되겠지??
            switch (msg.what){
                case 1:
                    Log.d(TAG, "서비스로부터 받은 내 메시지 : " + msg.obj.toString());
                    setPosition();
                    break;
                default:
                    Log.d(TAG,"등록되지 않은 곳에서 메시지가 옴 : " + msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            getActivity().unbindService(mConnection);    // 서비스가 먼저 동작중인지 알아보고 언바인드 해야해
        } catch (IllegalArgumentException e){
            Log.d(TAG, "서비스가 동작 안했는데 언바운드 함. 이게 문제가 될까...?");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        position = (TextView)getActivity().findViewById(R.id.position);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        BluetoothSPP bt = new BluetoothSPP(getContext());

        // 어떤 자세인지 실시간으로 보여주기 위해서
        // service 연결 시도
        if(bt.isBluetoothEnabled()) {   // 블루투스가 켜져있을때만 바인드를 시도함.
            Log.d(TAG, "연결 부분");
            Intent serviceIntent = new Intent(getContext(), BluetoothService.class);
            getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void setPosition(){
    //    position.setText(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date(System.currentTimeMillis())));
    }
}
