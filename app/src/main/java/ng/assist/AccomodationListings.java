package ng.assist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.AccomodationListingsAdapter;
import ng.assist.UIs.Utils.PaginationScrollListener;
import ng.assist.UIs.ViewModel.AccomodationListModel;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import static ng.assist.UIs.Utils.PaginationScrollListener.PAGE_START;

public class AccomodationListings extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AccomodationListingsAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private String totalPage;
    private boolean isLoading = false;
    private String nextPageUrl;
    private String selectedCity,houseType,maxPrice,minPrice;
    private ProgressBar progressBar,recyclerProgressbar;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_listings);
        initView();
    }

    private void initView(){
        recyclerProgressbar = findViewById(R.id.accommodation_recycler_progress);
        recyclerProgressbar.setVisibility(View.GONE);
        progressBar = findViewById(R.id.accommodation_loading_progress);
        rootLayout = findViewById(R.id.accommodation_root_layout);
        recyclerView = findViewById(R.id.accomodation_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        selectedCity = intent.getStringExtra("city");
        houseType = intent.getStringExtra("type");
        maxPrice = intent.getStringExtra("max_price");
        minPrice = intent.getStringExtra("min_price");


        AccomodationListModel accomodationListModel = new AccomodationListModel(houseType,selectedCity,maxPrice,minPrice);
        accomodationListModel.getAccomodations();
        accomodationListModel.setAccomodationListReadyListener(new AccomodationListModel.AccomodationListReadyListener() {
            @Override
            public void onListReady(ArrayList<AccomodationListModel> listModelArrayList, String nextPageUrl,String totalPage) {
                AccomodationListings.this.nextPageUrl = nextPageUrl;
                AccomodationListings.this.totalPage = totalPage;
                adapter = new AccomodationListingsAdapter(listModelArrayList,AccomodationListings.this);
                recyclerView.setAdapter(adapter);
                rootLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onEmpty(String message) {
                  Toast.makeText(AccomodationListings.this, message, Toast.LENGTH_LONG).show();
                  rootLayout.setVisibility(View.VISIBLE);
                  progressBar.setVisibility(View.GONE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){ //1 for down
                   if(nextPageUrl.equalsIgnoreCase("null")){
                        return;
                    }

                    recyclerProgressbar.setVisibility(View.VISIBLE);
                    AccomodationListModel accomodationListModel = new AccomodationListModel(houseType,selectedCity,maxPrice,minPrice);
                    accomodationListModel.getAccomodationsNextPage(AccomodationListings.this.nextPageUrl);
                    accomodationListModel.setAccomodationListReadyListener(new AccomodationListModel.AccomodationListReadyListener() {
                        @Override
                        public void onListReady(ArrayList<AccomodationListModel> listModelArrayList, String nextPageUrl,String totalPage) {
                            AccomodationListings.this.totalPage = totalPage;
                            AccomodationListings.this.nextPageUrl = nextPageUrl;
                            recyclerProgressbar.setVisibility(View.GONE);
                            adapter.addItem(listModelArrayList);
                        }
                        @Override
                        public void onEmpty(String message) {
                            Toast.makeText(AccomodationListings.this, message, Toast.LENGTH_LONG).show();
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
        }
    }
}
