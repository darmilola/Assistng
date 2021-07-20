package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.RideDisplayAdapter;
import ng.assist.UIs.ViewModel.CabHailingModel;
import ng.assist.UIs.ViewModel.ZoomCenterCardLayoutManager;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    TextView currentLocationTextView;
    ArrayList<String> rideList = new ArrayList<>();
    RideDisplayAdapter rideDisplayAdapter;
    LinearLayout rideLayout;
    LinearLayout locationLayout;
    String userCity;
    FrameLayout cabHailingRoot;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_hailing);
        initView();

    }

    private void initView(){
        cabHailingRoot = findViewById(R.id.cab_hailing_root);
        progressBar = findViewById(R.id.cab_hailing_progressbar);
        rideLayout = findViewById(R.id.ride_display_layout);
        locationLayout = findViewById(R.id.cab_hailing_location_selection_layout);
        currentLocationTextView = findViewById(R.id.current_location_name);
        recyclerView = findViewById(R.id.ride_display_recyclerview);
        ZoomCenterCardLayoutManager zoomCenterCardLayoutManager = new ZoomCenterCardLayoutManager(CabHailingActivity.this,LinearLayout.HORIZONTAL,false);
        recyclerView.setLayoutManager(zoomCenterCardLayoutManager);
        userCity = PreferenceManager.getDefaultSharedPreferences(this).getString("city","lagos");
        rideList = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            rideList.add("");
        }
        CabHailingModel cabHailingModel = new CabHailingModel(userCity);
        cabHailingModel.SearchCabDrivers();
        cabHailingModel.setCabHailingListener(new CabHailingModel.CabHailingListener() {
            @Override
            public void onReady(ArrayList<CabHailingModel> cabHailingModelArrayList) {
                rideDisplayAdapter = new RideDisplayAdapter(cabHailingModelArrayList,CabHailingActivity.this);
                recyclerView.setAdapter(rideDisplayAdapter);
                cabHailingRoot.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CabHailingActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          //  getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.transparent));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }
}
