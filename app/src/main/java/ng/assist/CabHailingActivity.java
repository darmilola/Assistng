package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.RideDisplayAdapter;
import ng.assist.UIs.ViewModel.CabHailingModel;
import ng.assist.UIs.ViewModel.ZoomCenterCardLayoutManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CabHailingActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView noRideAvailable;
    TextView currentLocationTextView;
    LinearLayout changeLocationText;
    ArrayList<String> rideList = new ArrayList<>();
    RideDisplayAdapter rideDisplayAdapter;
    LinearLayout rideLayout;
    LinearLayout locationLayout;
    String userCity;
    FrameLayout cabHailingRoot;
    ProgressBar progressBar;
    String from,to;
    CircleIndicator2 imagesIndicator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_hailing);
        initView();

    }

    private void initView(){
        changeLocationText = findViewById(R.id.change_location);
        noRideAvailable = findViewById(R.id.no_ride_available);
        cabHailingRoot = findViewById(R.id.cab_hailing_root);
        progressBar = findViewById(R.id.cab_hailing_progressbar);
        rideLayout = findViewById(R.id.ride_display_layout);
        locationLayout = findViewById(R.id.cab_hailing_location_selection_layout);
        currentLocationTextView = findViewById(R.id.current_location_name);
        from = getIntent().getStringExtra("from");
        to = getIntent().getStringExtra("to");
        imagesIndicator = findViewById(R.id.product_image_indicator);
        currentLocationTextView.setText(from+" - "+to);
        recyclerView = findViewById(R.id.ride_display_recyclerview);
        changeLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       // ZoomCenterCardLayoutManager zoomCenterCardLayoutManager = new ZoomCenterCardLayoutManager(CabHailingActivity.this,LinearLayout.HORIZONTAL,false);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);

        CabHailingModel cabHailingModel = new CabHailingModel(from,to);
        cabHailingModel.SearchTransports();
        cabHailingModel.setCabHailingListener(new CabHailingModel.CabHailingListener() {
            @Override
            public void onReady(ArrayList<CabHailingModel> cabHailingModelArrayList) {
                rideDisplayAdapter = new RideDisplayAdapter(cabHailingModelArrayList,CabHailingActivity.this);
                recyclerView.setAdapter(rideDisplayAdapter);
                cabHailingRoot.setVisibility(View.VISIBLE);
                noRideAvailable.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
                pagerSnapHelper.attachToRecyclerView(recyclerView);
                imagesIndicator.attachToRecyclerView(recyclerView, pagerSnapHelper);
                rideDisplayAdapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                noRideAvailable.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
          }
    }
}
