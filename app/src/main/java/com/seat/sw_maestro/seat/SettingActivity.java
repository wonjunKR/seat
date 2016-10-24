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

        //* 알고리즘 테스트
        Perceptron perceptron1;
        Perceptron perceptron2;
        Perceptron perceptron3;
        Perceptron perceptron4;
        Perceptron perceptron5;

        perceptron1 = new Perceptron(9);
        perceptron2 = new Perceptron(9);
        perceptron3 = new Perceptron(9);
        perceptron4 = new Perceptron(9);
        perceptron5 = new Perceptron(9);

        // 0.877	-9.245	2.963	-65.73	-56.37	-1.167	63.44	-0.0217	1.854
        perceptron1.weights[0] = (float) 0.877;
        perceptron1.weights[1] = (float) -9.245;
        perceptron1.weights[2] = (float) 2.963;
        perceptron1.weights[3] = (float) -65.73;
        perceptron1.weights[4] = (float) -56.37;
        perceptron1.weights[5] = (float) -1.167;
        perceptron1.weights[6] = (float) 63.44;
        perceptron1.weights[7] = (float) -0.0217;
        perceptron1.weights[8] = (float) 1.854;
        //-0.911	-3.423	2.678	8.603	-0.607	-11.25	7.134	-7.373	-1.49
        perceptron2.weights[0] = (float) -0.911;
        perceptron2.weights[1] = (float) -3.423;
        perceptron2.weights[2] = (float) 2.678;
        perceptron2.weights[3] = (float) 8.603;
        perceptron2.weights[4] = (float) -0.607;
        perceptron2.weights[5] = (float) -11.25;
        perceptron2.weights[6] = (float) 7.134;
        perceptron2.weights[7] = (float) -7.373;
        perceptron2.weights[8] = (float) -1.49;
        //-14.05	2.23	-7.38	4.89	9.09	6.64	-10.81	-8.56	8.8
        perceptron3.weights[0] = (float) -14.05;
        perceptron3.weights[1] = (float) 2.23;
        perceptron3.weights[2] = (float) -7.38;
        perceptron3.weights[3] = (float) 4.89;
        perceptron3.weights[4] = (float) 9.09;
        perceptron3.weights[5] = (float) 6.64;
        perceptron3.weights[6] = (float) -10.84;
        perceptron3.weights[7] = (float) -8.56;
        perceptron3.weights[8] = (float) 8.8;
        //-11.79	-0.86	-32.63	-10	-4.83	5.49	-5.05	32.78	-4.37
        perceptron4.weights[0] = (float) -11.79;
        perceptron4.weights[1] = (float) -0.86;
        perceptron4.weights[2] = (float) -32.63;
        perceptron4.weights[3] = (float) -10;
        perceptron4.weights[4] = (float) -4.83;
        perceptron4.weights[5] = (float) 5.49;
        perceptron4.weights[6] = (float) -5.05;
        perceptron4.weights[7] = (float) 32.78;
        perceptron4.weights[8] = (float) -4.37;
        //4.32	10.96	23.89	-33.78	-19.25	-12.42	-16.34	21.56	-0.459
        perceptron5.weights[0] = (float) 4.32;
        perceptron5.weights[1] = (float) 10.96;
        perceptron5.weights[2] = (float) 23.89;
        perceptron5.weights[3] = (float) -33.78;
        perceptron5.weights[4] = (float) -19.25;
        perceptron5.weights[5] = (float) -12.42;
        perceptron5.weights[6] = (float) -16.34;
        perceptron5.weights[7] = (float) 21.56;
        perceptron5.weights[8] = (float) -0.459;

        float [][]fArray;
        DataUtils dataUtils = new DataUtils();
        fArray = dataUtils.readInputsFromFile(getApplicationContext()); // 인풋 파일 읽어오고...
        for(int i =0;i<500;i++){
            float result1 = perceptron1.feedforward(fArray[i]);
            float result2 = perceptron2.feedforward(fArray[i]);
            float result3 = perceptron3.feedforward(fArray[i]);
            float result4 = perceptron4.feedforward(fArray[i]);
            float result5 = perceptron5.feedforward(fArray[i]);

            Log.d(TAG, i+1 + "번째 데이터 예측");
            Log.d(TAG, "자세 1 확률 : " + result1);
            Log.d(TAG, "자세 2 확률 : " + result2);
            Log.d(TAG, "자세 3 확률 : " + result3);
            Log.d(TAG, "자세 4 확률 : " + result4);
            Log.d(TAG, "자세 5 확률 : " + result5);
        }

        /*
        // -7.61	-8.02	-0.607	36.5	-20.12	-5.47	27.56	14.11	0.788
        perceptron1.weights[0] = (float) -7.61;
        perceptron1.weights[1] = (float) -8.02;
        perceptron1.weights[2] = (float) -0.607;
        perceptron1.weights[3] = (float) 36.5;
        perceptron1.weights[4] = (float) -20.12;
        perceptron1.weights[5] = (float) -5.47;
        perceptron1.weights[6] = (float) 27.56;
        perceptron1.weights[7] = (float) 14.11;
        perceptron1.weights[8] = (float) 0.788;
        */


        /* 학습 예제
        float [][]fArray;
        int []iArray;
        DataUtils dataUtils = new DataUtils();
        fArray = dataUtils.readInputsFromFile(getApplicationContext()); // 인풋 파일 읽어오고...
        iArray = dataUtils.readOutputsFromFile(getApplicationContext());

        //Log.d(TAG, "학습 전 예측 : " + perceptron.feedforward(fArray[0]));

        for(int j = 0; j<2000; j++) {
            for (int i = 0; i < 500; i++) {
                perceptron1.train(fArray[i], iArray[i]);  // 학습을 돌려보장
                //perceptron1.train(fArray[i],iArray[i]);  // 학습을 돌려보장
                //perceptron1.train(fArray[i],iArray[i]);  // 학습을 돌려보장
            }
        }

        for(int i = 0; i<9; i++){
            Log.d(TAG, "가중치 " + i + " : " + perceptron1.weights[i]);
        }



        // 학습 후 결과
        // 테스트용 변수 count, sum;
        int count = 0;
        float sum = 0;

        for(int i = 0; i<500; i++){
            sum = sum + perceptron1.feedforward(fArray[i]);
            count ++;

            if(count == 100){
                Log.d(TAG, "평균 : " + sum/100);
                count = 0;
                sum = 0;
            }
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