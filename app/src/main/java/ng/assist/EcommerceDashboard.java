package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ng.assist.UIs.EcommerceLocations;
import ng.assist.UIs.EcommerceOrders;
import ng.assist.UIs.EcommerceProduct;
import ng.assist.UIs.GroceryFastFoods;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class EcommerceDashboard extends AppCompatActivity {

    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    ViewPager viewPager;
    TabLayout tabLayout,tabLayout1;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    AppBarLayout appBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecommerce_dashboard);
        initView();
    }

    private void initView(){
        viewPager = findViewById(R.id.ecommerce_dashboard_pager);
        tabLayout = findViewById(R.id.ecommerce_dashboard_tabs);
        collapsingToolbarLayout = findViewById(R.id.ecommerce_collapsing_toolbar_layout);
        appBar = findViewById(R.id.ecommerce_dashboard_app_bar);
        tabLayout1 = findViewById(R.id.ecommerce_dashboard_tabs1);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout1.setupWithViewPager(viewPager);


        appBar.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {

            boolean isShown = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if(scrollRange + verticalOffset == 0){

                    tabLayout1.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);
                    isShown = true;
                    return;
                }
                else if(isShown){


                    tabLayout1.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    isShown = false;
                    return;
                }
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
                    return new EcommerceProduct();
                case 1:
                    return new EcommerceOrders();
                case 2:
                    return new EcommerceLocations();
            }
            return null;
        }

        @Override
        public int getCount() {

            return 3;
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

        adapter.addFragment(new EcommerceProduct(), "Products");
        adapter.addFragment(new EcommerceOrders(), "Orders");
        adapter.addFragment(new EcommerceLocations(), "Locations");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }
}
