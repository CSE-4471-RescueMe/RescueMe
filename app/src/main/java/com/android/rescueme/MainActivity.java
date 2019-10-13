package com.android.rescueme;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private TabItem mTabItemHome;
    private TabItem mTabItemEmergency;
    private TabItem mTabItemSettings;
    private ViewPager mViewPager;
    private PagerController mPagerController;

    private TextView mContactFullName;
    private TextView mContactEmail;
    private TextView mContactPhoneNumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContactFullName = findViewById(R.id.contact_name);
        mContactEmail = findViewById(R.id.contact_email);
        mContactPhoneNumer = findViewById(R.id.contact_phone_number);

        mTabLayout = findViewById(R.id.tab_layout);
        mTabItemHome = findViewById(R.id.tab_home);
        mTabItemEmergency = findViewById(R.id.tab_emergency);
        mTabItemSettings = findViewById(R.id.tab_settings);
        mViewPager = findViewById(R.id.view_pager);

        mPagerController = new PagerController(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mPagerController);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }
}