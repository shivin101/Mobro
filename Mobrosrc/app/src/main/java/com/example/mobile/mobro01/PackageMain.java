package com.example.mobile.mobro01;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PackageMain extends AppCompatActivity {
    public static String current_number;

    @Override
    protected void onResume() {
        super.onResume();
        if(Logged_in_User.getUser().getUser_uid() == null)
            finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);

        current_number = getIntent().getExtras().getString("phone_no");

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private String tabtitles[] = new String[] { "Topups", "Full Talktime", "2G", "3G", "Local / ISD / STD", "Others" };
        public MyPagerAdapter(FragmentManager  fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return PackageTopups.newInstance("Topups");
                case 1: return PackageFulltalk.newInstance("Full TAlktime");
                case 2: return PackageTwoG.newInstance("2G");
                case 3: return PackageThreeG.newInstance("3G");
                case 4: return PackagePhone.newInstance("Local / ISD / STD");
                case 5: return PackageOther.newInstance("Other");
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitles[position];
        }
    }
}
