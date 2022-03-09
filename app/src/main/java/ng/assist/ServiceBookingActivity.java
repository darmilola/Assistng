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
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ng.assist.UIs.AllBookings;
import ng.assist.UIs.CompletedBooking;
import ng.assist.UIs.PendingBooking;
import ng.assist.UIs.RefundBookings;

public class ServiceBookingActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter adapter;
    ImageView navBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_bookings);
        initView();
    }


    private void initView(){
        viewPager = findViewById(R.id.provider_bookings_dashboard_pager);
        tabLayout = findViewById(R.id.provider_bookings_tabs);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        navBack = findViewById(R.id.nav_back);
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {

                case 0:
                    return new AllBookings();
                case 1:
                    return new CompletedBooking();
                case 2:
                    return new PendingBooking();
                case 3:
                    return new RefundBookings();

            }
            return null;
        }

        @Override
        public int getCount() {

            return 4;
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

    void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new AllBookings(), "All");
        adapter.addFragment(new CompletedBooking(), "Completed");
        adapter.addFragment(new PendingBooking(), "Pending");
        adapter.addFragment(new RefundBookings(),"Refund");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}