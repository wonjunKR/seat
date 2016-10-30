package com.seat.sw_maestro.seat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by JiYun on 2016. 10. 14..
 */
public class AlarmReceiver extends BroadcastReceiver {
    final static String TAG = "AlarmReceiver";

    SharedPreferences queue_sittingTime;
    SharedPreferences queue_accuracy;
    SharedPreferences queue_timeLine;
    SharedPreferences queue_date;
    SharedPreferences queue_count;

    SharedPreferences.Editor editor_sittingTime;
    SharedPreferences.Editor editor_accuracy;
    SharedPreferences.Editor editor_timeLine;
    SharedPreferences.Editor editor_date;
    SharedPreferences.Editor editor_count;

    DatabaseManager databaseManager;
    Context globalContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        globalContext = context;

        // sharedPreference를 큐처럼 쓴다.
        queue_sittingTime = globalContext.getSharedPreferences("sittingTime", globalContext.MODE_PRIVATE);
        queue_accuracy = globalContext.getSharedPreferences("accuracy", globalContext.MODE_PRIVATE);
        queue_timeLine = globalContext.getSharedPreferences("timeLine", globalContext.MODE_PRIVATE);
        queue_date = globalContext.getSharedPreferences("date", globalContext.MODE_PRIVATE);
        queue_count = globalContext.getSharedPreferences("count", globalContext.MODE_PRIVATE);

        editor_sittingTime = queue_sittingTime.edit();
        editor_accuracy = queue_accuracy.edit();
        editor_timeLine = queue_timeLine.edit();
        editor_date = queue_date.edit();
        editor_count = queue_count.edit();

        //Log.d(TAG, "정각이 되었다!!!");

        /*
            정각이 된다면 서버로 로컬에 저장했던 데이터를 넘거야한다.
            하지만 서버가 꺼져있거나, 인터넷 연결이 되지 않았다면 오류가 발생할 것이다.
            따라서 매 정각이 되면 전 시간의 데이터를 큐에 넣고, 큐에 있는 데이터들을 서버로 전송을 시도한다.
            성공한다면 큐에서 비워내고, 실패한다면 큐에 저장하여 다음 정각에 시도한다.
         */

        // 가장 최근의 데이터를 가져와야 할 것이다. 예) 11시 3분에 실행됬다면 10(TimeLine)의 데이터, 00시 2분에 실행됬다면 전날의 23의 데이터
        databaseManager = new DatabaseManager(context);
        Calendar calendar = Calendar.getInstance();
        String timeLine;
        String date;

        // 테스트용 나중에 지워
        databaseManager.insertData(databaseManager.getCurrentHour(),calendar.get(Calendar.MINUTE),calendar.get(Calendar.HOUR),databaseManager.getCurrentDay());

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
            //Log.d(TAG, "AM or PM" + calendar.get(Calendar.AM_PM));

            if(calendar.get(Calendar.AM_PM) == 1){  // PM인 경우에는 12를 더해준다. 1은 PM
                tempTimeLine = tempTimeLine + 12;
            }

            tempTimeLine = tempTimeLine - 1;    // 하나 빼서 이전 시간을 구한다.
            timeLine = String.valueOf(tempTimeLine);    // String으로 변환

