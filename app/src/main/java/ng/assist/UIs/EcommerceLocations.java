package ng.assist.UIs;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.DashBoardLocationsAdapter;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.R;


public class EcommerceLocations extends Fragment {


    RecyclerView recyclerView;
    DashBoardLocationsAdapter adapter;
    ArrayList<String> locationList = new ArrayList<>();
    View view;
    public EcommerceLocations() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_ecommerce_locations, container, false);
        initView();
        return view;
    }
    private void initView(){
        recyclerView = view.findViewById(R.id.dashboard_locations_recyclerview);

        for(int i = 0; i < 20; i++){
            locationList.add("");
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new DashBoardLocationsAdapter(locationList,getContext());
            recyclerView.setAdapter(adapter);

        }

    }


}
