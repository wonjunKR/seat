package com.seat.sw_maestro.seat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

/*  블루투스 연결에서 다음으로 넘어가는 로직
    1. 여기 튜토리얼의 목적은 기기 자체의 블루투스 페어링 리스트에 방석 이름을 추가하는 것이다.
       따라서 블루투스 페어링 리스트를 가져와서 방석의 이름이 있다면 넘어가도록 한다.
       그래서 추후에 페어링 리스트에 이름만 있다면 자동연결을 통해서 연결할 수 있다. (일단 페어링 리스트에 있으면 넘어감)
    2. 페어링 리스트에 이름이 없는 경우에는 기기의 블루투스 지원 및 켜짐 상태를 고려해서
        2-1. 켜져있다면 블루투스 기기 리스트에서 방석을 선택하고 3번의 과정을 거친다. 통과하면 넘어가고 아니면 다시 2-1로
        2-2. 꺼져있다면 블루투스를 켜달라는 요청 후 3의 과정을 거친다.(블루투스 꺼진 상태에선 페어링 리스트 다 없음으로 나옴) 통과 넘어감, 실패 2-1로
        2-3. 기기가 블루투스를 지원하지 않는다면 어찌할 수가 없음.
    3. 예외상황(다른 기기를 연결 등)을 위해서 연결 후에도 페어링 리스트에 있는지 확인해야함.
*/

public class Tutorial2Activity extends AppCompatActivity {

    Button buttonBack;
    Button buttonConnect;
    BluetoothSPP bt; // 블루투스
    private static final String TAG = "Tutorial2Activity";
    private static final String SeatName = "wonjun";    // 방석의 블루투스 이름을 입력한다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial2);

        bt = new BluetoothSPP(getApplicationContext());

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {  // 이전 버튼 클릭시
            @Override
            public void onClick(View v) {   // 이전 버튼 클릭
                startActivity(new Intent(getApplicationContext(), Tutorial1Activity.class));  // 이전으로 이동
                finish();   // 끝내기
            }
        });

        buttonConnect = (Button) findViewById(R.id.buttonConnect);  // 연결 버튼 클릭
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "블루투스 연결 상태 : " + bt.getServiceState());
                Log.d(TAG, "방석 페어링 상태 : " + isPairedSeat("wonjun"));
                Log.d(TAG, "방석 페어링 상태 : " + isPairedSeat("방지윤의 MacBook Pro"));
                Log.d(TAG, "방석 페어링 상태 : " + isPairedSeat("이건 없지롱"));

                if(isPairedSeat(SeatName)){  // 페어링 리스트 중에 방석이 있으면 넘어간다. (블루투스가 연결이 되어 있지 않다면 다 false로 나옴)
                    startActivity(new Intent(getApplicationContext(), Tutorial3Activity.class));  // 다음으로 이동
                    finish();   // 끝내기
                }
                else {  // 연결중이 아니라면서 새롭게 연결
                    if (!bt.isBluetoothAvailable()) {    // 블루투스 자체를 지원 안함. 가능성이 거의 없겠지?
                        Toast.makeText(getApplicationContext(), "블루투스가 가능한 기기가 아닙니다.", Toast.LENGTH_LONG).show();
                    } else {  // 블루투스는 됨
                        if (!bt.isBluetoothEnabled()) { // 블루투스가 꺼져있다면.
                            Log.d(TAG, "블루투스 켜달라고 인텐트");
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
                        } else {    // 블루투스가 켜져있는데 방석이 리스트에 없었음.
                            Log.d(TAG, "블루투스 켜져있음. 디바이스 선택하는 인텐트");
                            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                        }
                    }
                }
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                // Do something when successfully connected
                Log.d(TAG, "블루투스 연결에 성공했다.");
                Log.d(TAG, "블루투스 연결 상태 : " + isPairedSeat(SeatName));
                if(isPairedSeat(SeatName)){  // 페어링 리스트 중에 방석이 있으면 넘어간다.
                    startActivity(new Intent(getApplicationContext(), Tutorial3Activity.class));  // 다음으로 이동
                    finish();   // 끝내기
                } else {    // 이 경우에는 사용자가 다른 블루투스 기기를 선택해버렸음. 다시 요청한다.
                    Toast.makeText(getApplicationContext(), "잘못된 기기를 선택하였습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }

            public void onDeviceDisconnected() {
                // Do something when connection was disconnected
                Log.d(TAG, "블루투스 연결이 끊어졌다.");
            }

            public void onDeviceConnectionFailed() {
                // Do something when connection failed
                Log.d(TAG, "블루투스 연결에 실패했다.");
                Toast.makeText(getApplicationContext(), "블루투스 연결에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
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
                Log.d(TAG, "블루투스 리스트에서 선택했음. 이 경우에는 방석을 연결 or 다른 기기를 연결했을 수도 있음. 일단 연결");
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                bt.connect(data);
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {    // 블루투스 연결해주세요. 인텐트 결과
            if(resultCode == Activity.RESULT_OK) {  // 이 전에는 사용자가 이전에 방석을 연결한 적이 있겠지.
                Log.d(TAG, "블루투스 요청 후 켰음.");
                if(isPairedSeat(SeatName)){  // 페어링 리스트 중에 방석이 있으면 넘어간다.
                    startActivity(new Intent(getApplicationContext(), Tutorial3Activity.class));  // 다음으로 이동
                    finish();   // 끝내기
                } else {
                    // 이 경우에는 사용자가 이전에 방석을 연결한 적이 없거나 목록에서 삭제했음. 새롭게 연결 요청
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            } else {
                Log.d(TAG, "블루투스 요청 후 취소");
                Toast.makeText(getApplicationContext(), "블루투스를 연결해야 서비스가 가능합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean isPairedSeat(String seatName) {    // 인자로는 방석의 블루투스 이름이 들어간다.
        boolean isPairedSeat = false;   // 블루투스 리스트에 Seat가 등록되어있는지 저장하는 변수. 일단은 false로

        // 이 부분은 블루투스 페어링 리스트를 가져오기 위한 과정이다.
        BluetoothAdapter mBtAdapter = null;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) { // 페어링 리스트를 하나씩 비교하며
                if(device.getName().equals(seatName))    // Seat의 이름이 있는지 확인한다.
                    isPairedSeat = true;    // 리스트 중 있다면 true로
            }
        }
        return isPairedSeat;
    }
}
