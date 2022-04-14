package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ng.assist.UIs.AccomodationApproval;
import ng.assist.UIs.AccountFragments;
import ng.assist.UIs.DmFragment;
import ng.assist.UIs.HomeFragment;
import ng.assist.UIs.WalletKt;

public class AccomodationAdmin extends AppCompatActivity {

    viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
    NoSwipeViewPager viewPager;
    LinearLayout navback;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_admin);
        initView();
    }

    private void initView(){
        tabLayout = findViewById(R.id.acc_tabs);
        navback = findViewById(R.id.accomodation_listing_back);
        viewPager = findViewById(R.id.content_frame);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0, false);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        navback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                finish();
            }
        });
    }


    public class viewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public viewPagerAdapter(FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {

                case 0:

                    AccomodationApproval accomodationApproval = new AccomodationApproval();
                    return accomodationApproval;
            }
            return null;
        }

        @Override
        public int getCount() {

            return 1;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }


    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new AccomodationApproval(), "Approval");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        }
    }

}