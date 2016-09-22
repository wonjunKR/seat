package com.seat.sw_maestro.seat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class SettingActivity extends AppCompatActivity {

    ImageButton buttonBack;
    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        buttonBack = (ImageButton)findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "뒤로가기 버튼이 눌림");
                finish();
            }
        });
    }
}
