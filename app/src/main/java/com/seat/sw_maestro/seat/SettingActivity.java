package com.seat.sw_maestro.seat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity {

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

        /*
        // 데이터베이스 테스트
        DatabaseManager databaseManager = new DatabaseManager(this);

        String date = databaseManager.getCurrentDay();  // 현재의 날짜. 타입 -yyyyMMdd
        String timeLine = databaseManager.getCurrentHour(); // 현재의 시간. 타입 - HH
        // 데이터 추가하기
        databaseManager.insertData(timeLine, 10, 20, date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        databaseManager.insertData(timeLine, 20, 30, date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        databaseManager.insertData(timeLine, 40, 50, date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        databaseManager.insertData(timeLine, 50, 60, date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜

        databaseManager.insertData("18", 10, 20, "20161017");    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        databaseManager.insertData("18", 20, 30, "20161017");    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        databaseManager.insertData("19", 40, 50, "20161017");    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        databaseManager.insertData("20", 50, 60, "20161017");    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        */

        //SharedPreferences queue_accuracy = getSharedPreferences("accuracy", MODE_PRIVATE);
        //Log.d(TAG, "테스트 : " + queue_accuracy.getInt("1",-1));
        //DatabaseManager databaseManager = new DatabaseManager(this);
        //Log.d(TAG, "테스트2 : " + databaseManager.getAccuracy("19","20161018"));

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
}