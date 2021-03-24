package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.BusesAdapter;
import ng.assist.Adapters.BusesDatesAdapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class BusTransport extends AppCompatActivity {

    RecyclerView busDatesRecyclerview;
    RecyclerView busesRecyclerview;
    BusesAdapter busesAdapter;
    BusesDatesAdapter busesDatesAdapter;
    LinearLayoutManager busesLayoutManager;
    LinearLayoutManager busesDatesLayoutManager;
    ArrayList<String> stringArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_transport);
        initView();

    }

    private void initView(){

        busDatesRecyclerview = findViewById(R.id.bus_dates_recycler_view);
        busesRecyclerview = findViewById(R.id.buses_recyclerview);
        busesLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        busesDatesLayoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);

        stringArrayList = new ArrayList<>();
        for(int i = 0; i < 15; i++){

            stringArrayList.add("");
        }
        busesAdapter = new BusesAdapter(stringArrayList, this, new BusesAdapter.BusesClickedListener() {
            @Override
            public void onBusCliked() {
                startActivity(new Intent(BusTransport.this,BusCheckOut.class));
            }
        });
        busesDatesAdapter = new BusesDatesAdapter(stringArrayList,this);

        busesRecyclerview.setLayoutManager(busesLayoutManager);
        busDatesRecyclerview.setLayoutManager(busesDatesLayoutManager);
        busesRecyclerview.setAdapter(busesAdapter);
        busDatesRecyclerview.setAdapter(busesDatesAdapter);

    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background ));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.special_activity_background ));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
          //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }
}
