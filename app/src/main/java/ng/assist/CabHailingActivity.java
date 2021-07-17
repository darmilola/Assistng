package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.RideDisplayAdapter;
import ng.assist.UIs.ViewModel.ZoomCenterCardLayoutManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CabHailingActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView currentLocationTextView;
    ArrayList<String> rideList = new ArrayList<>();
    RideDisplayAdapter rideDisplayAdapter;
    LinearLayout rideLayout;
    LinearLayout locationLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_hailing);
        initView();

    }

    private void initView(){


        rideLayout = findViewById(R.id.ride_display_layout);
        locationLayout = findViewById(R.id.cab_hailing_location_selection_layout);
        currentLocationTextView = findViewById(R.id.current_location_name);
        currentLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             rideLayout.setVisibility(View.GONE);
             locationLayout.setVisibility(View.VISIBLE);
            }
        });
        recyclerView = findViewById(R.id.ride_display_recyclerview);
        rideList = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            rideList.add("");
        }

        rideDisplayAdapter = new RideDisplayAdapter(rideList,CabHailingActivity.this);
        recyclerView.setAdapter(rideDisplayAdapter);
        ZoomCenterCardLayoutManager zoomCenterCardLayoutManager = new ZoomCenterCardLayoutManager(CabHailingActivity.this,LinearLayout.HORIZONTAL,false);
        recyclerView.setLayoutManager(zoomCenterCardLayoutManager);

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
