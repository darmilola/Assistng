package ng.assist.UIs;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ng.assist.Adapters.ProviderBookingAdapter;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.ViewModel.ProviderBookingsModel;


public class AllBookings extends Fragment {

    View view;
    RecyclerView recyclerView;
    ProviderBookingAdapter adapter;
    ProviderBookingsModel providerBookingsModel;
    ProgressBar serviceBookingProgress;
    TextView serviceBookingEmpty;

    public AllBookings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_all_bookings, container, false);
        initView();
        return  view;
    }

    private void initView(){
        serviceBookingProgress = view.findViewById(R.id.service_booking_progress);
        serviceBookingEmpty = view.findViewById(R.id.service_booking_empty_text);
        recyclerView = view.findViewById(R.id.service_booking_recyclerview);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userEmail = preferences.getString("userEmail","");

        providerBookingsModel = new ProviderBookingsModel(userEmail);

        providerBookingsModel.getAllProviderBookings();
        providerBookingsModel.setProviderBookingsReadyListener(new ProviderBookingsModel.ProviderBookingsReadyListener() {
            @Override
            public void onListReady(ArrayList<ProviderBookingsModel> listModelArrayList) {
                adapter = new ProviderBookingAdapter(listModelArrayList,getContext());
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                serviceBookingEmpty.setVisibility(View.GONE);
                serviceBookingProgress.setVisibility(View.GONE);
            }

            @Override
            public void onEmpty(String message) {
                recyclerView.setVisibility(View.GONE);
                serviceBookingEmpty.setVisibility(View.VISIBLE);
                serviceBookingProgress.setVisibility(View.GONE);
            }
        });



    }
}