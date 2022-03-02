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
import ng.assist.UIs.HomeFragment;
import ng.assist.UIs.Utils.InputDialog;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;

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

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class EcommerceDashboard extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout,tabLayout1;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    AppBarLayout appBar;
    TextView storeName, mPhone;
    ImageView phoneSelect,nameSelect;
    InputDialog inputDialog;
    EcommerceDashboardModel ecommerceDashboardModel;
    ProgressBar progressBar;
    String phonenumber,shopName;
    ViewPagerAdapter adapter;
    ImageView navBack;
    String userId;
    LinearLayout errorLayout;
    MaterialButton retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecommerce_dashboard);
        initView();
    }

    private void initView(){

        errorLayout = findViewById(R.id.error_occurred_layout_root);
        retry = findViewById(R.id.error_occurred_retry);
        navBack = findViewById(R.id.nav_back);
        progressBar = findViewById(R.id.ecc_progress);
        phoneSelect = findViewById(R.id.ecommerce_dashboard_phone_select);
        nameSelect = findViewById(R.id.ecommerce_dashboard_store_name_select);
        storeName = findViewById(R.id.ecommerce_dashboard_store_name);
        mPhone = findViewById(R.id.ecommerce_dashboard_phone);
        viewPager = findViewById(R.id.ecommerce_dashboard_pager);
        tabLayout = findViewById(R.id.ecommerce_dashboard_tabs);
        collapsingToolbarLayout = findViewById(R.id.ecommerce_collapsing_toolbar_layout);
        appBar = findViewById(R.id.ecommerce_dashboard_app_bar);
        tabLayout1 = findViewById(R.id.ecommerce_dashboard_tabs1);
        toolbar = findViewById(R.id.wallet_toolbar);
        appBar.setVisibility(View.GONE);

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EcommerceDashboard.this);
        userId = preferences.getString("userEmail","");

        loadPage();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage();
            }
        });




        phoneSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialog inputDialog = new InputDialog(EcommerceDashboard.this,"Phonenumber");
                inputDialog.showInputDialog();
                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {
                            mPhone.setText(text);
                            EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(userId,shopName,text,EcommerceDashboard.this);
                            ecommerceDashboardModel.updateRetailerInfo();
                            ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(EcommerceDashboard.this, "phonenumber Changed successfully", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String message) {
                                    Toast.makeText(EcommerceDashboard.this, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                    }

                    @Override
                    public void cancelClicked() {

                    }
                });
            }
        });

        nameSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialog inputDialog = new InputDialog(EcommerceDashboard.this,"Shop Name");
                inputDialog.showInputDialog();
                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {
                        storeName.setText(text);
                        EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(userId,text,phonenumber,EcommerceDashboard.this);
                        ecommerceDashboardModel.updateRetailerInfo();
                        ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(EcommerceDashboard.this, "Shop name Changed successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(EcommerceDashboard.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void cancelClicked() {

                    }
                });
            }
        });

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
                    toolbar.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.GONE);
                    isShown = true;
                    return;
                }
                else if(isShown){
                    tabLayout1.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.GONE);
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
                    EcommerceProduct ecommerceProduct = new EcommerceProduct();
                    Bundle data = new Bundle();
                    data.putString("phone", EcommerceDashboard.this.phonenumber);
                    data.putString("shopname", EcommerceDashboard.this.shopName);
                    ecommerceProduct.setArguments(data);
                    return ecommerceProduct;
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

    void loadPage(){
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        ecommerceDashboardModel = new EcommerceDashboardModel(EcommerceDashboard.this,userId);
        ecommerceDashboardModel.createRetailerInfo();
        ecommerceDashboardModel.setCreateInfoListener(new EcommerceDashboardModel.CreateInfoListener() {
            @Override
            public void onSuccess(String phone, String shopname) {
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                appBar.setVisibility(View.VISIBLE);
                phonenumber = phone;
                shopName = shopname;

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EcommerceDashboard.this);
                preferences.edit().putString("phone",phone).apply();
                preferences.edit().putString("shopName",shopname).apply();

                if(shopName.equalsIgnoreCase("null")){
                    storeName.setText("Not Available");
                }
                else{
                    storeName.setText(shopname);
                }
                if(phonenumber.equalsIgnoreCase("null")){
                    mPhone.setText("Not Available");
                }
                else{
                    mPhone.setText(phone);
                }
                adapter = new ViewPagerAdapter(getSupportFragmentManager());
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
                tabLayout1.setupWithViewPager(viewPager);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });

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
