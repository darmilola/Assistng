package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.AccomodationListingsAdapter;
import ng.assist.Adapters.RealEstateDashboardListingAdapter;
import ng.assist.UIs.Utils.InputDialog;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class EstateListingDashboard extends AppCompatActivity {

    RecyclerView recyclerView;
    RealEstateDashboardListingAdapter adapter;
    LinearLayout agentAddListing;
    TextView agentName,agentPhone;
    ImageView agentPhoneSelect;
    InputDialog inputDialog;
    EstateDashboardModel estateDashboardModel;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    LinearLayout noListingLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate_listing_dashboard);
        initView();
    }

    private void initView(){

        noListingLayout = findViewById(R.id.no_listing_layout);
        progressBar = findViewById(R.id.estate_dashboard_progress);
        nestedScrollView = findViewById(R.id.estate_dashboard_nested_scroll);
        agentAddListing = findViewById(R.id.house_agent_add_listing);
        agentName = findViewById(R.id.house_agent_name);
        agentPhone = findViewById(R.id.house_agent_phone_text);
        agentPhoneSelect = findViewById(R.id.house_agent_phone_select);
        recyclerView = findViewById(R.id.real_estate_dashboard_recyclerview);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EstateListingDashboard.this);
        String firstname =  preferences.getString("firstname","");
        String lastname =  preferences.getString("lastname","");
        agentName.setText(firstname+" "+lastname);

        estateDashboardModel = new EstateDashboardModel(EstateListingDashboard.this);
        estateDashboardModel.getAccomodations();
        estateDashboardModel.setEstateDashboardListener(new EstateDashboardModel.EstateDashboardListener() {
            @Override
            public void onInfoReady(ArrayList<AccomodationListModel> accomodationListModelArrayList, AgentModel agentModel) {
                progressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
                if(agentModel.getAgentPhone().equalsIgnoreCase("null")){
                    agentPhone.setText("Not Available");
                }
                else{
                    agentPhone.setText(agentModel.getAgentPhone());
                }


                if(accomodationListModelArrayList.size() == 0){
                    noListingLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else{
                    noListingLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new RealEstateDashboardListingAdapter(accomodationListModelArrayList,EstateListingDashboard.this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(EstateListingDashboard.this,LinearLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onError(String message) {
                Toast.makeText(EstateListingDashboard.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        agentAddListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EstateListingDashboard.this,RealEsateAgentAddListing.class));
            }
        });

        agentPhoneSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new InputDialog(EstateListingDashboard.this,"Phone");
                inputDialog.showInputDialog();
                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {
                        EstateDashboardModel estateDashboardModel = new EstateDashboardModel("phonenumber",text,EstateListingDashboard.this);
                        estateDashboardModel.updateProviderInfo();
                        estateDashboardModel.setUpdateInfoListener(new EstateDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                agentPhone.setText(text);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EstateListingDashboard.this);
                                preferences.edit().putString("phone",text).apply();
                            }

                            @Override
                            public void onError(String message) {

                            }
                        });
                    }

                    @Override
                    public void cancelClicked() {

                    }
                });
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
