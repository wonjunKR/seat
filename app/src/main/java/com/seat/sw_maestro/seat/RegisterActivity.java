package com.seat.sw_maestro.seat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    EditText editTextID;
    EditText editTextPassword;
    EditText editTextPasswordCheck;
    EditText editTextName;
    Spinner spinnerAge;
    Spinner spinnerJob;
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG, "RegisterActivity");

        //스피너 관련
        spinnerAge = (Spinner)findViewById(R.id.spinnerAge);
        spinnerJob = (Spinner)findViewById(R.id.spinnerJob);
        //어댑터 생성
        ArrayAdapter adapterAge = ArrayAdapter.createFromResource(this, R.array.age, android.R.layout.simple_spinner_item);
        ArrayAdapter adapterJob = ArrayAdapter.createFromResource(this, R.array.job, android.R.layout.simple_spinner_item);
        //스피너와 어댑터 연결
        spinnerAge.setAdapter(adapterAge);
        spinnerJob.setAdapter(adapterJob);
        editTextID = (EditText)findViewById(R.id.editTextID);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        editTextPasswordCheck = (EditText)findViewById(R.id.editTextPasswordCheck);
        editTextName = (EditText)findViewById(R.id.editTextName);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "회원가입 버튼이 눌림");
                String id = editTextID.getText().toString();
                String password = editTextPassword.getText().toString();
                String passwordCheck = editTextPasswordCheck.getText().toString();
                String name = editTextName.getText().toString();
                String age = spinnerAge.getSelectedItem().toString();
                String job = spinnerJob.getSelectedItem().toString();

                Boolean registerCheck = true; // 최종 제출을 하기전에 입력한 정보들이 정확한지 체크.. 아이디 중복유무, 패스워드 확인 등. false 뭔가 잘못

                String[] params = new String[6];
                params[0] = id;

                // 패스워드 체크 관련
                if(!password.equals(passwordCheck)){
                    Toast.makeText(getApplicationContext(), "Check Password!", Toast.LENGTH_LONG).show();
                    registerCheck = false;
                }

                // 아이디 중복 체크 관련
                String result;
                HTTPManager httpManager = new HTTPManager();
                result = httpManager.useAPI(10,params);  // 특정 계정 조회를 하는 API를 불러온다.
                Log.d(TAG, "계정조회 결과 : " + result);   // 이미 입력한 아이디 있으면 계정정보 리턴, 없으면 -1
                if(!result.equals("-1")){   // -1이 아니면 이미 아이디가 사용중이다.
                    Toast.makeText(getApplicationContext(), "ID already used!", Toast.LENGTH_LONG).show();
                    registerCheck = false;
                }

                // 최종 가입
                if(registerCheck == true){
                    password = Base64.encodeToString(password.toString().getBytes(), Base64.DEFAULT); // 암호화
                    Log.d(TAG, "입력정보 : " + id + " " + password + " " + passwordCheck + " " + name + " " + age + " " + job);

                    /*
                        사용자 등록을 완료는 3단계를 거친다.
                        1. 계정을 등록한다.
                        2. 유저 정보를 등록한다. (여기서 DB 구조에서 중요한 UserNumber를 발급받는다.)
                        3. 2번에서 발급받은 UserNumber를 1번 계정에 연결시킨다.
                     */

                    // 1. 계정 등록을 하고
                    params[0] = id;
                    params[1] = password;
                    params[2] = "-1";
                    result = httpManager.useAPI(8,params);  // 사용자 계정 등록하고,
                    Log.d(TAG, "계정 등록 : " + result);

                    // 2. 유저 정보를 등록하고
                    params[0] = name;   // 이름
                    params[1] = age;    // 나이
                    params[2] = "61.2"; // 체중
                    params[3] = "170.5";// 키
                    params[4] = job;    // 직업
                    params[5] = "-1";   // facebook ID
                    result = httpManager.useAPI(1,params);  // 사용자 정보 등록, 결과로 DB에 생성된 UserNumber를 받아온다.
                    Log.d(TAG, "사용자 정보 등록 : " + result);

                    // 3. UserNumber를 1번 계정에 연결시킨다.
                    params[0] = password;
                    params[1] = result;
                    params[2] = id;
                    httpManager.useAPI(9,params);   // 이거는 방금 등록한 계정에 새로 발급받은 UserNumber를 추가하는 것.

                    // 이 다음부터 메인으로 넘어가기 하장
                }
            }
        });
    }
}
