package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ng.assist.UIs.HomeFragment;
import ng.assist.UIs.SendMoneyAmount;
import ng.assist.UIs.SendMoneyRecepient;
import ng.assist.UIs.SendMoneySuccess;
import ng.assist.UIs.ViewModel.RecipientUserInfo;
import ng.assist.UIs.ViewModel.SendMoneyModel;
import ng.assist.UIs.Wallet;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

public class SendMoney extends AppCompatActivity implements SendMoneyRecepient.onRecipientReady,SendMoneyAmount.SendMoneyListener {


    viewPagerAdapter adapter = new viewPagerAdapter(getSupportFragmentManager());
    NoSwipeViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_send_money);
        viewPager = findViewById(R.id.send_money_viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(-1);
    }

    @Override
    public void onReady(RecipientUserInfo recipientUserInfo) {
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void onSendSuccess() {
        viewPager.setCurrentItem(2, true);
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
                    return new SendMoneyRecepient();
                case 1:
                    SendMoneyAmount sendMoneyAmount = new SendMoneyAmount();
                    return sendMoneyAmount;
                case 2:
                    SendMoneySuccess sendMoneySuccess = new SendMoneySuccess();
                    return sendMoneySuccess;

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
   private void setupViewPager(ViewPager viewPager) {

        adapter.addFragment(new SendMoneyRecepient(), "Recipient");
        adapter.addFragment(new SendMoneyAmount(), "Amount");
        adapter.addFragment(new SendMoneySuccess(), "Success");
        viewPager.setAdapter(adapter);
    }




    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
        }
    }

    @Override
    public void onBackPressed(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SendMoney.this);
        preferences.edit().remove("sendMoneyFirstname").apply();
        preferences.edit().remove("sendMoneyLastname").apply();
        preferences.edit().remove("sendMoneyEmail").apply();
        preferences.edit().remove("sendMoneyImageUrl").apply();
        super.onBackPressed();
    }

}
