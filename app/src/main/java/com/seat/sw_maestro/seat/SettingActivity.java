package com.seat.sw_maestro.seat;

import android.app.Activity;
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



        /*
        // 블루투스 테스트용 나중에 지워
        bt = new BluetoothSPP(getApplicationContext());

        if(!bt.isBluetoothAvailable()) {    // 블루투스 자체를 지원 안함
            Toast.makeText(getApplicationContext(), "블루투스가 가능한 기기가 아닙니다.", Toast.LENGTH_LONG).show();
        }
        else {  // 블루투스는 됨
            if (!bt.isBluetoothEnabled()) { // 되지만, 블루투스가 켜져있지 않음
                //Toast.makeText(getApplicationContext(), "블루투스가 가능하지 않습니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            } else {    // 되면서 켜져있으면...
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
        }

        // 여기까지 블루투스 테스트
        */

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                //bt.connect(data);
                Log.d(TAG, "test1");
            }

        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                //bt.setupService();
                Log.d(TAG, "test2");

            } else {
                Log.d(TAG, "test3");
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }
}