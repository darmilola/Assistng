package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.HomeServicesTypesAdapter;
import ng.assist.Adapters.ServiceProvidersAdapter;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class HomeServicesDetails extends AppCompatActivity {

    RecyclerView serviceProviderRecyclerview;
    ArrayList<String> serviceProviderList = new ArrayList<>();
    ServiceProvidersAdapter serviceProvidersAdapter;
    ProgressBar recyclerProgress, loadingProgress;
    LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_services_details);
        initView();
    }

    private void initView(){

        recyclerProgress = findViewById(R.id.home_services_recycler_progress);
        rootLayout = findViewById(R.id.home_services_root_layout);
        loadingProgress = findViewById(R.id.home_services_progressbar);
        serviceProviderRecyclerview = findViewById(R.id.home_services_experts_recyclerview);

        serviceProvidersAdapter = new ServiceProvidersAdapter(serviceProviderList,this);
        LinearLayoutManager providersLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        serviceProviderRecyclerview.setLayoutManager(providersLayoutManager);
        serviceProviderRecyclerview.setAdapter(serviceProvidersAdapter);

    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }
}
