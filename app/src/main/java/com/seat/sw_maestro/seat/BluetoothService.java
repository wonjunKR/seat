package com.seat.sw_maestro.seat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

/*
    이 서비스에서는 주기적으로 방석으로 데이터를 보낸다. (실시간모드 or 일반모드)
    각 모드가 보내지는 경우는 다음과 같다.

    방석상태모드 - 사용자가 Tab1(방석연결상태)을 보고 있을 때
    실시간모드 - 사용자가 Tab3(실시간)을 보고 있을 때
    일반모드 - 그 외의 경우

    실시간모드는 방석에 실시간모드용 데이터 요청을 하며, 일반모드는 일반모드용 데이터 요청을 한다.
    실시간모드의 경우에는 방석의 데이터를 실시간으로 받으며, 일반모드에는 쌓인 데이터가 있는 경우라면 쌓인 데이터를 받고, 없는 경우라면 받은 당시 데이터 받음.

    받아온 데이터는 DB에 저장되어야하는데 일반모드의 경우에는 1(변경가능)분에 한 번씩 데이터를 요청하며, 매 정각에 데이터를 정산한다.
    정산의 내용은 다음과 같다.
    몇 번 데이터를 받았는지 카운트하여, 1시간동안 몇 분을 앉아있었는지 체크한다.
    그 카운트된 시점마다 정확도를 분석하여, 카운트 된 동안의 평균을 구한다. 점수도 구함.
    DB에 넣는다. 현재시간, 정확도, 점수, 현재 날짜.
*/

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private static final String SeatName = "wnjungod";    // 방석의 블루투스 이름을 입력한다.
    BluetoothSPP bt;
    private Messenger mRemote;  // 서비스와 액티비티 간에 통신을 하기 위해서 쓰는 메신저
    Timer timer;    // 일정시간마다 일을 하기 위해서 .. 타이머

    int serviceState = 0;   // 서비스의 상태. 값은 아래를 보세요.
    private static final int STATE_COMMON = 0;  // 일반모드
    private static final int STATE_TAB1 = 1;    // Tab1을 보는 상태
    private static final int STATE_TAB3 = 2;    // Tab3를 보는 상태

    Perceptron perceptron0;
    Perceptron perceptron1;
    Perceptron perceptron2;
    Perceptron perceptron3;
    Perceptron perceptron4;

    Centroid centroid0;
    Centroid centroid1;
    Centroid centroid2;
    Centroid centroid3;
    Centroid centroid4;
    Centroid centroid5;


    // 생성자
    BluetoothService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "서비스가 bind 됨");
        return new Messenger(new RemoteHandler()).getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG,"서비스가 unbind 됨");

        return super.onUnbind(intent);
    }

    // what 값에 따라서 액티비티에서 받았을 때 처리가 달라진다.
    // 현재 Service -> Tab1 간에 통신은  what 값 0을 사용 (방석의 상태)
    // 현재 Service -> Tab3 간에 통신은  what 값 1을 사용 (자세의 결과)

    public void remoteSendMessage_Tab1(String data) {    // 액티비티로 메시지 전달. 방석의 연결 유무 상태를 나타내주기 위해 사용
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = 0;
            msg.obj = data; // 오브젝트로 String data를 보낸다.
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void remoteSendMessage_Tab3(String data) {    // 액티비티로 메시지 전달. 자세의 결과
        if (mRemote != null) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = data; // 오브젝트로 String data를 보낸다.
            try {
                mRemote.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    // Service handler 추가  액티비티 -> 서비스로 받아오는 경우.
    private class RemoteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            // 일반모드
            TimerTask timerTask_Common = new TimerTask() {
                public void run() { // 일반모드에서 어떻게 동작하는지
                    //Log.d(TAG, "TimerTask_Common 실행 됨");
                }
            };

            // 탭 1과 연결된 경우 동작을 여기다 둔다.
            // Service -> Tab1 보냄
            // 주기적으로 방석의 상태를 보내주는 일을 한다. 이것을 안하면 바뀌는 순간에만 텍스트뷰를 바꾸니... 바뀐 순간에 그 화면을 안보면 안바꿔짐
            TimerTask timerTask_Tab1 = new TimerTask() {
                public void run() {
                    //Log.d(TAG, "TimerTask_Tab1 실행 됨");
                    //Log.d(TAG, "현재 상태 : " + serviceState);
                    if (bt != null && bt.getServiceState() == 3)   // 3이면 블루투스에서 연결상태임
                        remoteSendMessage_Tab1("1"); // 연결되었다고 보내자.
                    else
                        remoteSendMessage_Tab1("0");
                }
            };

            // 탭 3과 연결된 경우 동작을 여기다 둔다.
            // Service -> Tab3 보냄
            // 자세의 결과를 보내준다.
            TimerTask timerTask_Tab3 = new TimerTask() {
                public void run() {
                    //Log.d(TAG, "TimerTask_Tab3 실행 됨");
                    //Log.d(TAG, "현재 상태 : " + serviceState);
                }
            };

            switch (msg.what) {
                case 0 :    // Tab1을 보는 경우.
                    Log.d(TAG, "서비스와 탭1이 연결되었다.");
                    serviceState = STATE_TAB1;
                    mRemote = (Messenger) msg.obj;

                    if(timer != null){  // 기존 타이머에 등록됬던 것 다 삭제
                        timer.cancel();
                        timer = null;
                    }

                    timer = new Timer();
                    timer.schedule(timerTask_Tab1, 1000, 2000);  // Tab1전용 task를 1초 후 2초마다 실행
                    break;

                case 1 :    // Tab3를 보는 경우
                    Log.d(TAG, "서비스와 탭3이 연결되었다.");
                    serviceState = STATE_TAB3;
                    mRemote = (Messenger) msg.obj;

                    if(timer != null){  // 기존 타이머에 등록됬던 것 다 삭제
                        timer.cancel();
                        timer = null;
                    }

                    timer = new Timer();
                    timer.schedule(timerTask_Tab3, 1000, 1000); // Tab3전용 task를 1초 후 1초마다 실행

                    Log.d(TAG, "실시간용 리스너를 등록한다.");

                    // 블루투스 리스너 실시간용
                    bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
                        public void onDataReceived(byte[] data, String message) {
                            // Do something when data incoming
                            Log.d(TAG, "실시간용 데이터 받았다 -> " + message);

                            //Toast.makeText(getApplicationContext(), "데이터를 받았다.", Toast.LENGTH_SHORT).show();
                            //bt.send("1",true);

                            String[] input_string = new String[11];
                            input_string = message.split(",");

                            int positionResult = guessPosition(input_string);   // 자세 결과 추측

                            if (bt != null && bt.getServiceState() == 3)   // 3이면 블루투스에서 연결상태임
                                remoteSendMessage_Tab3(String.valueOf(positionResult)); // 자세의 결과를 보냄
                            else
                                remoteSendMessage_Tab3("-1");   // 블루투스 연결이 안되어있음
                        }
                    });

                    break;

                case 2 :    // Tab1이 화면에서 사라짐
                    Log.d(TAG, "Tab1에서 끝났다 신호 보냄.");
                    // Tab1에서 끝났다는 것은, Tab1이 화면에서 사라짐. -> 홈으로 갔거나, Tab3를 본다...
                    // 타이머를 없애려면..

                    // 만약 Tab3를 보는 경우라면 serviceState가 Tab3로 먼저 바뀐다.
                    // 그래서 아래서 Tab1과 비교하는 것이다. 그대로 Tab1인 경우에는 홈화면으로 간 것이니까.
                    // 비교를 안하면 Tab3 새로운 타이머가 등록되었는데 그것을 삭제해버림.

                    if((timer != null) && (serviceState == STATE_TAB1)){  // 기존 타이머에 등록됬던 것 다 삭제
                        timer.cancel();
                        timer = null;

                        // 여기로 들어온 경우에는 홈 화면을 보는 것이다. 타이머 일반모드 실행시켜야함.
                        // 서비스의 동작을 일반모드로 변경
                        timer = new Timer();
                        timer.schedule(timerTask_Common, 1000, 1000);   // 일반모드 실행
                        serviceState = STATE_COMMON;
                    }
                    break;

                case 3 :    // Tab3가 화면에서 사라짐
                    Log.d(TAG, "Tab3에서 끝났다 신호 보냄.");
                    // Tab1에서 끝났다는 것은, Tab1이 화면에서 사라짐. -> 홈으로 갔거나, Tab3를 본다...
                    // 타이머를 없애려면..
                    if((timer != null) && (serviceState == STATE_TAB3)){  // 기존 타이머에 등록됬던 것 다 삭제
                        timer.cancel();
                        timer = null;

                        // 여기로 들어온 경우에는 홈 화면을 보는 것이다. 타이머 일반모드 실행시켜야함.
                        // 서비스의 동작을 일반모드로 변경
                        timer = new Timer();
                        timer.schedule(timerTask_Common, 1000, 1000);   // 일반모드 실행
                        serviceState = STATE_COMMON;
                    }
                    break;

                default :
                    Log.d(TAG, "등록되지 않은 곳에서 메시지가 옴");
                    break;
            }
        }
    }

    @Override
    public void onCreate() {

        //* 알고리즘
        perceptron0 = new Perceptron(9);
        perceptron1 = new Perceptron(9);
        perceptron2 = new Perceptron(9);
        perceptron3 = new Perceptron(9);
        perceptron4 = new Perceptron(9);

        // -1.29	-4.56	0.475	-13.803	-8.553	-1.134	7.945	9.411	1.5944
        perceptron0.weights[0] = (float) -1.29;
        perceptron0.weights[1] = (float) -4.56;
        perceptron0.weights[2] = (float) 0.475;
        perceptron0.weights[3] = (float) -13.803;
        perceptron0.weights[4] = (float) -8.553;
        perceptron0.weights[5] = (float) -1.134;
        perceptron0.weights[6] = (float) 7.945;
        perceptron0.weights[7] = (float) 9.411;
        perceptron0.weights[8] = (float) 1.5944;
        //-0.063	-1.508	-0.8172	5.765	0.7297	-5.76	4.73	-3.13	-3.07
        perceptron1.weights[0] = (float) -0.063;
        perceptron1.weights[1] = (float) -1.508;
        perceptron1.weights[2] = (float) -0.8172;
        perceptron1.weights[3] = (float) 5.765;
        perceptron1.weights[4] = (float) 0.7297;
        perceptron1.weights[5] = (float) -5.76;
        perceptron1.weights[6] = (float) 4.73;
        perceptron1.weights[7] = (float) -3.13;
        perceptron1.weights[8] = (float) -3.07;
        //-6.971	1.698	-0.397	-0.249	1.058	3.573	-6.608	-3.224	6.282
        perceptron2.weights[0] = (float) -6.971;
        perceptron2.weights[1] = (float) 1.698;
        perceptron2.weights[2] = (float) -0.397;
        perceptron2.weights[3] = (float) -0.249;
        perceptron2.weights[4] = (float) 1.058;
        perceptron2.weights[5] = (float) 3.573;
        perceptron2.weights[6] = (float) -6.608;
        perceptron2.weights[7] = (float) -3.224;
        perceptron2.weights[8] = (float) 6.282;
        //-0.6376	4.043	-4.448	0.2408	-0.7218	1.321	0.094	0.4255	-4.001
        perceptron3.weights[0] = (float) -0.6376;
        perceptron3.weights[1] = (float) 4.043;
        perceptron3.weights[2] = (float) -4.448;
        perceptron3.weights[3] = (float) 0.2408;
        perceptron3.weights[4] = (float) -0.7218;
        perceptron3.weights[5] = (float) 1.321;
        perceptron3.weights[6] = (float) 0.094;
        perceptron3.weights[7] = (float) 0.4255;
        perceptron3.weights[8] = (float) -4.001;
        //2.76	0.1593	1.5971	-1.565	0.0531	0.5649	-3.134	-0.5655	1.9782
        perceptron4.weights[0] = (float) 2.76;
        perceptron4.weights[1] = (float) 0.1593;
        perceptron4.weights[2] = (float) 1.5971;
        perceptron4.weights[3] = (float) -1.565;
        perceptron4.weights[4] = (float) 0.0531;
        perceptron4.weights[5] = (float) 0.5649;
        perceptron4.weights[6] = (float) -3.134;
        perceptron4.weights[7] = (float) -0.5655;
        perceptron4.weights[8] = (float) 1.9782;

        centroid0 = new Centroid(0.79255102,-1.647755102); // 정자세
        centroid1 = new Centroid(-2.389793814,-0.439484536);   // 왼쪽
        centroid2 = new Centroid(1.8,-3.3);    // 오른쪽
        centroid3 = new Centroid(0.194646465,-1.044747475); // 앞으로 쏠
        centroid4 = new Centroid(0.922626263,-1.468989899); // 뒤로 쏠
        centroid5 = new Centroid(0.2966,4.2344); // 엉덩이 앞으로 뺌

        bt = new BluetoothSPP(getApplicationContext());

        // 자동연결부분
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);
        bt.autoConnect(SeatName);

        // 블루투스 리스너
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                    // Do something when data incoming
                    Log.d(TAG, "블루투스 데이터 받았다 -> " + message);
                    //Toast.makeText(getApplicationContext(), "데이터를 받았다.", Toast.LENGTH_SHORT).show();
                    //bt.send("1",true);
            }
        });

        // 블루투스 상태 리스너. 상태가 바뀌면 Tab1으로 보내준다. (딱히 필요는 없다... 3초마다 확인하긴 하니까...)
        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
            public void onServiceStateChanged(int state) {
                if (state == BluetoothState.STATE_CONNECTED) {
                    //Log.d(TAG, "연결된 상태");
                    remoteSendMessage_Tab1("1");
                } else if (state == BluetoothState.STATE_CONNECTING) {
                    //Log.d(TAG, "연결중인 상태");
                    remoteSendMessage_Tab1("0");
                } else if (state == BluetoothState.STATE_LISTEN) {
                    //Log.d(TAG, "리슨 상태");
                    remoteSendMessage_Tab1("0");
                } else if (state == BluetoothState.STATE_NONE) {
                    //Log.d(TAG, "아무것도 아닌 상태");
                    remoteSendMessage_Tab1("0");
                }
            }
        });
        Log.d(TAG,"서비스가 시작되었습니다.");



        // 서비스가 생성될 때는 TimerTask를 일반모드로 생성한다.
        // 일반모드
        TimerTask timerTask_Common = new TimerTask() {
            public void run() { // 일반모드에서 어떻게 동작하는지
                //Log.d(TAG, "TimerTask_Common 실행 됨");
            }
        };

        timer = new Timer();
        timer.schedule(timerTask_Common, 1000, 1000);   // 일반모드 실행
        serviceState = STATE_COMMON;

        setAlarm(); // 서비스가 실행될 때 알람을 실행한다. 1시간마다 실행하며 값을 정산해서 DB에 넣는 부분.
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"서비스가 종료되었습니다.");
        super.onDestroy();
        bt.stopService();
    }

    public void setAlarm(){
        Log.d(TAG, "알람이 설정");
        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "지금 시간 : " + calendar.getTime());

        //테스트용
        //calendar.add(Calendar.HOUR, +8);
        //Log.d(TAG, "내가 지정한 시간 : " + calendar.getTime());

        // 테스트 끝나면 여기 주석 지우자
        calendar.add(Calendar.HOUR, + 1); // 서비스가 시작된 시간으로부터 1시간 뒤(다음 정각)
        calendar.set(Calendar.MINUTE, 0); // 다음 정각 0분에
        calendar.set(Calendar.SECOND, 30); // 초는 30초로 (혹시모를 오류.. 더 빨리 실행해버리면 꼬이니까)
        Log.d(TAG, "언제 알림이 시작될 것인가? : " + calendar.getTime());

        AlarmManager alarm = (AlarmManager) this. getSystemService(Context.ALARM_SERVICE);   // 알람 매니저
        Intent intent = new Intent(getApplication(), AlarmReceiver.class);  // 알람 리시버로 인텐트
        PendingIntent sender = PendingIntent.getBroadcast(getApplication(), 0, intent, 0);

        //alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1 * 1 * 30 * 1000, sender);
        // 위는 테스트용, 인자는 시간타입, 언제 실행할 건가?, 간격은?, 인텐트. 5초마다 AlarmReceiver 실행.

        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1 * 60 * 60 * 1000, sender);
        // 이게 진짜 실전용 1시간마다 동작하는 알람.

        // 24 * 60 * 60 * 1000 -> 하루 24시간, 60분, 60초
    }

    public int getMaxIndex(double[] input){
        int maxIndex = 0;
        double max = 0;

        for(int i = 0; i < input.length; i++){
            if(input[i] > max){ // 여러개 인풋 중에서 가장 컸던 부분의 인덱스를 리턴한다. input[0]~[5] -> 0~5 리턴
                max = input[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public int getMinIndex(double[] input){
        int minIndex = 0;
        double min = 999999;

        for(int i = 0;i < input.length; i++){
            if(input[i] < min){
                min = input[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    public int guessPosition(String[] input_string){
        // 블루투스로 오는 정보는 0~8번은 셀 값, 9~10번은 좌표가 들어온다.

        int[] input_int = new int[9];
        float[] input_float = new float[2];    // string을 float로

        // 셀 값
        for(int i = 0; i < 9; i++){
            input_int[i] = Integer.parseInt(input_string[i]);
        }

        // k-means 관련(좌표)
        input_float[0] = Float.valueOf(input_string[9])/100;
        input_float[1] = Float.valueOf(input_string[10])/100;

        Log.d(TAG, "(" + input_float[0] + " , " + input_float[1] + ")");

        // k-means
        double[] positionProbability = new double[6];
        positionProbability[0] = centroid0.getDistance(input_float[0],input_float[1]);  // 정자세
        positionProbability[1] = centroid1.getDistance(input_float[0],input_float[1]);  // 왼쪽
        positionProbability[2] = centroid2.getDistance(input_float[0],input_float[1]);  // 오른쪽
        positionProbability[3] = centroid3.getDistance(input_float[0],input_float[1]);  // 상체 앞으로
        positionProbability[4] = centroid4.getDistance(input_float[0],input_float[1]);  // 상체 뒤로
        positionProbability[5] = centroid5.getDistance(input_float[0],input_float[1]);  // 엉덩이 앞으로

        Log.d(TAG,"정자세" + positionProbability[0]);
        Log.d(TAG,"왼쪽" + positionProbability[1]);
        Log.d(TAG,"오른쪽" + positionProbability[2]);
        Log.d(TAG,"상체앞" + positionProbability[3]);
        Log.d(TAG,"상체뒤" + positionProbability[4]);
        Log.d(TAG,"엉덩이 앞" + positionProbability[5]);


        int positionResult = getMinIndex(positionProbability);

        if(positionResult == 1 && input_int[2] <= 5){   // 왼쪽으로 쏠린 자세인데, 오른쪽 앞 부분이 안눌렸다 -> 다리를 꼬았음
            return 6;
        }else if(positionResult == 2 && input_int[0] <= 5){ // 오른쪽으로 쏠린 자세인데, 왼쪽 앞 부분이 비었다 -> 다리를 꼬았음
            return 7;
        }else {
            return positionResult;
        }
    }
}
