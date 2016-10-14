package com.seat.sw_maestro.seat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by JiYun on 2016. 10. 14..
 */
public class AlarmReceiver extends BroadcastReceiver {
    final static String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 여기에는 한시간마다 동작하는 것이 된다.
        // 이제까지 값을 정산해서 .. DB에 넣어야겠지


        Log.d(TAG, "정각이 되었다!!!");
        Toast.makeText(context, "안녕 정각이 되었옹", Toast.LENGTH_LONG).show();
    }
}
