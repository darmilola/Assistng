package ng.assist.UIs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.DasdhboardProductAdapter;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.DashboardAddProduct;
import ng.assist.EcommerceDashboard;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.GroceryModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcommerceProduct extends Fragment {


    RecyclerView recyclerView;
    DasdhboardProductAdapter adapter;
    ArrayList<String> productList = new ArrayList<>();
    View view;
    String mPhone,mShopname,userId;
    MaterialButton addProduct;
    ProgressBar progressBar;
    LinearLayout rootLayout;
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
        progressBar = view.findViewById(R.id.ecommerce_product_dashboard_progress);
        rootLayout = view.findViewById(R.id.ecc_product_dashboard_root);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String mPhone =  preferences.getString("phone","");
        String mShopname = preferences.getString("shopName","");
        recyclerView = view.findViewById(R.id.dashboard_products_recyclerview);
        addProduct = view.findViewById(R.id.dashboard_add_product);

        userId = preferences.getString("userEmail","");

        EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(getContext(),userId);
        ecommerceDashboardModel.getRetailerProduct();
        ecommerceDashboardModel.setProductsReadyListener(new EcommerceDashboardModel.ProductsReadyListener() {
            @Override
            public void onReady(ArrayList<GroceryModel> groceryModelArrayList) {
                   rootLayout.setVisibility(View.VISIBLE);
                   progressBar.setVisibility(View.GONE);
                   addProduct.setVisibility(View.VISIBLE);
                   recyclerView.setVisibility(View.VISIBLE);

                  adapter = new DasdhboardProductAdapter(groceryModelArrayList,getContext());
                  GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
                  recyclerView.setLayoutManager(layoutManager);
                  recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                addProduct.setVisibility(View.VISIBLE);
                rootLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhone.equalsIgnoreCase("null") || mShopname.equalsIgnoreCase("null")) {
                    Toast.makeText(getContext(), "Update your profile to continue", Toast.LENGTH_SHORT).show();
                } else {
                    startActivityForResult(new Intent(getContext(), DashboardAddProduct.class),1);
                }
            }
        });
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 1) {

            rootLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(getContext(),userId);
            ecommerceDashboardModel.getRetailerProduct();
            ecommerceDashboardModel.setProductsReadyListener(new EcommerceDashboardModel.ProductsReadyListener() {
                @Override
                public void onReady(ArrayList<GroceryModel> groceryModelArrayList) {
                    rootLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    adapter = new DasdhboardProductAdapter(groceryModelArrayList,getContext());
                    GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
