package com.seat.sw_maestro.seat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/*

// 이 클래스는 DB를 총괄관리
// 디비에 데이터를 주고 받고
// 디비에 저장된 데이터를 조합해서 그래프에 뿌려줄 값들을 만들어준다. MakeTimeDatas, MakeAccuracyDatas

이렇게 사용한다.
        // 초기화
        DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());

        // 시간정보얻기
        databaseMaanger.getCurrentTime(); // 현재의 시각. 타입 - yyyyMMdd HH:mm:ss
        databaseManager.getCurrentDay();  // 현재의 날짜. 타입 -yyyyMMdd
        databaseManager.getCurrentHour(); // 현재의 시간. 타입 - HH
        databaseManager.getCurrentYear(); // 현재의 연도. 타입 - yyyy
        databaseManager.getCurrentMonth();// 현재의 월. 타입 - MM

        // 데이터 추가하기
        databaseManager.insertData(timeLine,53,78,date);    // 인자로 현재 시간, 앉은시간(분), 정확도(퍼센트 인트), 현재날짜
        databaseManager.insertData("0",13,78,"20160927");

        // 데이터 얻기
        databaseManager.getSittingTime("15","20160925"));   // 2016년 9월 25일의 15~16시에 해당하는 앉은 시간 리턴
        databaseManager.getAccuracy("15","20160925"));      // 이것은 정확도

        databaseManager.getSittingTime_OneDay("20160925")); // 2016년 9월 25일의 0시~24시까지 않았던 시간을 리턴
        databaseManager.getAccuracy_OneDay("20160926"));    // 이것은 정확도. 앉은 시간과 다른점은 평균을 계산하여 리턴

        databaseManager.getSittingTime_Month("201609"); // 2016년 9월의 1일~31일 전체 앉았던 시간을 리턴. 인자 타입 yyyyMM에 주의
        databaseManager.getAccuracy_Month("201609"); // 이것은 정확도. 앉은 시간과 다르게 평균을 계산해서 리턴

        // 그래프에 뿌려줄 데이터 만들기...
        databaseManager.makeTimeDatas_OneDay(date);         // 통계기간 '일' 선택하면 0~24시간 데이터 배열 리턴. getSittingTime 사용
        databaseManager.makeAccuracyDatas_OneDay(date);     // 통계기간 '일' 선택하면 0~24시간 데이터 배열 리턴. getAccuracy 사용

        databaseManager.makeTimeDatas_Month();  // 통계기간 '월' 선택하면 1일 ~ 31일 데이터 배열 리턴. getSittingTime_OneDay 사용
        databaseManager.makeAccuracyDatas_Month();  // 통계기간 '월' 선택하면 1일 ~ 31일 데이터 배열 리턴. getAccuracy_OneDay 사용

        databaseManager.makeTimeDatas_Year(); // 통계기간 '연' 선택하면 1월 ~ 12월 데이터 배열 리턴. getSittingTime_Month 사용
        databaseManager.makeAccuracyDatas_Year(); // 통계기간 '연' 선택하면 1월 ~ 12월 데이터 배열 리턴. getAccuracy_Month 사용
 */

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    // DB관련 상수 선언
    private static final String dbName = "datas.db";
    private static final String tableName = "datas";
    public static final int dbVersion = 1;

    // DB관련 객체 선언
    private OpenHelper opener; // DB opener
    private SQLiteDatabase db; // DB controller

    // 부가적인 객체들
    private Context context;

    // 생성자
    public DatabaseManager(Context context) {
        this.context = context;
        this.opener = new OpenHelper(context, dbName, null, dbVersion);
        db = opener.getWritableDatabase();
    }

    // Opener of DB and Table
    private class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
            super(context, name, null, version);
            // TODO Auto-generated constructor stub
        }

        // 생성된 DB가 없을 경우에 한번만 호출됨
        @Override
        public void onCreate(SQLiteDatabase arg0) {
            Log.d(TAG, "디비생성");
            // DataNumber(자동으로 증가) | TimeLine(String) | SittingTime(int) | Accuracy(int) | Date(String 예: 20160925)
            String createSql = "create table " + tableName + "(" + "DataNumber integer primary key autoincrement,"
                    + "TimeLine text," + "SittingTime integer," + "Accuracy integer," + "Date text);";
            arg0.execSQL(createSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
        }
    }

    // 데이터 추가   인자 순서 기억!!
    public void insertData(String timeLine, int sittingTime, int accuracy, String date){
        /* 원본
        String sql = "insert into " + tableName + " values( NULL," + timeLine + ", "
                + sittingTime + ", " + accuracy + ", "
                + date + ");";
        db.execSQL(sql);
        */

        String sql_count = "SELECT count(*) FROM " + tableName + " WHERE Date = " + date + " AND timeLine = " + timeLine + ";";
        //Log.d(TAG, "sql : " + sql_count);
        Cursor result_count = db.rawQuery(sql_count, null);

        if(result_count.moveToFirst()){
            int count = result_count.getInt(0);
            Log.d(TAG, "조회 결과 개수 : " + count);
            if(count == 0){ // 이미 해당 날짜와 TimeLine에 데이터가 있는지 조회해보고, 없다면 새로 추가한다.
                String sql = "insert into " + tableName + " values( NULL," + timeLine + ", "
                        + sittingTime + ", " + accuracy + ", "
                        + date + ");";
                db.execSQL(sql);
            }else{ // 있다면 값을 수정해서 올린다.
                Log.d(TAG, "이미 데이터는 들어있음. 수정이 필요함");
            }
        }

        result_count.close();
        /*
        // 정확도의 경우에는 앉은 시간과는 다르게 평균을 내야한다. 그래서 가져온 더한 값을 나눠줘야겠지?
        int accuracy_OneDay = 0;
        int count = 1;

        String sql_sum = "select sum(Accuracy) AS 'sumOfAccuracy' from " + tableName + " where Date = " + date + ";";
        String sql_count = "SELECT count(Accuracy) FROM " + tableName + " WHERE Date = " + date + ";";
        Cursor result_sum = db.rawQuery(sql_sum, null);
        Cursor result_count = db.rawQuery(sql_count, null);

        if(result_sum.moveToFirst()){
            accuracy_OneDay = result_sum.getInt(0);
        }

        if(result_count.moveToFirst()){
            count = result_count.getInt(0);
            if(count == 0)  // 개수가 없는 경우에 한에서는 1로 나누기로 한다. 어짜피 분모가 0이니까 0이 나오겠지만.. 0으로 나누는 오류를 제거하기 위해
                count = 1;
            //Log.d(TAG, "카운트 값 : " + count);
        }

        result_sum.close();
        result_count.close();

        return accuracy_OneDay/count;
        */
    }

    // 데이터 검색
    public void selectData() {
        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);

        // * 위 결과는 select * from datas 가 된다. Cursor는 DB결과를 저장한다. public Cursor
        // * query (String table, String[] columns, String selection, String[]
        // * selectionArgs, String groupBy, String having, String orderBy)

        while (cursor.moveToNext()) {
            // 하나씩 내려가며 로그캣에 출력
            Log.d(TAG, "DataNumber : " + cursor.getInt(cursor.getColumnIndex("DataNumber")));
            Log.d(TAG, "TimeLine : " + cursor.getInt(cursor.getColumnIndex("TimeLine")));
            Log.d(TAG, "SittingTime : " + cursor.getInt(cursor.getColumnIndex("SittingTime")));
            Log.d(TAG, "Accuracy : " + cursor.getInt(cursor.getColumnIndex("Accuracy")));
            Log.d(TAG, "Date : " + cursor.getString(cursor.getColumnIndex("Date")));
        }

        cursor.close();
    }

    public String getCurrentTime(){
        String time = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Log.d(TAG, "현재시간 : " +  time);
        return time;
    }

    public String getCurrentHour(){
        String hour = new SimpleDateFormat("HH").format(new Date(System.currentTimeMillis()));
        //Log.d(TAG, "현재시간 : " +  hour);
        return hour;
    }

    public String getCurrentYear(){
        String year = new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis()));
        Log.d(TAG, "현재 연 : " +  year);
        return year;
    }

    public String getCurrentMonth(){
        String month = new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis()));
        Log.d(TAG, "현재 월 : " +  month);
        return month;
    }

    public String getCurrentDay(){
        String day = new SimpleDateFormat("yyyyMMdd").format(new Date(System.currentTimeMillis()));
        //Log.d(TAG, "현재날짜 : " +  day);
        return day;
    }

    public int getSittingTime(String timeLine, String date){    // timeLine과 date에 해당하는 SittingTime을 리턴함.
        // 여기가 문제구먼
        String sql = "select SittingTime from " + tableName + " where TimeLine = " + timeLine + " AND Date = " + date + ";";
        Cursor result = db.rawQuery(sql, null);
        int sittingTime;

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){
            //while(result.moveToNext()) {  // 이론적으로 내려가면서 할 필요는 없겠지. 우리의 계획상으로는 디비에 타임라인, 시간이 같은 것이 중복되서 들어올 수 없어.
            sittingTime = result.getInt(0);
            //Log.d(TAG, "date : " + date + " timeLine : " + timeLine + " 앉은시간 : " + sittingTime);
            //}
        }
        else{
            //Log.d(TAG, "데이터가 존재하지 않음. 0을 리턴하겠습니다.");
            sittingTime = 0;
        }
        result.close();

        return sittingTime;
    }

    public int getAccuracy(String timeLine, String date){    // timeLine과 date에 해당하는 Accuracy을 리턴함.
        String sql = "select Accuracy from " + tableName + " where TimeLine = " + timeLine + " AND Date = " + date + ";";
        Cursor result = db.rawQuery(sql, null);
        int accuracy;

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){
            accuracy = result.getInt(0);
        }
        else{
            //Log.d(TAG, "데이터가 존재하지 않음. 0을 리턴하겠습니다.");
            accuracy = 0;
        }
        result.close();

        return accuracy;
    }

    /* 속도가 많이 느린 버전... 비효율적임 일 계산을 하나씩 가져와서 하니까 .. 이거 쓰지 말것. 디비에서 계산해서 가져오는 아래가 더 빠름
    public int getSittingTime_OneDay(String date){  // 이것은 date를 넣으면 그 하루동안 앉았던 sittingTime을 더해서 리턴해준다.
        int sittingTime_OneDay = 0;
        for(int i = 0; i<24; i++){  // 타임라인은 0부터 23까지 존재하니까 0~23까지 루프를 돌면 되겠음.
            String numberToString;  // int -> string 변환을 해줘야 getSitiingTime 함수를 부를 수 있음.
            if(i<10){
                numberToString = String.format ("%01d", i);    // 10 이하의 경우
            }
            else{
                numberToString = String.format ("%02d", i);    // 이상의 경우
            }
            sittingTime_OneDay += getSittingTime(numberToString,date); // date 날짜 동안의 타임라인에 해당하는 sittingTime 값을 다 더하면 하루의 값이 됨
        }
        return sittingTime_OneDay;
    }
    */

    public int getSittingTime_OneDay(String date){  // 이것은 date를 넣으면 그 하루동안 앉았던 sittingTime을 더해서 리턴해준다. 월 데이터에서 부분 사용
        int sittingTime_OneDay = 0;

        String sql = "select sum(SittingTime) AS 'sumOfSittingTime' from " + tableName + " where Date = " + date + ";";
        Cursor result = db.rawQuery(sql, null);

        if(result.moveToFirst()){
            sittingTime_OneDay = result.getInt(0);
        }
        result.close();
        //Log.d(TAG, "오늘 앉은 시간 " + sittingTime_OneDay);

        return sittingTime_OneDay;
    }

    /* 이것도 옛날 버전 쓰지말자.
    public int getAccuracy_OneDay(String date){  // 이것은 date를 넣으면 그 하루동안의 Accuracy를 더해서 리턴해준다.
        int accuracy_OneDay = 0;
        for(int i = 0; i<24; i++){  // 타임라인은 0부터 23까지 존재하니까 0~23까지 루프를 돌면 되겠음.
            String numberToString;
            if(i<10){
                numberToString = String.format ("%01d", i);    // 10 이하의 경우
            }
            else{
                numberToString = String.format ("%02d", i);    // 이상의 경우
            }
            accuracy_OneDay += getAccuracy(numberToString,date); // date 날짜 동안의 타임라인에 해당하는 accuracy 값을 다 더하면 하루의 값이 됨
        }
        return accuracy_OneDay;
    }
    */

    public int getAccuracy_OneDay(String date){  // 이것은 date를 넣으면 그 하루동안 앉았던 sittingTime을 더해서 리턴해준다.
        // 정확도의 경우에는 앉은 시간과는 다르게 평균을 내야한다. 그래서 가져온 더한 값을 나눠줘야겠지?
        int accuracy_OneDay = 0;
        int count = 1;

        String sql_sum = "select sum(Accuracy) AS 'sumOfAccuracy' from " + tableName + " where Date = " + date + ";";
        String sql_count = "SELECT count(Accuracy) FROM " + tableName + " WHERE Date = " + date + ";";
        Cursor result_sum = db.rawQuery(sql_sum, null);
        Cursor result_count = db.rawQuery(sql_count, null);

        if(result_sum.moveToFirst()){
            accuracy_OneDay = result_sum.getInt(0);
        }

        if(result_count.moveToFirst()){
            count = result_count.getInt(0);
            if(count == 0)  // 개수가 없는 경우에 한에서는 1로 나누기로 한다. 어짜피 분모가 0이니까 0이 나오겠지만.. 0으로 나누는 오류를 제거하기 위해
                count = 1;
            //Log.d(TAG, "카운트 값 : " + count);
        }

        result_sum.close();
        result_count.close();

        return accuracy_OneDay/count;
    }

    public float[] makeTimeDatas_OneDay(String date){ // 그래프에 보여줄 하루 동안의 데이터들 (통계기간 일 선택했을 때) 0~24시 1시간 간격
        float[] timeDatas = new float[24];
        for(int i = 0; i<24; i++){
            String numberToString;
            if(i<10){
                numberToString = String.format ("%01d", i);    // 10 이하의 경우
            }
            else{
                numberToString = String.format ("%02d", i);    // 이상의 경우
            }

            timeDatas[i] = getSittingTime(numberToString,date); // 함수 리턴은 int형이지만 크게 상관 없을듯..
        }
        return timeDatas;
    }

    public float[] makeAccuracyDatas_OneDay(String date){ // 그래프에 보여줄 하루 동안의 데이터들 (통계기간 일 선택했을 때)
        float[] accuracyDatas = new float[24];
        for(int i = 0; i<24; i++){
            String numberToString;
            if(i<10){
                numberToString = String.format ("%01d", i);    // 10 이하의 경우
            }
            else{
                numberToString = String.format ("%02d", i);    // 이상의 경우
            }

            accuracyDatas[i] = getAccuracy(numberToString,date); // 함수 리턴은 int형이지만 크게 상관 없을듯..
        }
        return accuracyDatas;
    }

    public float[] makeTimeDatas_Month(){ // 그래프에 보여줄 이번 1달 동안의 데이터들 (통계기간 월 선택했을 때) 1일~31일
        float[] timeDatas = new float[31];
        // 31일 동안... getSittingTime_OneDay(date)를 사용해야겠지??
        // 그러기 위해서는 현재 월을 받아와서 yyyymm01 ~ yyyymm31까지 for문을 돌려서 넣어.
        String year = getCurrentYear();
        String month = getCurrentMonth();
        String day;
        for(int i = 1; i <= 31; i++){
            if(i<10){
                day = String.format ("%02d", i);    // 10 이하의 경우
            }
            else{
                day = String.format ("%02d", i);    // 이상의 경우
            }
            String date = year + month + day;   // 날짜를 조합했음.

            int data = getSittingTime_OneDay(date);    // 그 날짜의 하루 데이터를 다 더한다.
            timeDatas[i-1] = data;
        }
        return timeDatas;
    }

    public float[] makeAccuracyDatas_Month(){ // 그래프에 보여줄 이번 달 1달 동안의 데이터들 (통계기간 월 선택했을 때)
        float[] accuracyDatas = new float[31];
        // 31일 동안... getAccuracy_OneDay(date)를 사용해야겠지??
        // 그러기 위해서는 현재 월을 받아와서 yyyymm01 ~ yyyymm31까지 for문을 돌려서 넣어.
        String year = getCurrentYear();
        String month = getCurrentMonth();
        String day;
        for(int i = 1; i <= 31; i++){
            if(i<10){
                day = String.format ("%02d", i);    // 10 이하의 경우
            }
            else{
                day = String.format ("%02d", i);    // 이상의 경우
            }
            String date = year + month + day;   // 날짜를 조합했음.
            int data = getAccuracy_OneDay(date);    // 그 날짜의 하루 데이터를 다 더한다.
            accuracyDatas[i-1] = data;
        }
        return accuracyDatas;
    }

    public float[] makeTimeDatas_Year(){ // 그래프에 보여줄 1년 동안의 데이터들 (통계기간 연 선택했을 때) 1월~12월
        float[] timeDatas = new float[12];

        String year = getCurrentYear();
        String month;

        for(int i = 1; i <= 12; i++){
            month = String.format ("%02d", i);  // 형식이 yyyymm 이기 떄문에 mm은 항상 두 자리로 해야함.
            String date = year + month;   // 날짜를 조합했음. 예) 201609  일은 필요없음.
            int data = getSittingTime_Month(date);    // 그 날짜의 하루 데이터를 다 더한다.
            timeDatas[i-1] = data;
        }
        return timeDatas;
    }

    public int getSittingTime_Month(String date){   // date를 받음. 형식은(yyyymm) 해당 연, 월의 데이터를 다 더해서 리턴한다.
        int sittingTime_Month = 0;
        // Date Like yyyymm% 로 조회하면 뒤에 일에 상관없이 다 값들을 더해오겠지?
        String sql = "select sum(SittingTime) AS 'sumOfSittingTime' from " + tableName + " where Date Like '" + date + "%'" + ";";
        //Log.d(TAG, "query : " + sql);
        Cursor result = db.rawQuery(sql, null);

        if(result.moveToFirst()){
            sittingTime_Month = result.getInt(0);
            //Log.d(TAG, "getSittingTime_Month - " + sittingTime_Month);
        }
        result.close();

        return sittingTime_Month;
    }

    public float[] makeAccuracyDatas_Year(){ // 그래프에 보여줄 1년 동안의 데이터들 (통계기간 연 선택했을 때) 1월~12월
        float[] accuracyDatas = new float[12];

        String year = getCurrentYear();
        String month;

        for(int i = 1; i <= 12; i++){
            month = String.format ("%02d", i);  // 형식이 yyyymm 이기 떄문에 mm은 항상 두 자리로 해야함.
            String date = year + month;   // 날짜를 조합했음. 예) 201609  일은 필요없음.
            int data = getAccuracy_Month(date);    // 그 날짜의 하루 데이터를 다 더한다.
            accuracyDatas[i-1] = data;
        }
        return accuracyDatas;
    }

    public int getAccuracy_Month(String date){   // date를 받음. 형식은(yyyymm) 해당 연, 월의 데이터를 다 더해서 리턴한다.
        int accuracy_Month = 0;
        int count = 0;

        // Accuracy Like yyyymm% 로 조회하면 뒤에 일에 상관없이 다 값들을 더해오겠지?
        String sql_sum = "select sum(Accuracy) AS 'sumOfAccuracy' from " + tableName + " where Date Like '" + date + "%'" + ";";
        String sql_count = "SELECT count(Accuracy) FROM " + tableName + " WHERE Date Like '" + date + "%'" + ";";
        Cursor result_sum = db.rawQuery(sql_sum, null);
        Cursor result_count = db.rawQuery(sql_count, null);

        if(result_sum.moveToFirst()){
            accuracy_Month = result_sum.getInt(0);
            //Log.d(TAG, "accuracy_Month - " + accuracy_Month);
        }

        if(result_count.moveToFirst()){
            count = result_count.getInt(0);
            if(count == 0)  // 개수가 없는 경우에 한에서는 1로 나누기로 한다. 어짜피 분모가 0이니까 0이 나오겠지만.. 0으로 나누는 오류를 제거하기 위해
                count = 1;
            //Log.d(TAG, "카운트 값 : " + count);
        }

        result_sum.close();
        result_count.close();

        return accuracy_Month/count;
    }
}
