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


        // 1111111111111111111111
        // -1.29	-4.56	0.475	-13.803	-8.553	-1.134	7.945	9.411	1.5944
        perceptron1.weights[0] = (float) -1.29;
        perceptron1.weights[1] = (float) -4.56;
        perceptron1.weights[2] = (float) 0.475;
        perceptron1.weights[3] = (float) -13.803;
        perceptron1.weights[4] = (float) -8.553;
        perceptron1.weights[5] = (float) -1.134;
        perceptron1.weights[6] = (float) 7.945;
        perceptron1.weights[7] = (float) 9.411;
        perceptron1.weights[8] = (float) 1.5944;
        //-0.063	-1.508	-0.8172	5.765	0.7297	-5.76	4.73	-3.13	-3.07
        perceptron2.weights[0] = (float) -0.063;
        perceptron2.weights[1] = (float) -1.508;
        perceptron2.weights[2] = (float) -0.8172;
        perceptron2.weights[3] = (float) 5.765;
        perceptron2.weights[4] = (float) 0.7297;
        perceptron2.weights[5] = (float) -5.76;
        perceptron2.weights[6] = (float) 4.73;
        perceptron2.weights[7] = (float) -3.13;
        perceptron2.weights[8] = (float) -3.07;
        //-6.971	1.698	-0.397	-0.249	1.058	3.573	-6.608	-3.224	6.282
        perceptron3.weights[0] = (float) -6.971;
        perceptron3.weights[1] = (float) 1.698;
        perceptron3.weights[2] = (float) -0.397;
        perceptron3.weights[3] = (float) -0.249;
        perceptron3.weights[4] = (float) 1.058;
        perceptron3.weights[5] = (float) 3.573;
        perceptron3.weights[6] = (float) -6.608;
        perceptron3.weights[7] = (float) -3.224;
        perceptron3.weights[8] = (float) 6.282;
        //-0.6376	4.043	-4.448	0.2408	-0.7218	1.321	0.094	0.4255	-4.001
        perceptron4.weights[0] = (float) -0.6376;
        perceptron4.weights[1] = (float) 4.043;
        perceptron4.weights[2] = (float) -4.448;
        perceptron4.weights[3] = (float) 0.2408;
        perceptron4.weights[4] = (float) -0.7218;
        perceptron4.weights[5] = (float) 1.321;
        perceptron4.weights[6] = (float) 0.094;
        perceptron4.weights[7] = (float) 0.4255;
        perceptron4.weights[8] = (float) -4.001;
        //2.76	0.1593	1.5971	-1.565	0.0531	0.5649	-3.134	-0.5655	1.9782
        perceptron5.weights[0] = (float) 2.76;
        perceptron5.weights[1] = (float) 0.1593;
        perceptron5.weights[2] = (float) 1.5971;
        perceptron5.weights[3] = (float) -1.565;
        perceptron5.weights[4] = (float) 0.0531;
        perceptron5.weights[5] = (float) 0.5649;
        perceptron5.weights[6] = (float) -3.134;
        perceptron5.weights[7] = (float) -0.5655;
        perceptron5.weights[8] = (float) 1.9782;

        float [][]fArray;
        DataUtils dataUtils = new DataUtils();
        fArray = dataUtils.readInputsFromFile(getApplicationContext()); // 인풋 파일 읽어오고...

        int error_count = 0;

        for(int i =0;i<500;i++){
            float result1 = perceptron1.feedforward(fArray[i]);
            float result2 = perceptron2.feedforward(fArray[i]);
            float result3 = perceptron3.feedforward(fArray[i]);
            float result4 = perceptron4.feedforward(fArray[i]);
            float result5 = perceptron5.feedforward(fArray[i]);

            Log.d(TAG, i+1 + "번째 데이터 예측");

            float[] input = new float[5];
            input[0] = result1;
            input[1] = result2;
            input[2] = result3;
            input[3] = result4;
            input[4] = result5;
            Log.d(TAG, "이 자세는 " + (getMax(input) + 1) + "번 자세입니다.");
            if(((i/100) + 1) != (getMax(input) + 1)) error_count++;

            //Log.d(TAG, "자세 1 확률 : " + result1);
            //Log.d(TAG, "자세 2 확률 : " + result2);
            //Log.d(TAG, "자세 3 확률 : " + result3);
            //Log.d(TAG, "자세 4 확률 : " + result4);
            //Log.d(TAG, "자세 5 확률 : " + result5);
        }

        Log.d(TAG, "에러 카운트 : " + error_count);

        float [][] test;
        test = new float[10][9];
        // 0.14,0,0.18,0.34,0.22,0.46,0.62,0.68,0.8
        // 0.18,0.2,0.26,0.38,0.22,0.48,0.66,0.72,0.8
        test[0][0] = (float)0.18;
        test[0][1] = (float)0.2;
        test[0][2] = (float)0.28;
        test[0][3] = (float)0.38;
        test[0][4] = (float)0.22;
        test[0][5] = (float)0.48;
        test[0][6] = (float)0.66;
        test[0][7] = (float)0.72;
        test[0][8] = (float)0.8;

        Log.d(TAG, "테스트 결과 1 : " + perceptron1.feedforward(test[0]));
        Log.d(TAG, "테스트 결과 2 : " + perceptron2.feedforward(test[0]));
        Log.d(TAG, "테스트 결과 3 : " + perceptron3.feedforward(test[0]));
        Log.d(TAG, "테스트 결과 4 : " + perceptron4.feedforward(test[0]));
        Log.d(TAG, "테스트 결과 5 : " + perceptron5.feedforward(test[0]));
        // 1111111111111111111111



        /*
        // 22222222222222222222222
        // -5.231	1.2477	0.1274	-0.4975	0.5438	2.934	-5.5332	-2.428	5.504
        perceptron1.weights[0] = (float) -5.231;
        perceptron1.weights[1] = (float) 1.2477;
        perceptron1.weights[2] = (float) 0.1274;
        perceptron1.weights[3] = (float) -0.4975;
        perceptron1.weights[4] = (float) 0.5438;
        perceptron1.weights[5] = (float) 2.934;
        perceptron1.weights[6] = (float) -5.5322;
        perceptron1.weights[7] = (float) -2.428;
        perceptron1.weights[8] = (float) 5.504;


        float [][]fArray;
        int []iArray;
        DataUtils dataUtils = new DataUtils();
        fArray = dataUtils.readInputsFromFile(getApplicationContext()); // 인풋 파일 읽어오고...
        iArray = dataUtils.readOutputsFromFile(getApplicationContext());

        //Log.d(TAG, "학습 전 예측 : " + perceptron.feedforward(fArray[0]));

        for(int j = 0; j<5; j++) {
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

        Log.d(TAG,"정상테스트5");
        // 22222222222222222222222
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

    public int getMax(float[] input){
        int maxIndex = 0;
        float max = 0;

        for(int i = 0; i < input.length; i++){
            if(input[i] > max){ // 여러개 인풋 중에서 가장 컸던 부분의 인덱스를 리턴한다. input[0]~[5] -> 0~5 리턴
                max = input[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}