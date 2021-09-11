package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.AccomodationListingsAdapter;
import ng.assist.Adapters.RealEstateDashboardListingAdapter;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class EstateListingDashboard extends AppCompatActivity {

    RecyclerView recyclerView;
    RealEstateDashboardListingAdapter adapter;
    ArrayList<String> accomdationList = new ArrayList<>();
    LinearLayout agentAddListing;
    TextView agentName,agentPhone;
    ImageView agentPhoneSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate_listing_dashboard);
        initView();
    }

    private void initView(){

        agentAddListing = findViewById(R.id.house_agent_add_listing);
        agentName = findViewById(R.id.house_agent_name);
        agentPhone = findViewById(R.id.house_agent_phone_text);
        agentPhoneSelect = findViewById(R.id.house_agent_phone_select);

        agentAddListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EstateListingDashboard.this,RealEsateAgentAddListing.class));
            }
        });

        recyclerView = findViewById(R.id.real_estate_dashboard_recyclerview);
        for(int i = 0; i < 20; i++){
            accomdationList.add("");
        }

        adapter = new RealEstateDashboardListingAdapter(accomdationList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
