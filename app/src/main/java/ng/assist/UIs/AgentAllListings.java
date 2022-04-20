package ng.assist.UIs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import ng.assist.Adapters.RealEstateDashboardListingAdapter;
import ng.assist.EstateDashboardListingDetails;
import ng.assist.EstateListingDashboard;
import ng.assist.R;
import ng.assist.RealEsateAgentAddListing;
import ng.assist.UIs.Utils.InputDialog;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;


public class AgentAllListings extends Fragment {

    RecyclerView recyclerView;
    RealEstateDashboardListingAdapter adapter;
    LinearLayout agentAddListing;
    TextView agentName, agentPhone;
    ImageView agentPhoneSelect;
    InputDialog inputDialog;
    EstateDashboardModel estateDashboardModel;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    LinearLayout noListingLayout;
    LinearLayout errorOccuredLayout;
    MaterialButton retry;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_agent_all_listings, container, false);
        initView();
        return view;
    }

    private void initView() {

        errorOccuredLayout = view.findViewById(R.id.error_occurred_layout_root);
        retry = view.findViewById(R.id.error_occurred_retry);
        noListingLayout = view.findViewById(R.id.no_listing_layout);
        progressBar = view.findViewById(R.id.estate_dashboard_progress);
        nestedScrollView = view.findViewById(R.id.estate_dashboard_nested_scroll);
        agentAddListing = view.findViewById(R.id.house_agent_add_listing);
        agentName = view.findViewById(R.id.house_agent_name);
        agentPhone = view.findViewById(R.id.house_agent_phone_text);
        agentPhoneSelect = view.findViewById(R.id.house_agent_phone_select);
        recyclerView = view.findViewById(R.id.real_estate_dashboard_recyclerview);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String firstname = preferences.getString("firstname", "");
        String lastname = preferences.getString("lastname", "");
        agentName.setText(firstname + " " + lastname);

        loadPage();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage();
            }
        });

        agentAddListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), RealEsateAgentAddListing.class), 100);
            }
        });

        agentPhoneSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new InputDialog(getContext(), "Phone");
                inputDialog.showInputDialog();
                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {
                        EstateDashboardModel estateDashboardModel = new EstateDashboardModel("phonenumber", text, getContext());
                        estateDashboardModel.updateProviderInfo();
                        estateDashboardModel.setUpdateInfoListener(new EstateDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                agentPhone.setText(text);
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                preferences.edit().putString("phone", text).apply();
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


    public void loadPage(){

        progressBar.setVisibility(View.VISIBLE);
        errorOccuredLayout.setVisibility(View.GONE);

        estateDashboardModel = new EstateDashboardModel(getContext());
        estateDashboardModel.getAccomodations();
        estateDashboardModel.setEstateDashboardListener(new EstateDashboardModel.EstateDashboardListener() {
            @Override
            public void onInfoReady(ArrayList<AccomodationListModel> accomodationListModelArrayList, AgentModel agentModel) {
                progressBar.setVisibility(View.GONE);
                errorOccuredLayout.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
                if (agentModel.getAgentPhone().equalsIgnoreCase("null")) {
                    agentPhone.setText("Not Available");
                } else {
                    agentPhone.setText(agentModel.getAgentPhone());
                }
                if (accomodationListModelArrayList.size() == 0) {
                    noListingLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noListingLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new RealEstateDashboardListingAdapter(accomodationListModelArrayList, getContext());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    adapter.setItemClickListener(new RealEstateDashboardListingAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            AccomodationListModel accomodationListModel = accomodationListModelArrayList.get(position);
                            Intent intent = new Intent(getContext(), EstateDashboardListingDetails.class);
                            intent.putExtra("accModel", accomodationListModel);
                            startActivityForResult(intent,300);
                        }
                    });
                }

            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                errorOccuredLayout.setVisibility(View.VISIBLE);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == 100) {
            progressBar.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
            estateDashboardModel = new EstateDashboardModel(getContext());
            estateDashboardModel.getAccomodations();
            estateDashboardModel.setEstateDashboardListener(new EstateDashboardModel.EstateDashboardListener() {
                @Override
                public void onInfoReady(ArrayList<AccomodationListModel> accomodationListModelArrayList, AgentModel agentModel) {
                    progressBar.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                    if (agentModel.getAgentPhone().equalsIgnoreCase("null")) {
                        agentPhone.setText("Not Available");
                    } else {
                        agentPhone.setText(agentModel.getAgentPhone());
                    }


                    if (accomodationListModelArrayList.size() == 0) {
                        noListingLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noListingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new RealEstateDashboardListingAdapter(accomodationListModelArrayList, getContext());
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                        adapter.setItemClickListener(new RealEstateDashboardListingAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                AccomodationListModel accomodationListModel = accomodationListModelArrayList.get(position);
                                Intent intent = new Intent(getContext(),EstateDashboardListingDetails.class);
                                intent.putExtra("accModel", accomodationListModel);
                                startActivityForResult(intent,300);
                            }
                        });
                    }

                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

        }


        if (requestCode == 300 && resultCode == 300) {
            progressBar.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
            estateDashboardModel = new EstateDashboardModel(getContext());
            estateDashboardModel.getAccomodations();
            estateDashboardModel.setEstateDashboardListener(new EstateDashboardModel.EstateDashboardListener() {
                @Override
                public void onInfoReady(ArrayList<AccomodationListModel> accomodationListModelArrayList, AgentModel agentModel) {
                    progressBar.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);
                    if (agentModel.getAgentPhone().equalsIgnoreCase("null")) {
                        agentPhone.setText("Not Available");
                    } else {
                        agentPhone.setText(agentModel.getAgentPhone());
                    }


                    if (accomodationListModelArrayList.size() == 0) {
                        noListingLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noListingLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter = new RealEstateDashboardListingAdapter(accomodationListModelArrayList, getContext());
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                        adapter.setItemClickListener(new RealEstateDashboardListingAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                AccomodationListModel accomodationListModel = accomodationListModelArrayList.get(position);
                                Intent intent = new Intent(getContext(),EstateDashboardListingDetails.class);
                                intent.putExtra("accModel", accomodationListModel);
                                startActivityForResult(intent,300);
                            }
                        });
                    }

                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}