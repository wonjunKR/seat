package com.seat.sw_maestro.seat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FacebookLoginActivity extends AppCompatActivity {

    EditText editTextWeight;
    EditText editTextHeight;
    Spinner spinnerJob;
    Button buttonRegister;

    private static final String TAG = "FacebookLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        Log.d(TAG, "FacebookLoginActivity");

        //스피너 관련
        spinnerJob = (Spinner)findViewById(R.id.spinnerJob);
        //어댑터 생성
        ArrayAdapter adapterJob = ArrayAdapter.createFromResource(this, R.array.job, R.layout.spinner_layout);
        //스피너와 어댑터 연결
        spinnerJob.setAdapter(adapterJob);

        editTextWeight = (EditText)findViewById(R.id.editTextWeight);
        editTextHeight = (EditText)findViewById(R.id.editTextHeight);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "등록 버튼이 눌림");

                Intent intent = getIntent();
                String facebookID = intent.getExtras().getString("facebookID");
                String name = intent.getExtras().getString("name");
                String birthday = intent.getExtras().getString("birthday");

                Log.d(TAG, "facebookID : " + facebookID);
                Log.d(TAG, "name : " + name);
                Log.d(TAG, "birthday : " + birthday);

                String weight = editTextWeight.getText().toString();
                String height = editTextHeight.getText().toString();
                String job = spinnerJob.getSelectedItem().toString();

                String[] params = new String[6];
                String result;

                params[0] = name;   // 이름
                params[1] = birthday; // 나이
                params[2] = weight; // 체중
                params[3] = height; // 키
                params[4] = job;    // 직업
                params[5] = facebookID;   // facebook ID

                HTTPManager httpManager = new HTTPManager();
                result = httpManager.useAPI(1,params);  // 사용자 정보 등록, 결과로 DB에 생성된 UserNumber를 받아온다.
                Log.d(TAG, "사용자 정보 등록 : " + result);

                // 로그인 정보를 저장하기 위한 sharedPreferences
                SharedPreferences prefs = getSharedPreferences("UserStatus", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit(); 

                editor.putString("isLoggedIn", "true"); // 로그인 상태 true로
                editor.putString("UserNumber", result); // UserNumber 세팅
                editor.commit();

                //Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();

                startActivity(new Intent(getApplicationContext(), TabActivity.class));  // 다음으로 이동
                finish();
            }
        });
    }
}
