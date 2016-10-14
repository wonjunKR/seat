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
import android.widget.LinearLayout;
import android.widget.TextView;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class Tab1 extends Fragment {

    private static final String TAG = "Tab1";
    private Messenger mRemote;  // 블루투스 서비스로부터 받아오는 메시지. 블루투스 연결 상태를 확인하기 위해서
    BluetoothSPP bt;
    boolean isScreenVisible = false;    // 사용자가 스크린을 보고있는지 확인하는 용도
    boolean isViewCreated = false;  // 뷰가 생성되었는지 확인하는 용도. 사용자가 스크린을 이 페이지를 보게되면 서비스와 바인드하는데 뷰가 생성되지 않아서 체크용

    TextView textView_bluetoothState;
    TextView textView_TodaySittingTime;
    TextView textView_TodayAccuracy;
    TextView textView_TodayComment;
    TextView textView_BarGauge1;
    TextView textView_BarGauge2;

    private ServiceConnection mConnection = new ServiceConnection() {   // 서비스와 핸들러를 연결해주는 부분
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemote = new Messenger(service);   // service 하고 연결될때

            if (mRemote != null) {  // Activity handler를 service에 전달하기
                Message msg = new Message();    // 새로운 메시지를 만들고
                msg.what = 0;   // Tab1 에서는 메시지에서 what을 0으로 사용한다. 블루투스 서비스에서 메시지가 어디서부터 왔는지 구분 위해
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
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0: // what이 0인 경우에만 Tab1전용 메시지이다.
                    if(msg.obj.toString() == "1")   // 핸들러 메시지로는 1이면 연결상태, 0이면 연결이 끊어진 상태이다.
                        changeBluetoothState(1);
                    else
                        changeBluetoothState(0);
                    break;
                default :// 나머지는 무시
                    //Log.d(TAG, "등록되지 않은 곳에서 메시지가 옴");
                    //Log.d(TAG, "내용 : " + msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            Log.d(TAG, "서비스와 언바인드 탭1");
            getActivity().unbindService(mConnection);    // 서비스가 먼저 동작중인지 알아보고 언바인드 해야해
        } catch (IllegalArgumentException e){
            Log.d(TAG, "서비스가 동작 안했는데 언바인드 함. 이게 문제가 될까...?");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isScreenVisible = true;
            if(bt != null && bt.isBluetoothEnabled() && isViewCreated == true){
                // 사용자가 이쪽을 보고 있고, 뷰가 생성된 적이 있으면 서비스와 바인드한다.
                Intent serviceIntent = new Intent(getContext(), BluetoothService.class);
                getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }else{
            isScreenVisible = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1,container,false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        bt = new BluetoothSPP(getContext());
        isViewCreated = true;   // 뷰가 생성되었음.

        textView_TodaySittingTime = (TextView) getActivity().findViewById(R.id.textViewTodaySittingTime);   // 오늘 앉은 시간
        textView_TodayAccuracy = (TextView) getActivity().findViewById(R.id.textViewTodayAccuracy);  // 오늘 정확도
        textView_bluetoothState = (TextView) getActivity().findViewById(R.id.bluetoothState);  // 블루투스 연결 상태를 보여주는 텍스트뷰
        textView_BarGauge1 = (TextView) getActivity().findViewById(R.id.textViewBarGauge1); // 정확도 게이지1
        textView_BarGauge2 = (TextView) getActivity().findViewById(R.id.textViewBarGauge2); // 정확도 게이지2
        textView_TodayComment = (TextView) getActivity().findViewById(R.id.textViewTodayComment); // 오늘의 자세 코멘트

        DatabaseManager databaseManager = new DatabaseManager(getContext());
        databaseManager.insertData("8",24,20,databaseManager.getCurrentDay()); // 임시로 데이터 넣기 나중에 지워
        databaseManager.insertData("0",53,72,databaseManager.getCurrentDay()); // 임시로 데이터 넣기 나중에 지워
        databaseManager.insertData("23",22,83,databaseManager.getCurrentDay()); // 임시로 데이터 넣기 나중에 지워
        databaseManager.insertData("10",13,46,databaseManager.getCurrentDay()); // 임시로 데이터 넣기 나중에 지워
        databaseManager.insertData("8",4,66,databaseManager.getCurrentDay()); // 임시로 데이터 넣기 나중에 지워

        // 앉은 시간 세팅 부분
        int sittingTime = databaseManager.getSittingTime_OneDay(databaseManager.getCurrentDay());   // 오늘 하루 앉은 시간 받아옴. 분으로
        int[] hourAndMinute = getHourMinute(sittingTime);   // [0]엔 시간, [1]엔 분 / 예) 170분 -> 2시간 50분으로 변환
        textView_TodaySittingTime.setText(hourAndMinute[0] + "시간 " + hourAndMinute[1] + "분 앉아 있음."); // 오늘 앉은 시간 세팅

        // 정확도 세팅 부분
        int accuracy = databaseManager.getAccuracy_OneDay(databaseManager.getCurrentDay()); // 오늘의 정확도. 코멘트, 게이지에도 쓰임
        textView_TodayAccuracy.setText(accuracy + "%"); // 오늘 정확도 세팅

        // 자세 코멘트 부분. 정확도 값에 따라서 오늘의 코멘트 값을 바꿔준다.
        changeTodayComment(accuracy);

        // 정확도 게이지 세팅 부분
        setBarGauge(accuracy);

        // 방석 상황을 표시하기 위한 부분
        // service 연결 시도
        if(bt.isBluetoothEnabled() && isScreenVisible == true) {   // 블루투스가 켜져있을때만 바인드를 시도함.
            // 사용자가 이쪽을 보고 있고, 뷰가 생성된 적이 있으면 서비스와 바인드한다.
            Intent serviceIntent = new Intent(getContext(), BluetoothService.class);
            getActivity().bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void changeBluetoothState(int state){    // 1이면 연결된 상태, 다른 값은 연결되지 않은 상태로 한다.
        if(state == 1)
            textView_bluetoothState.setText("방석이 연결되어 있습니다.");
        else
            textView_bluetoothState.setText("방석이 연결되지 않았습니다.");
    }

    public void changeTodayComment(int accuracy){   // 정확도 값에 자세 코멘트를 바꿔준다.
        if(accuracy >=70){
            textView_TodayComment.setText("좋은 자세를 잘 유지하고 있습니다!");
        } else if(accuracy >= 50){
            textView_TodayComment.setText("자세에 신경을 더 써보세요!");
        } else{
            textView_TodayComment.setText("자세가 좋지 않습니다!");
        }
    }

    public void setBarGauge(int accuracy){  // 정확도 값에 따라서 바 게이지를 조절한다.
        // tab_1.xml 가보면 이 부분은 weight_sum이 100으로 되어있다.
        // 따라서 활성화 부분은 위에서 구한 accuracy를 잡고, 나머지 부분은 100 - accuracy로 구한다.
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        params1.weight = accuracy;
        textView_BarGauge1.setLayoutParams(params1);
        params2.weight = 100-accuracy;
        textView_BarGauge2.setLayoutParams(params2);
    }

    public int[] getHourMinute(int sittingTime){    // 분 단위의 sittingTime을 넣으면 몇 시,몇 분으로 바꾸어서 리턴
        int[] hourAndMinute = new int[2];
        hourAndMinute[0] = sittingTime / 60;  // 시간은 60으로 나눈 몫
        hourAndMinute[1] = sittingTime % 60;  // 분은 60으로 나눈 나머지
        return hourAndMinute;
    }
}