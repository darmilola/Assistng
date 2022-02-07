package ng.assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
    NoSwipeViewPager viewPager;
    ProgressBar mainActivityContentProgressbar;
    LinearLayout bottomBarLayout;
    String userWalletBalance,userFirstname;

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
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0, false);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setSelected(true);
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
                setupViewPager(viewPager);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

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



    @Override
    public void onBackPressed(){

    }


    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }





}
