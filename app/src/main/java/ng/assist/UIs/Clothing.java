package ng.assist.UIs;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.R;
import ng.assist.UIs.ViewModel.GroceryModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class Clothing extends Fragment {


    RecyclerView recyclerView;
    View view;
    ProgressBar progressBar;
    ProgressBar recyclerProgress;
    GroceryDisplayAdapter groceryDisplayAdapter;
    ArrayList<GroceryModel> groceryList = new ArrayList<>();
    private String nextPageUrl;
    TextView noProductAvailable;

    public Clothing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fashion, container, false);
        initView();
        return  view;
    }

    private void initView(){
        recyclerProgress = view.findViewById(R.id.recycler_progress);
        recyclerView = view.findViewById(R.id.recyclerview);
        progressBar = view.findViewById(R.id.progressbar);
        noProductAvailable = view.findViewById(R.id.grocery_no_product_available);
        noProductAvailable.setVisibility(View.GONE);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        String city = getLocation();
        GroceryModel groceryModel = new GroceryModel("Clothing",city);
        groceryModel.getGroceryProducts();
        groceryModel.setProductReadyListener(new GroceryModel.ProductReadyListener() {
            @Override
            public void onProductReady(ArrayList<GroceryModel> groceryModels, String mNextPageUrl) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                groceryDisplayAdapter = new GroceryDisplayAdapter(groceryModels,getContext());
                recyclerView.setAdapter(groceryDisplayAdapter);
                nextPageUrl = mNextPageUrl;
            }
            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noProductAvailable.setVisibility(View.VISIBLE);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){ //1 for down
                    if(nextPageUrl.equalsIgnoreCase("null")){
                        return;
                    }

                    recyclerProgress.setVisibility(View.VISIBLE);
                    GroceryModel groceryModel = new GroceryModel("Clothing",city);
                    groceryModel.getGroceryProductsNextPage(Clothing.this.nextPageUrl);
                    groceryModel.setProductReadyListener(new GroceryModel.ProductReadyListener() {
                        @Override
                        public void onProductReady(ArrayList<GroceryModel> groceryModels, String mNextPageUrl) {
                            recyclerProgress.setVisibility(View.GONE);
                            groceryDisplayAdapter.addItem(groceryModels);
                            nextPageUrl = mNextPageUrl;
                        }
                        @Override
                        public void onError(String message) {
                            recyclerProgress.setVisibility(View.GONE);
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    public void refreshFragment(String city){

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        noProductAvailable.setVisibility(View.GONE);

        GroceryModel groceryModel = new GroceryModel("Clothing",city);
        groceryModel.getGroceryProducts();
        groceryModel.setProductReadyListener(new GroceryModel.ProductReadyListener() {
            @Override
            public void onProductReady(ArrayList<GroceryModel> groceryModels, String mNextPageUrl) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                groceryDisplayAdapter = new GroceryDisplayAdapter(groceryModels,getContext());
                recyclerView.setAdapter(groceryDisplayAdapter);
                nextPageUrl = mNextPageUrl;
            }
            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noProductAvailable.setVisibility(View.VISIBLE);
            }
        });
    }

    private String getLocation(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String city = preferences.getString("userCity","lagos");
        return city;
    }
}
