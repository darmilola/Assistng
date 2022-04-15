package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import ng.assist.Adapters.AccomodationListingsAdapter;
import ng.assist.Adapters.DasdhboardProductAdapter;
import ng.assist.Adapters.RealEstateDashboardListingAdapter;
import ng.assist.UIs.AccomodationApproval;
import ng.assist.UIs.AgentAllListings;
import ng.assist.UIs.AgentBookings;
import ng.assist.UIs.ApprovedHouse;
import ng.assist.UIs.RejectedListing;
import ng.assist.UIs.Utils.InputDialog;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.GroceryModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class EstateListingDashboard extends AppCompatActivity {

    viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
    NoSwipeViewPager viewPager;
    LinearLayout navback;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate_listing_dashboard);
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
                    return new AgentAllListings();
                case 1:
                    return new ApprovedHouse();
                case 2:
                    return new RejectedListing();
                case 3:
                    return new AgentBookings();
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


    private void setupViewPager(ViewPager viewPager) {
        adapter.addFragment(new AgentAllListings(), "All Listings");
        adapter.addFragment(new ApprovedHouse(), "Approved");
        adapter.addFragment(new RejectedListing(), "Rejected");
        adapter.addFragment(new AgentBookings(), "Bookings");
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
