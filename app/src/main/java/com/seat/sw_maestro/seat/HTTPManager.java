package com.seat.sw_maestro.seat;

import android.os.StrictMode;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPManager extends Thread {
    private static String user_url = "http://52.192.119.174:52273/user";
    private static String data_url = "http://52.192.119.174:52273/data";
    private static String account_url = "http://52.192.119.174:52273/account";
    String temp_url;
    public HTTPManager() {
    //
    }

    /* 서버와 통신 사용법
    // 유저 관련
    모든 유저 정보 조회 - mode = 0
    특정 유저 정보 조회 - mode = 0, params[0] = 죄회할 사용자 아이디
    유저 등록 - mode = 1, params[0~5] = 이름, 나이, 몸무게, 키, 직업, FacebookID
    유저 수정 - mode = 2, params[0~6] = 이름, 나이, 몸무게, 키, 직업, FacebookID, 수정할 사용자 아이디
    유저 삭제 - mode = 3, params[0] = 삭제할 사용자 아이디
    // 데이터 관련
    모든 데이터 정보 조회 - mode = 4
    특정 데이터 정보 조회 - mode = 4, params[0] = 죄회할 데이터 아이디
    데이터 등록 - mode = 5, params[0~4] = 유저아이디, 타임라인, 앉은시간, 정확도, 날짜
    데이터 수정 - mode = 6, params[0~5] = 유저아이디, 타임라인, 앉은시간, 정확도, 날짜, 데이터 아이디
    데이터 삭제 - mode = 7, params[0] = 삭제할 데이터 아이디
    // 계정 관련
    계정 추가 - mode = 8, params[0~2] = 계정 아이디, 패스워드, UserNumber
    계정 수정 - mode = 9, params[0~2] = 패스워드, UserNumber, 수정할 유저 아이디
    모든 계정 조회 - mode = 10
    특정 계정 조회 - mode = 10, params[0] = 조회할 계정 아이디

    사용 예)
    String[] test = new String[6];
        test[0] = "name2";
        test[1] = "13";
        test[2] = "423.12";
        test[3] = "123.12";
        test[4] = "job";
        test[5] = "2";

     HTTPManager httpManager = new HTTPManager();
     httpManager.useAPI(1,test);
     */

    public String useAPI(int mode, String params[]) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); // 네트워크 강제 사용

        OkHttpClient client = new OkHttpClient();   // HTTP 통신위해
        Response response = null;
        String result = null;
        RequestBody body;
        Request request;

        switch(mode){   // 모드에 따라서 어떤 API를 사용할 것인가..?
            case 0: // 사용자 정보 조회
                temp_url = user_url;
                if(null != params)  // 널이라면 전체사용자 조회, 아니라면 특정 사용자 정보 조회니까
                     temp_url = user_url + "/" + params[0];  // 특정 사용자의 id를 받는다.

                //request
                request = new Request.Builder()
                        .url(temp_url)
                        .get()
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 1: // 사용자 추가
                body = new FormBody.Builder()
                        .add("Name", params[0])
                        .add("Age", params[1])
                        .add("Weight", params[2])
                        .add("Height", params[3])
                        .add("Job", params[4])
                        .add("FacebookID", params[5])
                        .build();
                //request
                request = new Request.Builder()
                        .url(user_url)
                        .post(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 2: // 사용자 수정
                body = new FormBody.Builder()
                        .add("Name", params[0])
                        .add("Age", params[1])
                        .add("Weight", params[2])
                        .add("Height", params[3])
                        .add("Job", params[4])
                        .add("FacebookID", params[5])
                        .build();
                //request
                request = new Request.Builder()
                        .url(user_url + "/" + params[6])
                        .put(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 3: // 사용자 삭제
                body = new FormBody.Builder().build();
                //request
                request = new Request.Builder()
                        .url(user_url + "/" + params[0])
                        .delete(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 4: // 데이터 정보 조회
                String temp_url = data_url;
                if(null != params)  // 널이라면 전체 데이터 조회, 아니라면 특정 데이터 정보 조회니까
                    temp_url = data_url + "/" + params[0];  // 특정 데이터의 id를 받는다.

                //request
                request = new Request.Builder()
                        .url(temp_url)
                        .get()
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 5: // 데이터 추가
                body = new FormBody.Builder()
                        .add("UserNumber", params[0])
                        .add("TimeLine", params[1])
                        .add("SittingTime", params[2])
                        .add("Accuracy", params[3])
                        .add("Date", params[4])
                        .build();
                //request
                request = new Request.Builder()
                        .url(data_url)
                        .post(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 6: // 데이터 수정
                body = new FormBody.Builder()
                        .add("UserNumber", params[0])
                        .add("TimeLine", params[1])
                        .add("SittingTime", params[2])
                        .add("Accuracy", params[3])
                        .add("Date", params[4])
                        .build();
                //request
                request = new Request.Builder()
                        .url(data_url + "/" + params[5])
                        .put(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 7: // 데이터 삭제
                body = new FormBody.Builder().build();
                //request
                request = new Request.Builder()
                        .url(data_url + "/" + params[0])
                        .delete(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 8: // 계정 추가
                body = new FormBody.Builder()
                        .add("UserID", params[0])
                        .add("Password", params[1])
                        .add("UserNumber", params[2])
                        .build();
                //request
                request = new Request.Builder()
                        .url(account_url)
                        .post(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 9: // 계정 수정
                body = new FormBody.Builder()
                        .add("Password", params[0])
                        .add("UserNumber", params[1])
                        .build();
                //request
                request = new Request.Builder()
                        .url(account_url + "/" + params[2])
                        .put(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 10: // 계정 정보 조회
                temp_url = account_url;
                if(null != params)  // 널이라면 전체 계정 조회, 아니라면 특정 계정 정보 조회니까
                    temp_url = account_url + "/" + params[0];  // 특정 계정의 id를 받는다.

                //request
                request = new Request.Builder()
                        .url(temp_url)
                        .get()
                        .build();
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        try {
            result = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
