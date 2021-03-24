package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.PortfolioAdapter;
import ng.assist.Adapters.ServiceProvidersAdapter;
import ng.assist.UIs.ItemDecorator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ServiceProviderDetails extends AppCompatActivity {


    RecyclerView recyclerView;
    PortfolioAdapter adapter;
    ArrayList<String> portfolioItemList = new ArrayList<>();
    LinearLayout hireMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_details);
        initView();
    }

    private void initView() {

        hireMe = findViewById(R.id.service_provider_hireme);
        recyclerView = findViewById(R.id.portfolio_recyclerview);
        for (int i = 0; i < 10; i++) {
            portfolioItemList.add("");
        }
        adapter = new PortfolioAdapter(portfolioItemList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        new PagerSnapHelper().attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new ItemDecorator(dpToPx(3), dpToPx(5), dpToPx(30), ContextCompat.getColor(this, R.color.White), ContextCompat.getColor(this, R.color.pinkypinky)));


        hireMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServiceProviderDetails.this,ChatActivity.class));
            }
        });

    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

}