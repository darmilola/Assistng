package ng.assist.UIs;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ng.assist.Adapters.AccomodationListingsAdapter;
import ng.assist.Adapters.AgentAccomodationBookingAdapter;
import ng.assist.R;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;


public class AgentBookings extends Fragment {

    private RecyclerView recyclerView;
    private AgentAccomodationBookingAdapter adapter;
    private ProgressBar progressBar;
    LinearLayout noLayout;
    View view;
    EstateDashboardModel estateDashboardModel;


    public AgentBookings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view =  inflater.inflate(R.layout.fragment_agent_bookings, container, false);
         initView();
         return view;
    }

    private void initView(){
        recyclerView = view.findViewById(R.id.house_agent_approval_recyclerview);
        progressBar = view.findViewById(R.id.house_agent_approval_progress);
        noLayout = view.findViewById(R.id.no_listing_layout);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = preferences.getString("userEmail","");

        estateDashboardModel = new EstateDashboardModel(userId);
        estateDashboardModel.getAccomodationsBookings();
        estateDashboardModel.setEstateDashboardListener(new EstateDashboardModel.EstateDashboardListener() {
            @Override
            public void onInfoReady(ArrayList<AccomodationListModel> accomodationListModelArrayList, AgentModel agentModel) {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                noLayout.setVisibility(View.GONE);
                adapter = new AgentAccomodationBookingAdapter(accomodationListModelArrayList,getContext());
                recyclerView.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onError(String message) {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                noLayout.setVisibility(View.VISIBLE);
                Log.e("onEmpty: ",message);
            }
        });
    }
}