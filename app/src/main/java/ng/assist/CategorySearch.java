package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ng.assist.Adapters.ServiceProvidersAdapter;
import ng.assist.UIs.ViewModel.ServicesModel;

public class CategorySearch extends AppCompatActivity {

    LinearLayout backButton;
    TextView nothingToShow,title;
    RecyclerView recyclerView;
    String city,category,jobTitle;
    ServiceProvidersAdapter serviceProvidersAdapter;
    ProgressBar recyclerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_search);
        initView();
    }

    private void initView(){
        city = getIntent().getStringExtra("city");
        category = getIntent().getStringExtra("category");
        jobTitle = getIntent().getStringExtra("title");
        backButton = findViewById(R.id.services_back_nav);
        nothingToShow = findViewById(R.id.category_search_nothing_to_show);
        title = findViewById(R.id.category_search_title);
        recyclerView = findViewById(R.id.category_search_recyclerview);
        recyclerProgress = findViewById(R.id.progressbar);
        title.setText(jobTitle);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ServicesModel servicesModel = new ServicesModel(category,city,jobTitle);
        servicesModel.getServiceCategoryProvider();
        servicesModel.setServiceProviderListener(new ServicesModel.ServiceProviderListener() {
            @Override
            public void onProvidersReadyListener(ArrayList<ServicesModel> servicesModelArrayList, String nextPageUrl, String totalPage) {
                serviceProvidersAdapter = new ServiceProvidersAdapter(servicesModelArrayList,CategorySearch.this);
                recyclerView.setAdapter(serviceProvidersAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(CategorySearch.this,RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerProgress.setVisibility(View.GONE);
                nothingToShow.setVisibility(View.GONE);
            }
            @Override
            public void onError(String message) {
                recyclerView.setVisibility(View.GONE);
                recyclerProgress.setVisibility(View.GONE);
                nothingToShow.setVisibility(View.VISIBLE);
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