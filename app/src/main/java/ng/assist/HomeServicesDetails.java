package ng.assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.HomeServicesTypesAdapter;
import ng.assist.Adapters.ServiceProvidersAdapter;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.ServicesModel;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeServicesDetails extends AppCompatActivity {

    RecyclerView serviceProviderRecyclerview;
    ServiceProvidersAdapter serviceProvidersAdapter;
    ProgressBar recyclerProgress, loadingProgress;
    LinearLayout rootLayout;
    private String nextPageUrl;

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
        LinearLayoutManager providersLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        serviceProviderRecyclerview.setLayoutManager(providersLayoutManager);


        ServicesModel servicesModel = new ServicesModel("Home Services");
        servicesModel.getServiceProvider();
        servicesModel.setServiceProviderListener(new ServicesModel.ServiceProviderListener() {
            @Override
            public void onProvidersReadyListener(ArrayList<ServicesModel> servicesModelArrayList, String nextPageUrl, String totalPage) {
                HomeServicesDetails.this.nextPageUrl = nextPageUrl;
                serviceProvidersAdapter = new ServiceProvidersAdapter(servicesModelArrayList,HomeServicesDetails.this);
                serviceProviderRecyclerview.setAdapter(serviceProvidersAdapter);
                rootLayout.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.GONE);
            }
            @Override
            public void onError(String message) {
                rootLayout.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.GONE);
                Toast.makeText(HomeServicesDetails.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        serviceProviderRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){ //1 for down
                    if(nextPageUrl.equalsIgnoreCase("null")){
                        return;
                    }
                    recyclerProgress.setVisibility(View.VISIBLE);
                    ServicesModel servicesModel = new ServicesModel("home_service");
                    servicesModel.getServiceProviderNextPage(HomeServicesDetails.this.nextPageUrl);
                    servicesModel.setServiceProviderListener(new ServicesModel.ServiceProviderListener() {
                        @Override
                        public void onProvidersReadyListener(ArrayList<ServicesModel> servicesModelArrayList, String nextPageUrl, String totalPage) {
                            HomeServicesDetails.this.nextPageUrl = nextPageUrl;
                            //serviceProvidersAdapter = new ServiceProvidersAdapter(servicesModelArrayList,HomeServicesDetails.this);
                            serviceProvidersAdapter.addItem(servicesModelArrayList);
                            recyclerProgress.setVisibility(View.GONE);
                        }
                        @Override
                        public void onError(String message) {
                            recyclerProgress.setVisibility(View.GONE);
                            Toast.makeText(HomeServicesDetails.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
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
