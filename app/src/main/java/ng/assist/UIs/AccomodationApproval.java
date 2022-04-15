package ng.assist.UIs;

import static ng.assist.UIs.Utils.PaginationScrollListener.PAGE_START;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ng.assist.AccomodationListings;
import ng.assist.Adapters.AccomodationApprovalAdapter;
import ng.assist.Adapters.AccomodationListingsAdapter;
import ng.assist.R;
import ng.assist.UIs.ViewModel.AccomodationListModel;

public class AccomodationApproval extends Fragment {


    private RecyclerView recyclerView;
    private AccomodationApprovalAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private String totalPage;
    private boolean isLoading = false;
    private String nextPageUrl;
    private ProgressBar progressBar,recyclerProgressbar;
    private LinearLayout rootLayout;
    private LinearLayout imageScrollLayout;
    View view;

    public AccomodationApproval() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_accomodation_approval, container, false);
        initView();
        return view;
    }

    private void initView(){

        recyclerProgressbar = view.findViewById(R.id.accommodation_recycler_progress);
        recyclerProgressbar.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.accommodation_loading_progress);
        rootLayout = view.findViewById(R.id.accommodation_root_layout);
        recyclerView = view.findViewById(R.id.accomodation_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);


        AccomodationListModel accomodationListModel = new AccomodationListModel();
        accomodationListModel.getAccomodationsPending();
        accomodationListModel.setAccomodationListReadyListener(new AccomodationListModel.AccomodationListReadyListener() {
            @Override
            public void onListReady(ArrayList<AccomodationListModel> listModelArrayList, String mNextPageUrl, String mTotalPage, int totalListingAvailable) {
                nextPageUrl = mNextPageUrl;
                totalPage = mTotalPage;
                adapter = new AccomodationApprovalAdapter(listModelArrayList,getContext());
                recyclerView.setAdapter(adapter);
                rootLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onEmpty(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                rootLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });



    }
}