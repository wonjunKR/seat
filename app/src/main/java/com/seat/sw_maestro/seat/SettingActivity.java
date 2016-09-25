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