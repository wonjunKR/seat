package com.seat.sw_maestro.seat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
요렇게 사용해. 나중에 좀 더 추가하자
DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
        databaseManager.insertData(1,53,78,20160925);
        databaseManager.selectData();
 */

//DB를 총괄관리
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
            // DataNumber(자동으로 증가) | TimeLine(int) | SittingTime(int) | Accuracy(int) | Date(int 예: 20160925)
            String createSql = "create table datas(" + "DataNumber integer primary key autoincrement,"
                    + "TimeLine integer," + "SittingTime integer," + "Accuracy integer," + "Date integer);";
            arg0.execSQL(createSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
        }
    }

    // 데이터 추가   인자 순서 기억!!
    public void insertData(int timeLine, int sittingTime, int accuracy, int date){
        String sql = "insert into " + tableName + " values( NULL," + timeLine + ", "
                + sittingTime + ", " + accuracy + ", "
                + date + ");";
        db.execSQL(sql);
    }

    // 데이터 검색
    public void selectData() {
        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용
        Cursor cursor = db.query("datas", null, null, null, null, null, null);

        // * 위 결과는 select * from datas 가 된다. Cursor는 DB결과를 저장한다. public Cursor
        // * query (String table, String[] columns, String selection, String[]
        // * selectionArgs, String groupBy, String having, String orderBy)

        while (cursor.moveToNext()) {
            // 하나씩 내려가며 로그캣에 출력
            Log.d(TAG, "DataNumber : " + cursor.getInt(cursor.getColumnIndex("DataNumber")));
            Log.d(TAG, "TimeLine : " + cursor.getInt(cursor.getColumnIndex("TimeLine")));
            Log.d(TAG, "SittingTime : " + cursor.getInt(cursor.getColumnIndex("SittingTime")));
            Log.d(TAG, "Accuracy : " + cursor.getInt(cursor.getColumnIndex("Accuracy")));
            Log.d(TAG, "Date : " + cursor.getInt(cursor.getColumnIndex("Date")));
        }
    }
}
