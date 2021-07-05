package ng.assist.UIs;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.DashboardOrdersAdapter;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcommerceOrders extends Fragment {


    RecyclerView recyclerView;
    DashboardOrdersAdapter adapter;
    ArrayList<String> orderList = new ArrayList<>();
    View view;
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
        recyclerView = view.findViewById(R.id.dashboard_orders_recyclerview);
        for(int i = 0; i < 10; i++){
            orderList.add("");
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DashboardOrdersAdapter(orderList,getContext());
        recyclerView.setAdapter(adapter);
    }

}
