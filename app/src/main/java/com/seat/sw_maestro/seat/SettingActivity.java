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

        // 테스트용 나중에 지울것
        DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
        String date = databaseManager.getCurrentDay();  // 현재의 날짜 예)20160926
        String timeLine = databaseManager.getCurrentHour(); // 현재의 시간 14:25면 14를 리턴
        //databaseManager.insertData(timeLine,53,78,date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜

        // 임시 데이터
        databaseManager.insertData("0",13,78,"20160925");
        databaseManager.insertData("1",12,76,"20160925");
        databaseManager.insertData("2",18,86,"20160925");
        databaseManager.insertData("3",19,58,"20160925");
        databaseManager.insertData("4",13,47,"20160925");
        databaseManager.insertData("5",51,75,"20160925");

        databaseManager.insertData("1",23,82,"20160926");
        databaseManager.insertData("2",35,78,"20160926");
        databaseManager.insertData("3",45,78,"20160926");
        databaseManager.insertData("23",55,78,"20160926");
        //databaseManager.selectData();   // 조회
        //Log.d(TAG,"sittingTime : " + databaseManager.getSittingTime("15","20160925"));
        //Log.d(TAG,"accuracy : " + databaseManager.getAccuracy("15","20160925"));
        //Log.d(TAG,"sittingTime_OneDay : " + databaseManager.getSittingTime_OneDay("20160925"));
        //Log.d(TAG,"accuracy_OneDay : " + databaseManager.getAccuracy_OneDay("20160926"));
        //databaseManager.getCurrentMonth();
        //databaseManager.getCurrentYear();

        Toast.makeText(getApplicationContext(), "일부 설정은 앱을 재시작하면 적용됩니다.", Toast.LENGTH_LONG).show();

        ListPreference listPreference = (ListPreference) findPreference("prefGraphList");   // 그래프 관련
        setOnPreferenceChange(listPreference);  // 값이 바뀌면.. 리스너 등록
    }

    // 요거는 리스너고, 바뀔때마다 프리퍼런스 키와 바뀐 값을 표시
    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Log.d(TAG, "preference key : " + preference.getKey());
            Log.d(TAG, "value : " + newValue);

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