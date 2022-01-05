package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.UIs.ViewModel.GroceryModel;

public class GrocerySearch extends AppCompatActivity {

    TextView searchTitle;
    String query;
    RecyclerView recyclerView;
    GroceryDisplayAdapter groceryDisplayAdapter;
    GroceryModel groceryModel;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_search);
        initView();
    }

    private void initView(){
        searchTitle = findViewById(R.id.grocery_search_title);
        query = getIntent().getStringExtra("query");
        recyclerView = findViewById(R.id.grocery_search_recyclerview);
        progressBar = findViewById(R.id.grocery_search_progress);
        searchTitle.setText(query);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        groceryModel = new GroceryModel("lagos",query,GrocerySearch.this,true);
        groceryModel.searchGroceryProducts();
        groceryModel.setProductReadyListener(new GroceryModel.ProductReadyListener() {
            @Override
            public void onProductReady(ArrayList<GroceryModel> groceryModels, String nextPageUrl) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                groceryDisplayAdapter = new GroceryDisplayAdapter(groceryModels,GrocerySearch.this);
                recyclerView.setAdapter(groceryDisplayAdapter);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(GrocerySearch.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

}