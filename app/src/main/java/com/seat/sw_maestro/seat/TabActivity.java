package com.seat.sw_maestro.seat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class TabActivity extends AppCompatActivity {
    ImageButton buttonSetting;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"요약","대시보드","실시간"};
    int numberOfTabs =3;
    BluetoothSPP bt; // 블루투스
    // 디바이스와 방석을 연결하는 부분은 BluetoothService에서 담당한다. 여기서는 블루투스 온오프 체크 및 페어링 상태만 체크하고
    // 넘어가도 된다면 BluetoothService를 호출한다.

    private static final String TAG = "TabActivity";
    private static final String SeatName = "wonjun";    // 방석의 블루투스 이름을 입력한다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        Log.d(TAG,"onCreate");

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, numberOfTabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);    // 눌렀을 때 아래에 작게 보이는 색
            }
        });
        tabs.setViewPager(pager);
        // 여기까지는 텝메뉴와 관련됨.

        buttonSetting = (ImageButton) findViewById(R.id.buttonSetting);  // 세팅하기로 가는 버튼
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "세팅 버튼이 눌림");
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            }
        });

        // 블루투스 연결과 관련된 부분입니다
        bt = new BluetoothSPP(getApplicationContext());
        if (!bt.isBluetoothAvailable()) {    // 블루투스 자체를 지원 안함. 가능성이 거의 없겠지?
            Toast.makeText(getApplicationContext(), "블루투스가 가능한 기기가 아닙니다.", Toast.LENGTH_LONG).show();
        } else {  // 블루투스는 됨
            if (!bt.isBluetoothEnabled()) { // 블루투스가 꺼져있다면.
                Log.d(TAG, "블루투스 켜달라고 인텐트");
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
            } else {    // 블루투스가 켜져있다면.
                if(isPairedSeat(SeatName)){ // 만약 SeatName과 페어링 되어 있다면 튜토리얼을 정상적으로 마침 + 설정에서 제거 안했음.
                    Log.d(TAG,"블루투스 서비스를 부른다");
                    Intent bluetoothService = new Intent(getApplicationContext(), BluetoothService.class);
                    startService(bluetoothService);
                } else{ // 아니라면 튜토리얼을 정상적으로 마치지 않았거나, 설정에서 제거해버림.
                    startActivity(new Intent(getApplicationContext(), Tutorial1Activity.class));  // 튜토리얼로 다시 이동
                    finish();   // 끝내기
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {    // 블루투스 연결해주세요. 인텐트 결과
            if(resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "블루투스 요청 후 켰음.");
                if(isPairedSeat(SeatName)){  // 페어링 리스트 중에 방석이 있으면 넘어간다.
                    Log.d(TAG,"블루투스 서비스를 부른다");
                    Intent bluetoothService = new Intent(getApplicationContext(), BluetoothService.class);
                    startService(bluetoothService);
                } else {
                    startActivity(new Intent(getApplicationContext(), Tutorial1Activity.class));  // 튜토리얼로 다시 이동
                    finish();   // 끝내기
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
