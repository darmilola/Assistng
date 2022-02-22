package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.UIs.ViewModel.GroceryModel;

public class GrocerySearch extends AppCompatActivity {

    TextView searchTitle,noProduct;
    String query,city;
    RecyclerView recyclerView;
    GroceryDisplayAdapter groceryDisplayAdapter;
    GroceryModel groceryModel;
    ProgressBar progressBar;
    LinearLayout navBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_search);
        initView();
    }

    private void initView(){
        noProduct = findViewById(R.id.grocery_search_no_product);
        navBack = findViewById(R.id.grocery_search_nav_back);
        searchTitle = findViewById(R.id.grocery_search_title);
        query = getIntent().getStringExtra("query");
        city = getIntent().getStringExtra("city");
        recyclerView = findViewById(R.id.grocery_search_recyclerview);
        progressBar = findViewById(R.id.grocery_search_progress);
        searchTitle.setText(query);
        deleteCart();

        GridLayoutManager layoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        groceryModel = new GroceryModel(city,query,GrocerySearch.this,true);
        groceryModel.searchGroceryProducts();
        groceryModel.setProductReadyListener(new GroceryModel.ProductReadyListener() {
            @Override
            public void onProductReady(ArrayList<GroceryModel> groceryModels, String nextPageUrl) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                noProduct.setVisibility(View.GONE);
                groceryDisplayAdapter = new GroceryDisplayAdapter(groceryModels,GrocerySearch.this);
                recyclerView.setAdapter(groceryDisplayAdapter);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noProduct.setVisibility(View.VISIBLE);
            }
        });

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    private void deleteCart(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GrocerySearch.this);
        String userId =  preferences.getString("userEmail","");
        GroceryModel groceryModel = new GroceryModel(userId);
        groceryModel.deleteUsersCart();
    }

}