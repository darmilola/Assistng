package ng.assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import ng.assist.UIs.AccountFragments;
import ng.assist.UIs.DmFragment;
import ng.assist.UIs.HomeFragment;
import ng.assist.UIs.ViewModel.MainActivityModel;
import ng.assist.UIs.Wallet;
import ng.assist.UIs.WalletKt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DmFragment.UnreadReadyListener {

    BottomNavigationView bottomNavigationView;
    viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
    NoSwipeViewPager viewPager;
    ProgressBar mainActivityContentProgressbar;
    LinearLayout bottomBarLayout,errorOccuredRoot;
    String userWalletBalance,userFirstname;
    MaterialButton retry;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }


    private void initView() {
        mainActivityContentProgressbar = findViewById(R.id.mainactivity_content_loader_progress);
        bottomBarLayout = findViewById(R.id.bottomnav_root_layout);
        bottomNavigationView = findViewById(R.id.chip_navigation);
        viewPager = findViewById(R.id.content_frame);
        errorOccuredRoot = findViewById(R.id.error_occurred_layout_root);
        retry = findViewById(R.id.error_occurred_retry);
        swipeContainer = findViewById(R.id.swipeContainer);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0, false);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setSelected(true);
        if(isPendingAvailable()){
            addWalletBadge();
        }


        refreshPage();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshPage();
            }
        });


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshPage();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);




        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.home) {
                    viewPager.setCurrentItem(0, false);
                } else if (i == R.id.wallet) {
                    viewPager.setCurrentItem(1, false);
                } else if (i == R.id.explore) {
                    viewPager.setCurrentItem(2, false);
                } else if (i == R.id.my_dm) {
                    viewPager.setCurrentItem(3, false);
                }
                return true;
            }
        });

    }

    @Override
    public void onUnreadReady(int count) {
        addUnreadBadge(count);
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

                    HomeFragment homeFragment = new HomeFragment();
                    Bundle data = new Bundle();
                    data.putString("walletBalance", MainActivity.this.userWalletBalance);
                    data.putString("firstname",MainActivity.this.userFirstname);
                    homeFragment.setArguments(data);

                    return homeFragment;
                case 1:
                    WalletKt wallet = new WalletKt();
                    Bundle bdata = new Bundle();
                    bdata.putString("walletBalance", MainActivity.this.userWalletBalance);
                    wallet.setArguments(bdata);
                    return wallet;
                case 2:
                    return new DmFragment();
                case 3:
                    return new AccountFragments();



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

        HomeFragment homeFragment = new HomeFragment();
        Bundle data = new Bundle();
        data.putString("walletBalance", MainActivity.this.userWalletBalance);
        data.putString("firstname",MainActivity.this.userFirstname);
        homeFragment.setArguments(data);
        adapter.addFragment(homeFragment, "Home");
        adapter.addFragment(new WalletKt(), "Wallet");
        adapter.addFragment(new DmFragment(), "Dm");
        adapter.addFragment(new AccountFragments(), "Accounts");
        viewPager.setAdapter(adapter);

    }

    public void refreshPage(){
        mainActivityContentProgressbar.setVisibility(View.VISIBLE);
        errorOccuredRoot.setVisibility(View.GONE);
        String userEmail = getIntent().getStringExtra("email");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        preferences.edit().putString("userEmail",userEmail).apply();
        MainActivityModel mainActivityModel = new MainActivityModel(userEmail,MainActivity.this);
        mainActivityModel.getUserInfo();
        mainActivityModel.setMainactivityContentListener(new MainActivityModel.MainactivityContentListener() {
            @Override
            public void onContentReady(MainActivityModel mainActivityModel) {
                viewPager.setVisibility(View.VISIBLE);
                bottomBarLayout.setVisibility(View.VISIBLE);
                mainActivityContentProgressbar.setVisibility(View.GONE);
                MainActivity.this.userFirstname = mainActivityModel.getUserFirstname();
                MainActivity.this.userWalletBalance = mainActivityModel.getUserWalletBalance();
                preferences.edit().putString("walletBalance",userWalletBalance).apply();
                preferences.edit().putString("firstname",mainActivityModel.getUserFirstname()).apply();
                preferences.edit().putString("lastname",mainActivityModel.getUserLastname()).apply();
                preferences.edit().putString("imageUrl",mainActivityModel.getUserImageUrl()).apply();
                preferences.edit().putString("accountType",mainActivityModel.getAccountType()).apply();
                preferences.edit().putString("isVerified",mainActivityModel.getIsVerified()).apply();
                preferences.edit().putString("verificationStatus",mainActivityModel.getVerificationStatus()).apply();
                preferences.edit().putString("role",mainActivityModel.getRole()).apply();
                preferences.edit().putString("verificationType",mainActivityModel.getVerificationType()).apply();
                preferences.edit().putString("canApply",mainActivityModel.getCanApplyForLoan()).apply();
                setupViewPager(viewPager);
            }
            @Override
            public void onError(String message) {
                mainActivityContentProgressbar.setVisibility(View.GONE);
                errorOccuredRoot.setVisibility(View.VISIBLE);

            }
        });

        if(!isVerified()){
            addVerificationBadge();
        }

    }


    public void swipeRefreshPage(){
        errorOccuredRoot.setVisibility(View.GONE);
        String userEmail = getIntent().getStringExtra("email");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        preferences.edit().putString("userEmail",userEmail).apply();
        MainActivityModel mainActivityModel = new MainActivityModel(userEmail,MainActivity.this);
        mainActivityModel.getUserInfo();
        mainActivityModel.setMainactivityContentListener(new MainActivityModel.MainactivityContentListener() {
            @Override
            public void onContentReady(MainActivityModel mainActivityModel) {
                swipeContainer.setRefreshing(false);
                viewPager.setVisibility(View.VISIBLE);
                bottomBarLayout.setVisibility(View.VISIBLE);
                mainActivityContentProgressbar.setVisibility(View.GONE);
                MainActivity.this.userFirstname = mainActivityModel.getUserFirstname();
                MainActivity.this.userWalletBalance = mainActivityModel.getUserWalletBalance();
                preferences.edit().putString("walletBalance",userWalletBalance).apply();
                preferences.edit().putString("firstname",mainActivityModel.getUserFirstname()).apply();
                preferences.edit().putString("lastname",mainActivityModel.getUserLastname()).apply();
                preferences.edit().putString("imageUrl",mainActivityModel.getUserImageUrl()).apply();
                preferences.edit().putString("accountType",mainActivityModel.getAccountType()).apply();
                preferences.edit().putString("isVerified",mainActivityModel.getIsVerified()).apply();
                preferences.edit().putString("verificationStatus",mainActivityModel.getVerificationStatus()).apply();
                preferences.edit().putString("role",mainActivityModel.getRole()).apply();
                preferences.edit().putString("verificationType",mainActivityModel.getVerificationType()).apply();
                preferences.edit().putString("canApply",mainActivityModel.getCanApplyForLoan()).apply();
                setupViewPager(viewPager);
            }
            @Override
            public void onError(String message) {
                swipeContainer.setRefreshing(false);
                mainActivityContentProgressbar.setVisibility(View.GONE);
                errorOccuredRoot.setVisibility(View.VISIBLE);

            }
        });

        if(!isVerified()){
            addVerificationBadge();
        }

    }


    private void addWalletBadge(){
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.wallet);
        badgeDrawable.setVisible(true);
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.pink));
    }

    private void addUnreadBadge(int count){
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.explore);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(count);
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.pink));
    }

    private void addVerificationBadge(){
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.my_dm);
        badgeDrawable.setVisible(true);
        badgeDrawable.setBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.pink));
    }



    @Override
    public void onBackPressed(){

    }


    @Override
    public void onResume() {
        if(isPendingAvailable()){
            addWalletBadge();
        }
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        }
    }

    private boolean isPendingAvailable(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        boolean isAvailable =  preferences.getBoolean("isPending",false);
        return isAvailable;
    }

    private boolean isVerified(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String isVerified =  preferences.getString("isVerified","false");
        if(isVerified.equalsIgnoreCase("true")){
            return true;
        }
        else{
            return false;
        }
    }







}