            // date
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            date = dateFormat.format(calendar.getTime());
        }

        Log.d(TAG, "서버로 날릴 TimeLine : " + timeLine);
        Log.d(TAG, "서버로 날릴 Date : " + date);

        int count = queue_count.getInt("count", 0); // 카운트를 임시로 빼온다. 없으면 0

        //Log.d(TAG, "중요 count : " + count);
        //Log.d(TAG, "중요 sittingTime : " + databaseManager.getSittingTime(timeLine,date));
        //Log.d(TAG, "중요 accuracy : " + databaseManager.getAccuracy(timeLine,date));

        editor_sittingTime.putInt(String.valueOf(count),databaseManager.getSittingTime(timeLine,date));
        editor_accuracy.putInt(String.valueOf(count),databaseManager.getAccuracy(timeLine,date));
        editor_date.putString(String.valueOf(count),date);
        editor_timeLine.putString(String.valueOf(count),timeLine);
        editor_count.putInt("count",count + 1); //카운트 값 증가

        editor_sittingTime.commit();
        editor_accuracy.commit();
        editor_date.commit();
        editor_timeLine.commit();
        editor_count.commit();

        notification(databaseManager.getSittingTime(timeLine,date), databaseManager.getAccuracy(timeLine,date));
        sendToServer();
    }

    public void sendToServer(){
        while(queue_count.getInt("count",-1) != 0) {

            int count = queue_count.getInt("count", 0); // 카운트를 임시로 빼온다. 없으면 0
            Log.d(TAG, "큐에 개수 : " + count);
            count = count - 1;  // 1을 빼는 이유는 카운트 값이 1일 때 그 값들은 0에 들어가있음 (배열이라 생각)

            // 데이터 등록 - mode = 5, params[0~4] = 유저아이디, 타임라인, 앉은시간, 정확도, 날짜
            HTTPManager httpManager = new HTTPManager();
            String params[] = new String[5];

            // 로그인 정보를 저장하기 위한 sharedPreferences
            SharedPreferences prefs_user = globalContext.getSharedPreferences("UserStatus", globalContext.MODE_PRIVATE);

            params[0] = prefs_user.getString("UserNumber", "-1");
            params[1] = queue_timeLine.getString(String.valueOf(count),"-1");
            params[2] = String.valueOf(queue_sittingTime.getInt(String.valueOf(count),-1));
            params[3] = String.valueOf(queue_accuracy.getInt(String.valueOf(count),-1));
            params[4] = queue_date.getString(String.valueOf(count),"-1");

            //Log.d(TAG, "중요2 count : " + count);
            //Log.d(TAG, "중요2 sittingTime : " + queue_sittingTime.getInt(String.valueOf(count), -1));
            //Log.d(TAG, "중요2 accuracy : " + queue_accuracy.getInt(String.valueOf(count),-1));

            Log.d(TAG, "UserNumber : " + params[0]);
            Log.d(TAG, "timeLine : " + params[1]);
            Log.d(TAG, "sittingTime : " + params[2]);
            Log.d(TAG, "accuracy : " + params[3]);
            Log.d(TAG, "date : " + params[4]);

            try {
                httpManager.useAPI(5, params);
                // 성공하면 큐에서 뺀다.
                Log.d(TAG, "통신 성공");
                editor_count.putInt("count",count); // 여기서 1을 안빼도 되는 이유는 위에서 뺐다.
                editor_count.commit();

            } catch (java.lang.NullPointerException e) {
                Log.e(TAG, "네트워크가 꺼져서 서버로 못 보냄, 혹은 서버가 꺼져있음.");
                return;
            }
        }
    }

    public void notification(int sittingTime, int accuracy){    // 매 정각 보여주는 notification
        //알림(Notification)을 관리하는 NotificationManager 얻어오기
        NotificationManager manager= (NotificationManager)globalContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //알림(Notification)을 만들어내는 Builder 객체 생성
        //만약 minimum SDK가 API 11 이상이면 Notification 클래스 사용 가능
        //한번에 여러개의 속성 설정 가능
        NotificationCompat.Builder builder= new NotificationCompat.Builder(globalContext)
                .setSmallIcon(R.drawable.ic_launcher)  //상태표시줄에 보이는 아이콘 모양
                .setTicker("자세의 결과를 확인하세요!")                  //알림이 발생될 때 잠시 보이는 글씨
                .setContentTitle("최근 1시간 동안의 자세")                                //알림창에서의 제목
                .setStyle(new NotificationCompat.BigTextStyle().bigText("앉은 시간 : " + sittingTime + "분\n"+ "정확도 : " + accuracy + "%"));



        //상태바를 드래그하여 아래로 내리면 보이는 알림창(확장 상태바)의 아이콘 모양 지정
        builder.setLargeIcon(BitmapFactory.decodeResource(globalContext.getResources(), R.drawable.ic_launcher));

        //알림에 사운드 기능 추가
        Uri soundUri= RingtoneManager.getActualDefaultRingtoneUri(globalContext, RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(soundUri);

        Notification notification= builder.build();    //Notification 객체 생성
        manager.notify(0, notification);    //NotificationManager가 알림(Notification)을 표시, id는 알림구분용
    }
}


