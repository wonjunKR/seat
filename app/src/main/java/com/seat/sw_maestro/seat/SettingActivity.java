package com.seat.sw_maestro.seat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class SettingActivity extends PreferenceActivity {

    BluetoothSPP bt; // 블루투스 테스트
    private static final String TAG = "SettingActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        Log.d(TAG, "SettingActivity");

        /* 알고리즘 테스트
        Perceptron perceptron;
        perceptron = new Perceptron(2); // 2개의 인풋을 위한 뉴럴 네트워크 생성
        float [][]fArray;
        int []iArray;
        DataUtils dataUtils = new DataUtils();
        fArray = dataUtils.readInputsFromFile(getApplicationContext()); // 인풋 파일 읽어오고...
        iArray = dataUtils.readOutputsFromFile(getApplicationContext());

        Log.d(TAG, "학습 전 예측 : " + perceptron.feedforward(fArray[0]));
        for(int i = 0; i<100; i++){
            perceptron.train(fArray[i],iArray[i]);  // 학습을 돌려보장
            perceptron.train(fArray[i],iArray[i]);  // 학습을 돌려보장
            perceptron.train(fArray[i],iArray[i]);  // 학습을 돌려보장
        }
        for(int i = 0; i<100; i++){
            Log.d(TAG, "운동 : " + fArray[i][0] + " 담배 : " + fArray[i][1] + " 결과 : " + perceptron.feedforward(fArray[i]));
        }
        */


        // 블루투스 테스트용 나중에 지워
        bt = new BluetoothSPP(getApplicationContext());

        if(!bt.isBluetoothAvailable()) {    // 블루투스 자체를 지원 안함. 가능성이 거의 없겠지?
            Toast.makeText(getApplicationContext(), "블루투스가 가능한 기기가 아닙니다.", Toast.LENGTH_LONG).show();
        }
        else {  // 블루투스는 됨
            if (!bt.isBluetoothEnabled()) { // 블루투스가 꺼져있다면.
                Log.d(TAG, "블루투스 켜달라고 인텐트");
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            } else {    // 블루투스가 켜져있다면.
                // 자동연결
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }

        bt.send("1",true);
        Log.d(TAG, "값 보낸다 ");
        bt.send("y",true);

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                // Do something when data incoming
                Log.d(TAG, "블루투스 데이터 받았다 -> " + message);
                bt.send("1",true);
            }
        });

        // 여기까지 블루투스 테스트

        Toast.makeText(getApplicationContext(), "일부 설정은 앱을 재시작하면 적용됩니다.", Toast.LENGTH_LONG).show();

        ListPreference listPreference = (ListPreference) findPreference("prefGraphList");   // 그래프 관련
        setOnPreferenceChange(listPreference);  // 값이 바뀌면.. 리스너 등록
    }

    // 요거는 리스너고, 바뀔때마다 프리퍼런스 키와 바뀐 값을 표시
    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            //Log.d(TAG, "preference key : " + preference.getKey());
            //Log.d(TAG, "value : " + newValue);

            // 환경설정 정보를 저장하기 위한 sharedPreferences
            SharedPreferences prefs = getSharedPreferences("SettingStatus", MODE_PRIVATE);  // UserStatus 아닌 것 주의!!
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString(preference.getKey(), newValue.toString()); // 바뀐 키와 값을 쉐어드 프리퍼런스에 저장.
            editor.commit();

            return true;
        }
    };

    // 이거는 위에서 만든 리스너를 등록해주는 것. 인자로 어떤 프리퍼런스인지만 넣어주면 된다.
    private void setOnPreferenceChange(Preference mPreference) {
        mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        onPreferenceChangeListener.onPreferenceChange(mPreference,
                PreferenceManager.getDefaultSharedPreferences(mPreference.getContext()).getString(mPreference.getKey(), ""));
    }

    // 블루투스 테스트
    // 상태를 나타내는 상태 변수
    //STATE_NONE = 0; // we're doing nothing
    //STATE_LISTEN = 1; // now listening for incoming connections
    //STATE_CONNECTING = 2; // now initiating an outgoing connection
    //STATE_CONNECTED = 3; // now connected to a remote device

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "켜진 상태에서 기기에 연결");
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                bt.connect(data);
            }

        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {    // 블루투스 연결해주세요. 인텐트 결과
            if(resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "블루투스 요청 후 켰음. 자동 연결");
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Log.d(TAG, "블루투스 요청 후 취소");
                Toast.makeText(getApplicationContext(), "블루투스를 연결해야 서비스가 가능합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void setup() {
        //bt.autoConnect("HC-06");
        bt.autoConnect("wonjun");
    }
}