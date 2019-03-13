package com.example.jhu.seminar.WMSA;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.jhu.seminar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WalkModeStatsAnal extends AppCompatActivity {
    private String email;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager pager_2;
    private TabLayout tabs_2;
    private FragmentPagerAdapter mAdapterViewPager;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wmsa);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("走路模式統計分析");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        email = user.getEmail();

        pager_2 = (ViewPager) findViewById(R.id.wmsa_pager);
        tabs_2 = (TabLayout) findViewById(R.id.wmsa_tabs);
        mAdapterViewPager = new MainPagerAdapter(getSupportFragmentManager());
        manager = getSupportFragmentManager();
        pager_2.setAdapter(mAdapterViewPager);
        tabs_2.setupWithViewPager(pager_2);
        pager_2.setOffscreenPageLimit(2);
    }

    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
        private final String[] tabTitles = { "時", "日", "自訂" };

        MainPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public int getCount() { return NUM_ITEMS; }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new WMSAHour();
                case 1:
                    return new WMSADay();
                case 2:
                    return new WMSACustom();
                default:
                    return null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (manager.getBackStackEntryCount() != 0)
            manager.popBackStack();
    }
}
