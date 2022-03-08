package ng.assist.UIs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ng.assist.Adapters.ProviderBookingAdapter;
import ng.assist.R;


public class AllBookings extends Fragment {

    View view;
    RecyclerView recyclerView;
    ProviderBookingAdapter adapter;
    ArrayList<String> bookingList = new ArrayList<>();


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
        recyclerView = view.findViewById(R.id.service_booking_recyclerview);

        for(int i = 0; i < 20; i++){
            bookingList.add("");
        }

        adapter = new ProviderBookingAdapter(bookingList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}