package com.seat.sw_maestro.seat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Button buttonLogin;
    Button buttonRegister;

    // 로그인 사용자 정보를 저장하기 위한 SharedPreferences
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    // 페이스북 연동과 관련
    private CallbackManager callbackManager = null;
    private AccessTokenTracker accessTokenTracker = null;
    private LoginButton buttonFacebookLogin = null;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {    // 페북 로그인 성공시
            Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_LONG).show();

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            response.getError();
                            Log.e("JSON:", object.toString());
                            try {
                                prefs = getSharedPreferences("UserStatus", MODE_PRIVATE);
                                editor = prefs.edit();

                                editor.putString("FacebookID", object.getString("id")); // 페이스북 아이디 세팅
                                editor.putString("Name", object.getString("name")); // 이름 세팅
                                editor.putString("Age", object.getString("birthday")); // 생일 세팅
                                editor.commit();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, birthday");   // 페북 아이디, 이름, 생일 가져올거야
            request.setParameters(parameters);
            //Log.e(" About to Graph Call", " ");
            request.executeAsync();
            //Log.e(" Finished Graph Call", " ");

            // 여기에서 그 디비에 넣고 그 작업 시작해.
            //startActivity(new Intent(LoginActivity.this, LoginActivity2.class));
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "User sign in canceled!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getApplicationContext(), "Something is wrong!", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // 페북 연동 위해
        setContentView(R.layout.activity_main);

        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);
        buttonFacebookLogin = (LoginButton) findViewById(R.id.buttonFacebookLogin); // 페북 로그인 버튼

        // 권한.. 무엇무엇 가져올까?
        buttonFacebookLogin.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday"));

        // 페북 연동 관련
        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                System.out.println("사용자 토큰 변경됨.");
            }
        };
        accessTokenTracker.startTracking();
        buttonFacebookLogin.registerCallback(callbackManager, callback);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "로그인 버튼이 눌림");
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "회원가입 버튼이 눌림");
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }
}
