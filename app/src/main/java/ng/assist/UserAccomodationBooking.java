package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ng.assist.Adapters.AccomodationListingsAdapter;
import ng.assist.Adapters.ProviderBookingAdapter;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.ProviderBookingsModel;

public class UserAccomodationBooking extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar serviceBookingProgress;
    TextView serviceBookingEmpty;
    EstateDashboardModel estateDashboardModel;
    AccomodationListingsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_accomodation_booking);
        initView();
    }

    private void initView(){
        serviceBookingProgress = findViewById(R.id.service_booking_progress);
        serviceBookingEmpty = findViewById(R.id.service_booking_empty_text);
        recyclerView = findViewById(R.id.service_booking_recyclerview);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = preferences.getString("userEmail","");
        estateDashboardModel = new EstateDashboardModel(userId);
        estateDashboardModel.getAccomodationsUserBookings();
        estateDashboardModel.setEstateDashboardListener(new EstateDashboardModel.EstateDashboardListener() {
            @Override
            public void onInfoReady(ArrayList<AccomodationListModel> accomodationListModelArrayList, AgentModel agentModel) {
                recyclerView.setVisibility(View.VISIBLE);
                serviceBookingProgress.setVisibility(View.GONE);
                serviceBookingEmpty.setVisibility(View.GONE);
                adapter = new AccomodationListingsAdapter(accomodationListModelArrayList,UserAccomodationBooking.this);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(UserAccomodationBooking.this,RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onError(String message) {
                recyclerView.setVisibility(View.GONE);
                serviceBookingProgress.setVisibility(View.GONE);
                serviceBookingEmpty.setVisibility(View.VISIBLE);
                Log.e("onEmpty: ",message);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        }
    }


}