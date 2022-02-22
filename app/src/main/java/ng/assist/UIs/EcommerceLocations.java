package ng.assist.UIs;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.DashBoardLocationsAdapter;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.LocationModel;


public class EcommerceLocations extends Fragment {


    RecyclerView recyclerView;
    DashBoardLocationsAdapter adapter;
    ArrayList<String> locationList = new ArrayList<>();
    View view;
    ListDialog listDialog;
    MaterialButton addLocation;
    ProgressBar progressBar;
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
        populateLocation();
        progressBar = view.findViewById(R.id.eccommerce_location_progress);
        recyclerView = view.findViewById(R.id.dashboard_locations_recyclerview);
        addLocation = view.findViewById(R.id.eccommerce_location_add_location);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String retailerId =  preferences.getString("userEmail","");

        LocationModel locationModel = new LocationModel(retailerId);
        locationModel.LoadDeliveryLocations();
        locationModel.setLocationReadyListener(new LocationModel.LocationReadyListener() {
            @Override
            public void onReady(ArrayList<LocationModel> assistLocations, ArrayList<LocationModel> retailerLocation) {
                addLocation.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                locationList = new ArrayList<>();
                for (LocationModel locationModel1: assistLocations) {
                     //locationList.add(locationModel1.getCity());
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new DashBoardLocationsAdapter(retailerLocation,getContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });


        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(locationList,getContext());
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {
                        LocationModel locationModel1 = new LocationModel(retailerId,city);
                        locationModel1.CreateDeliveryLocations();
                        locationModel1.setUpdateInfoListener(new LocationModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                adapter.addLocation(locationModel1);
                                Toast.makeText(getContext(), "Location Added Successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void populateLocation(){
        locationList.add("Lagos");
        locationList.add("Abuja");
        locationList.add("Kano");

        String[] stateList = {"Abia","Adamawa","Akwa Ibom","Anambra","Bauchi","Bayelsa","Benue","Borno","Cross River","Delta","Ebonyi","Edo","Ekiti","Enugu","Gombe","Imo","Jigawa","Kaduna","Kano","Katsina","Kebbi"
                ,"Kogi","Kwara","Lagos","Nasarawa","Niger","Ogun","Ondo","Osun","Oyo","Plateau","Rivers","Sokoto","Taraba","Yobe","Zamfara"};

        for (String city : stateList) {
            locationList.add(city);
        }

    }

}
