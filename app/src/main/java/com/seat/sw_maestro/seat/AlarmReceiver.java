package com.seat.sw_maestro.seat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

        // 데이터베이스에 값을 넣는다.
        DatabaseManager databaseManager = new DatabaseManager(context);

        Calendar calendar = Calendar.getInstance();

        int sittingTime = calendar.get(calendar.SECOND);// 여기도 일단은 초로. 앉은 시간이 들어가야함
        int accuracy = calendar.get(calendar.MINUTE);   // 일단은 테스트로 현재 분을 넣는다. 정확도가 들어가야함

        String date = databaseManager.getCurrentDay();  // 현재의 날짜. 타입 -yyyyMMdd
        String timeLine = databaseManager.getCurrentHour(); // 현재의 시간. 타입 - HH

        // 데이터 추가하기
        databaseManager.insertData(timeLine,sittingTime,accuracy,date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
    }
}
