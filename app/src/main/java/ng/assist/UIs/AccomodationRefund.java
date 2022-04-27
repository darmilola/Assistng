package ng.assist.UIs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ng.assist.AccomodationRefundsDetails;
import ng.assist.Adapters.RealEstateDashboardListingAdapter;
import ng.assist.EstateDashboardListingDetails;
import ng.assist.R;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;


public class AccomodationRefund extends Fragment {


    View view;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TextView noRefund;
    EstateDashboardModel estateDashboardModel;
    RealEstateDashboardListingAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view =  inflater.inflate(R.layout.fragment_accomodation_refund, container, false);
         initView();
         return view;
    }

    private void initView(){
        progressBar = view.findViewById(R.id.refund_progress);
        recyclerView = view.findViewById(R.id.refund_recycler);
        noRefund = view.findViewById(R.id.no_refund_available);

        estateDashboardModel = new EstateDashboardModel();
        estateDashboardModel.getAccomodationsRefunds();
        estateDashboardModel.setEstateDashboardListener(new EstateDashboardModel.EstateDashboardListener() {
            @Override
            public void onInfoReady(ArrayList<AccomodationListModel> accomodationListModelArrayList, AgentModel agentModel) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                noRefund.setVisibility(View.GONE);
                adapter = new RealEstateDashboardListingAdapter(accomodationListModelArrayList,getContext());
                recyclerView.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);

                adapter.setItemClickListener(new RealEstateDashboardListingAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        AccomodationListModel accomodationListModel = accomodationListModelArrayList.get(position);
                        Intent intent = new Intent(getContext(), AccomodationRefundsDetails.class);
                        intent.putExtra("accModel", accomodationListModel);
                        startActivity(intent);
                    }
                });

            }
            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noRefund.setVisibility(View.VISIBLE);
            }
        });


    }
}