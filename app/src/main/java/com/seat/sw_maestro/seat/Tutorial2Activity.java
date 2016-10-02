package com.seat.sw_maestro.seat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class Tutorial2Activity extends AppCompatActivity {

    Button buttonBack;
    Button buttonConnect;
    BluetoothSPP bt; // 블루투스
    private static final String TAG = "Tutorial2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial2);

        bt = new BluetoothSPP(getApplicationContext());

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Tutorial1Activity.class));  // 이전으로 이동
                finish();   // 끝내기
            }
        });

        buttonConnect = (Button) findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bt.isBluetoothAvailable()) {    // 블루투스 자체를 지원 안함. 가능성이 거의 없겠지?
                    Toast.makeText(getApplicationContext(), "블루투스가 가능한 기기가 아닙니다.", Toast.LENGTH_LONG).show();
                }
                else {  // 블루투스는 됨
                    if (!bt.isBluetoothEnabled()) { // 블루투스가 꺼져있다면.
                        Log.d(TAG, "블루투스 켜달라고 인텐트");
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
                    } else {    // 블루투스가 켜져있다면 디바이스 선택하는 인텐트
                        Log.d(TAG, "블루투스 켜져있음. 디바이스 선택하는 인텐트");
                        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                // Do something when successfully connected
                Log.d(TAG, "블루투스 연결에 성공했다.");
                bt.stopService();   // 블루투스 정지하고
                startActivity(new Intent(getApplicationContext(), Tutorial3Activity.class));  // 다음으로 이동
                finish();   // 끝내기
            }

            public void onDeviceDisconnected() {
                // Do something when connection was disconnected
                Log.d(TAG, "블루투스 연결이 끊어졌다.");
            }

            public void onDeviceConnectionFailed() {
                // Do something when connection failed
                Log.d(TAG, "블루투스 연결에 실패했다.");
            }
        });
    }

    // 블루투스 관련
    // 상태를 나타내는 상태 변수, 상태 바뀌면 로그캣에 나옴
    //STATE_NONE = 0; // we're doing nothing
    //STATE_LISTEN = 1; // now listening for incoming connections
    //STATE_CONNECTING = 2; // now initiating an outgoing connection
    //STATE_CONNECTED = 3; // now connected to a remote device

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "켜진 상태에서 기기에 연결");
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                bt.connect(data);
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {    // 블루투스 연결해주세요. 인텐트 결과
            if(resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "블루투스 요청 후 켰음. 디바이스 목록에서 선택");
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            } else {
                Log.d(TAG, "블루투스 요청 후 취소");
                Toast.makeText(getApplicationContext(), "블루투스를 연결해야 서비스가 가능합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
