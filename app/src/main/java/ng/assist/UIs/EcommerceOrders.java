package ng.assist.UIs;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import ng.assist.DashboardViewOrder;
import ng.assist.UIs.ViewModel.Orders;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.DashboardOrdersAdapter;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcommerceOrders extends Fragment {


    RecyclerView recyclerView;
    DashboardOrdersAdapter adapter;
    ArrayList<String> orderList = new ArrayList<>();
    ProgressBar progressBar;
    View view;
    String userId;
    public EcommerceOrders() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ecommerce_orders, container, false);
        initView();
        return view;
   }

    private void initView(){
        progressBar = view.findViewById(R.id.dashboard_order_progress);
        recyclerView = view.findViewById(R.id.dashboard_orders_recyclerview);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = preferences.getString("userEmail","");

        EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(getContext(),userId);
        ecommerceDashboardModel.displayOrders();
        ecommerceDashboardModel.setOrderReadyListener(new EcommerceDashboardModel.OrderReadyListener() {
            @Override
            public void onOrderReady(ArrayList<Orders> ordersArrayList) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new DashboardOrdersAdapter(ordersArrayList,getContext());
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adapter.setViewOrderClickedListener(new DashboardOrdersAdapter.ViewOrderClickedListener() {
                    @Override
                    public void onViewClicked(int position) {
                        Intent intent = new Intent(getContext(), DashboardViewOrder.class);
                        intent.putExtra("orderList",ordersArrayList.get(position));
                        intent.putExtra("position",position);
                        startActivityForResult(intent,0);
                    }
                });
            }

            @Override
            public void onError(String message) {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == 1) {

            int orderId = data.getIntExtra("orderId",0);
            adapter.removeItem(orderId);

        }
    }

}
