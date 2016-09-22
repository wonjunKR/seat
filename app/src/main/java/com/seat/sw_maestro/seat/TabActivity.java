package com.seat.sw_maestro.seat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class TabActivity extends AppCompatActivity {
    ImageButton buttonSetting;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"요약","대시보드","실시간"};
    int numberOfTabs =3;

    private static final String TAG = "TabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,numberOfTabs);
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
        // 요 위까지는 텝메뉴와 관련됨.

        buttonSetting = (ImageButton)findViewById(R.id.buttonSetting);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "세팅 버튼이 눌림");
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            }
        });
    }

}
