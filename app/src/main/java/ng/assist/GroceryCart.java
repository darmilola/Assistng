package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.GroceryCartAdapter;
import ng.assist.UIs.ViewModel.GroceryModel;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class GroceryCart extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<GroceryModel> storeProductList = new ArrayList<>();
    GroceryCartAdapter adapter;
    NestedScrollView rootLayout;
    ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_cart);
        initView();
    }

    private void initView(){
        String retailerId = getIntent().getStringExtra("retailerId");
        String userId = PreferenceManager.getDefaultSharedPreferences(GroceryCart.this).getString("userEmail","null");
        recyclerView = findViewById(R.id.grocery_cart_recyclerview);
        rootLayout = findViewById(R.id.cart_root_layout);
        loadingProgress = findViewById(R.id.cart_loading_progress);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);


        GroceryModel groceryModel = new GroceryModel(retailerId,userId,GroceryCart.this);
        groceryModel.viewCart();
        groceryModel.setCartDisplayListener(new GroceryModel.CartDisplayListener() {
            @Override
            public void onCartReady(ArrayList<GroceryModel> groceryModels) {
                if(!(groceryModels.size() < 1)) {
                    rootLayout.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.GONE);
                    adapter = new GroceryCartAdapter(groceryModels,GroceryCart.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    rootLayout.setVisibility(View.GONE);
                    loadingProgress.setVisibility(View.GONE);
                    Toast.makeText(GroceryCart.this, "Nothing in Cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCartError(String message) {
                rootLayout.setVisibility(View.GONE);
                loadingProgress.setVisibility(View.GONE);
                Toast.makeText(GroceryCart.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
             getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }
}
