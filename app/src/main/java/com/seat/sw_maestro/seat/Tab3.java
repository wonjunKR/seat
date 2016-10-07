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

import java.util.Date;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class Tab3 extends Fragment {

    private static final String TAG = "Tab3";
    private Messenger mRemote;  // 블루투스 서비스로부터 받아오는 메시지. 실시간 자세를 받아오기 위해서
    BluetoothSPP bt;
    boolean isScreenVisible = false;    // 사용자가 스크린을 보고있는지 확인하는 용도
    boolean isViewCreated = false;  // 뷰가 생성되었는지 확인하는 용도. 사용자가 스크린을 이 페이지를 보게되면 서비스와 바인드하는데 뷰가 생성되지 않아서 체크용

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
                case 1: // what이 1인 경우에만 Tab3으로 온 메시지로 한다.
                    //Log.d(TAG, "서비스로부터 받은 내 메시지 : " + msg.obj.toString());
                    /*
                        Tab3에서 setPosition으로 텍스트만 바꾸는데.. 만약에 핸들러 메시지로 자세 값이 온다면
                        이미지를 바꾸는 것으로 자세를 표현할 수 있다.
                        그래서 이 부분은 간단한 것으로 생각된다.

                        - 그럴려면 선행조건 -
                        바인드 되는 순간 서비스로 메시지를 보내니까 지금 사용자가 어디 페이지를 보고 잇는지를 알 수 있다. - 확인완료

                        따라서 그때마다 지금 TimeTask를 바꿔서 해당 페이지에 맞는 부분을 구현할 수 있다.
                        예) 1초마다 방석으로 값 요청 -> Perceptron 클래스 -> 자세의 결과 -> Tab3로 보낸다. -> 자세에 맞게 이미지 바꿈

                        해당 탭이 이동될 때 바인드가 정상적으로 바뀌어야함.
                        그런데 지금 탭3 -> 탭2 -> 탭1로 가는 경우에는 정상적으로 안되는 상황이 발생함. - 해결필요
                     */
                    setPosition();
                    break;
                default:
                    // 나머지는 무시
                    //Log.d(TAG,"등록되지 않은 곳에서 메시지가 옴 : " + msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isScreenVisible = true;
            if(bt != null && bt.isBluetoothEnabled() && isViewCreated == true){
                // 사용자가 이쪽을 보고 있고, 뷰가 생성된 적이 있으면 서비스와 바인드한다.
                Intent serviceIntent = new Intent(getContext(), BluetoothService.class);
                getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }
        else{
            isScreenVisible = false;
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

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        isViewCreated = true;   // 뷰가 생성되었음.

        bt = new BluetoothSPP(getContext());
        position = (TextView)getActivity().findViewById(R.id.position);

        // 어떤 자세인지 실시간으로 보여주기 위해서
        // service 연결 시도
        if(bt.isBluetoothEnabled() && isScreenVisible == true) {   // 블루투스가 켜져있을때만 바인드를 시도함.
            // 사용자가 이쪽을 보고 있고, 뷰가 생성된 적이 있으면 서비스와 바인드한다.
            Intent serviceIntent = new Intent(getContext(), BluetoothService.class);
            getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void setPosition(){
        String test = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        position.setText(test);
    }
}
