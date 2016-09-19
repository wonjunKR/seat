package com.seat.sw_maestro.seat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText editTextID;
    EditText editTextPassword;
    Button buttonLogin;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "LoginActivity");

        editTextID = (EditText)findViewById(R.id.editTextID);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "로그인 버튼이 눌림");

                String id;
                String password;

                id = editTextID.getText().toString();
                password = editTextPassword.getText().toString();

                Log.d(TAG, "입력한 아이디 : " + id);
                Log.d(TAG, "입력한 패스워드 : " + password);

                String[] params = new String[1];
                params[0] = id;

                HTTPManager httpManager = new HTTPManager();
                result = httpManager.useAPI(10,params);  // 특정 계정 조회를 하는 API를 불러온다.
                Log.d(TAG, "계정조회 결과 : " + result);   // 이미 입력한 아이디 있으면 계정정보 리턴, 없으면 -1

                if (result.equals("-1")) {   // 아이디 없음.
                    Toast.makeText(getApplicationContext(), "Check ID!", Toast.LENGTH_LONG).show();
                } else {    // 그게 아니면 비밀번호 체크하겠지
                    try {
                        JSONArray jsonArray = new JSONArray(result);    // 서버로부터 json 배열 받고
                        JSONObject jsonObject = jsonArray.getJSONObject(0);   // 리턴은 하나니까 이거 하나만 오브젝트로..

                        String encoded_password = jsonObject.getString("Password");    // 값 중에서 패스워드 추출
                        Log.d(TAG, "서버로 부터 받은 비번 : " + encoded_password);
                        String decoded_password = new String(Base64.decode(encoded_password, Base64.DEFAULT)); // 복호화
                        Log.d(TAG, "복호화 : " + decoded_password);

                        if(password.equals(decoded_password)){  // 로그인 성공
                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                            // 여기서부터 다음으로 넘어가는 것 하기!!!
                        }
                        else{   // 로그인 실패
                            Toast.makeText(getApplicationContext(), "Check Password!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
