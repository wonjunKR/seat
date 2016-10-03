package com.seat.sw_maestro.seat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Tutorial1Activity extends AppCompatActivity {

    Button buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial1);

        // 만약에 이전 액티비티 살아있다면 죽이기
        LoginActivity loginActivity = (LoginActivity)LoginActivity.LoginActivity;
        MainActivity mainActivity = (MainActivity)MainActivity.MainActivity;
        FacebookLoginActivity facebookLoginActivity = (FacebookLoginActivity)FacebookLoginActivity.FacebookLoginActivity;
        RegisterActivity registerActivity = (RegisterActivity)RegisterActivity.RegisterActivity;
        if(loginActivity != null) loginActivity.finish();
        if(mainActivity != null) mainActivity.finish();
        if(facebookLoginActivity != null) facebookLoginActivity.finish();
        if(registerActivity != null) registerActivity.finish();

        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Tutorial2Activity.class));  // 다음으로 이동
                finish();   // 끝내기
            }
        });
    }
}
