package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import ng.assist.UIs.GroceryFastFoods;
import ng.assist.UIs.HomeFragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Grocery extends AppCompatActivity {


    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
    ViewPager viewPager;
    TabLayout tabLayout;
    EditText searchEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);
        initView();
    }

    private void initView(){

        viewPager = findViewById(R.id.grocery_viewpager);
        tabLayout = findViewById(R.id.grocery_tab_layout);
        searchEdittext = findViewById(R.id.grocery_search_edittext);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        searchEdittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(!searchEdittext.getText().toString().trim().equalsIgnoreCase("")){
                        Intent intent = new Intent(Grocery.this, GrocerySearch.class);
                        intent.putExtra("query",searchEdittext.getText().toString().trim());
                        startActivity(intent);
                    }
                    return true;
                }
                return false;
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
                    return new GroceryFastFoods();
                case 1:
                    return new GroceryFastFoods();
                case 2:
                    return new GroceryFastFoods();
                case 3:
                    return new GroceryFastFoods();



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

        adapter.addFragment(new GroceryFastFoods(), "Fast-Foods");
        adapter.addFragment(new GroceryFastFoods(), "Electronics");
        adapter.addFragment(new GroceryFastFoods(), "Gadgets");
        adapter.addFragment(new GroceryFastFoods(), "Clothings");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
             getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }





}
