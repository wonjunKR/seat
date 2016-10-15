package com.seat.sw_maestro.seat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by JiYun on 2016. 10. 14..
 */
public class AlarmReceiver extends BroadcastReceiver {
    final static String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 여기에는 한시간마다 동작하는 것이 된다.
        // 이제까지 값을 정산해서 .. DB에 넣는다.
        Log.d(TAG, "정각이 되었다!!!");

        //테스트용 ******
        // 데이터베이스에 값을 넣는다.
        DatabaseManager databaseManager = new DatabaseManager(context);

        Calendar calendar = Calendar.getInstance();

        int sittingTime = calendar.get(calendar.SECOND);// 여기도 일단은 초로. 앉은 시간이 들어가야함
        int accuracy = calendar.get(calendar.MINUTE);   // 일단은 테스트로 현재 분을 넣는다. 정확도가 들어가야함

        String date = databaseManager.getCurrentDay();  // 현재의 날짜. 타입 -yyyyMMdd
        String timeLine = databaseManager.getCurrentHour(); // 현재의 시간. 타입 - HH

        // 데이터 추가하기
        databaseManager.insertData(timeLine,sittingTime,accuracy,date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        //테스트용 끝 ******

        // insertDatabase(context); 다른 부분이 되면 위를 지우고 이것을 쓰면 된다.
    }

    public void insertDatabase(Context context){   // 이제까지 받은 데이터를 데이터베이스에 넣는다.
        /*
        현재 앉은시간은 count값을 그대로 넣는다. 1분에 1번씩 방석에 요청한다고 가정하고 있으니...
        그게 아니라면 count값을 정제해서 바꾸면 됨.
        예) 30초마다 1번씩 요청한다면, 앉은 시간은 count / 2로 하면 되겠지? 1분에 2번, 60분에 120번이 가능하니...
         */

        // 앉은 데이터를 저장하기 위한 sharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("SittingData", context.MODE_PRIVATE);

        int count = prefs.getInt("count",0);
        int accuracy = prefs.getInt("accuracy", 0);

        if(count == 0) return;  // count가 0이라면 받은 데이터가 없다.(앉지 않았다. 그냥 종료해도 됨)
        else {  // 받은 데이터가 있는 경우
            accuracy = accuracy / count;    // 정확도는 평균을 구해서 넣어야함.

            // 데이터베이스에 값을 넣는다.
            DatabaseManager databaseManager = new DatabaseManager(context);

            String date = databaseManager.getCurrentDay();  // 현재의 날짜. 타입 -yyyyMMdd
            String timeLine = databaseManager.getCurrentHour(); // 현재의 시간. 타입 - HH

            // 데이터 추가하기
            databaseManager.insertData(timeLine, count, accuracy, date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜

            // sharedPreference에 들어있던 값들 초기화
            clearSharedPreference(context);
        }
    }

    public void clearSharedPreference(Context context){
        // 앉은 데이터를 저장하기 위한 sharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("SittingData", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("count", 0);
        editor.putInt("accuracy", 0);
        editor.commit();
    }
}


