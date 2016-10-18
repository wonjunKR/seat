package com.seat.sw_maestro.seat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by JiYun on 2016. 10. 14..
 */
public class AlarmReceiver extends BroadcastReceiver {
    final static String TAG = "AlarmReceiver";
    Queue queue_sittingTime = new LinkedList();
    Queue queue_accuracy = new LinkedList();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        //Log.d(TAG, "정각이 되었다!!!");

        /*
            정각이 된다면 서버로 로컬에 저장했던 데이터를 넘거야한다.
            하지만 서버가 꺼져있거나, 인터넷 연결이 되지 않았다면 오류가 발생할 것이다.
            따라서 매 정각이 되면 전 시간의 데이터를 큐에 넣고, 큐에 있는 데이터들을 서버로 전송을 시도한다.
            성공한다면 큐에서 비워내고, 실패한다면 큐에 저장하여 다음 정각에 시도한다.
         */

        // 가장 최근의 데이터를 가져와야 할 것이다. 예) 11시 3분에 실행됬다면 10(TimeLine)의 데이터, 00시 2분에 실행됬다면 전날의 23의 데이터
        DatabaseManager databaseManager = new DatabaseManager(context);
        Calendar calendar = Calendar.getInstance();
        String timeLine;
        String date;

        //calendar.set(2016, 10, 1, 00, 34, 56);  // 테스트용
        //Log.d(TAG, "현재 날짜 : " + calendar.getTime());

        // 예) 2016년 10월 01일이다.
        // 이 함수가 실행된 시간이 09시 03분이라면 date = 20161001, timeLine = "08" 을 얻어내야함.
        // 20161001 00:03 이라면 date = 20160930, timeLine = "23"을 얻어내야함.
        if(calendar.get(Calendar.HOUR) == 0){   // 알람이 울린 시각이 00시인 경우.
            // timeLine
            //Log.d(TAG, "하루 이전 날짜" + calendar.getTime());
            timeLine = "23";    // 정각인 경우에는 timeLine은 23시를 얻고

            // date
            calendar.add(Calendar.DATE, - 1); // 날짜를 전날으로
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            date = dateFormat.format(calendar.getTime()); // 위에서 하루전 날짜로 바꾸었다. 이 날을 얻어옴
        }else{  // 정각이 아닌 경우
            // timeLine
            int tempTimeLine = calendar.get(Calendar.HOUR);  // 현재 시간을 int로 구함
            tempTimeLine = tempTimeLine - 1;    // 하나 빼서 이전 시간을 구한다.
            timeLine = String.valueOf(tempTimeLine);    // String으로 변환

            // date
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            date = dateFormat.format(calendar.getTime());
        }

        Log.d(TAG, "서버로 날릴 TimeLine : " + timeLine);
        Log.d(TAG, "서버로 날릴 Date : " + date);

        //queue_sittingTime.offer(sittingTime);
        //queue_accuracy.offer(accuracy);



        /*
        // 데이터베이스에 값을 넣는다.
        DatabaseManager databaseManager = new DatabaseManager(context);

        Calendar calendar = Calendar.getInstance();

        int sittingTime = calendar.get(calendar.SECOND);// 여기도 일단은 초로. 앉은 시간이 들어가야함
        int accuracy = calendar.get(calendar.MINUTE);   // 일단은 테스트로 현재 분을 넣는다. 정확도가 들어가야함

        String date = databaseManager.getCurrentDay();  // 현재의 날짜. 타입 -yyyyMMdd
        String timeLine = databaseManager.getCurrentHour(); // 현재의 시간. 타입 - HH

        // 데이터 추가하기
        databaseManager.insertData(timeLine,sittingTime,accuracy,date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        */
    }

}


