package ng.assist.UIs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.DasdhboardProductAdapter;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.DashboardAddProduct;
import ng.assist.EcommerceDashboard;
import ng.assist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcommerceProduct extends Fragment {


    RecyclerView recyclerView;
    DasdhboardProductAdapter adapter;
    ArrayList<String> productList = new ArrayList<>();
    View view;
    String mPhone,mShopname;
    MaterialButton addProduct;
    public EcommerceProduct() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_ecommerce_product, container, false);
        initView();
        return view;
    }

    private void initView(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String mPhone =  preferences.getString("phone","");
        String mShopname = preferences.getString("shopName","");
        recyclerView = view.findViewById(R.id.dashboard_products_recyclerview);
        addProduct = view.findViewById(R.id.dashboard_add_product);

        for(int i = 0; i < 20; i++){
            productList.add("");
        }

        adapter = new DasdhboardProductAdapter(productList,getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhone.equalsIgnoreCase("null") || mShopname.equalsIgnoreCase("null")) {
                    Toast.makeText(getContext(), "Update your profile to continue", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getContext(), DashboardAddProduct.class));
                }
            }
        });
    }

}
