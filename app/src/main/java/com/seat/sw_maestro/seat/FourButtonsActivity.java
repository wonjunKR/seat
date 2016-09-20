package com.seat.sw_maestro.seat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarBadge;
import com.roughike.bottombar.BottomBarFragment;
import com.roughike.bottombar.OnTabSelectedListener;

public class FourButtonsActivity extends AppCompatActivity {

    private static final String TAG = "FourButtonsActivity";

    private BottomBar bottomBar;
    HomeFragment homeFragment = new HomeFragment(); // 요렇게도 가능

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_buttons);

        // 테스트
        SharedPreferences prefs = getSharedPreferences("UserStatus", MODE_PRIVATE);
        Log.d(TAG, "UserNumber : " + prefs.getString("UserNumber", "값이 존재하지 않음"));

        bottomBar = BottomBar.attach(this, savedInstanceState);

        bottomBar.setFragmentItems(getFragmentManager(), R.id.fragmentContainer, // activity_four_buttons.xml 의 id
                new BottomBarFragment(HomeFragment.newInstance(), R.drawable.ic_update_white_24dp, "Home"),
                new BottomBarFragment(ReportFragment.newInstance(), R.drawable.ic_local_dining_white_24dp, "Report"),
                new BottomBarFragment(homeFragment, R.drawable.ic_favorite_white_24dp, "Position"),
                new BottomBarFragment(homeFragment, R.drawable.ic_location_on_white_24dp, "Setting")
        );

        // Setting colors for different tabs when there's more than three of them.
        bottomBar.mapColorForTab(0, "#3B494C");
        bottomBar.mapColorForTab(1, "#00796B");
        bottomBar.mapColorForTab(2, "#7B1FA2");
        bottomBar.mapColorForTab(3, "#FF5252");



        bottomBar.setOnItemSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                switch (position) {
                    case 0:
                        // Item 1 Selected
                }
            }
        });

        // Make a Badge for the first tab, with red background color and a value of "4".
        BottomBarBadge unreadMessages = bottomBar.makeBadgeForTabAt(1, "#E91E63", 4);   // 이건 카톡 메시지처럼 안 읽은 개수 고런 알람이네

        // Control the badge's visibility
        unreadMessages.show();
        //unreadMessages.hide();

        // Change the displayed count for this badge.
        //unreadMessages.setCount(4);

        // Change the show / hide animation duration.
        unreadMessages.setAnimationDuration(200);

        // If you want the badge be shown always after unselecting the tab that contains it.
        //unreadMessages.setAutoShowAfterUnSelection(true);

    }
}
