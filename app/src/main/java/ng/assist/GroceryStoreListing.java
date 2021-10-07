package ng.assist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.GroceryDetailProductAdapter;
import ng.assist.Adapters.GroceryDisplayAdapter;
import ng.assist.Adapters.GroceryStoreListingAdapter;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.ViewModel.GroceryListingDetailsModel;
import ng.assist.UIs.ViewModel.GroceryModel;
import ng.assist.UIs.ViewModel.RetailerInfoModel;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GroceryStoreListing extends AppCompatActivity {

    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<String> storeProductList = new ArrayList<>();
    GroceryStoreListingAdapter groceryStoreListingAdapter;
    FrameLayout cartLayout;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView productName,description,price,shopname;
    GroceryDetailProductAdapter groceryDisplayAdapter;
    ProgressBar detailsProgressbar;
    LinearLayout detailsRootLayout;
    LinearLayout addToCart;
    GroceryModel groceryModel;
    View cartIndicator;
    LinearLayout productImageviewScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_store_listing);
        initView();
    }

    private void initView(){
        productImageviewScroll = findViewById(R.id.scroll_image_layout);
        cartIndicator = findViewById(R.id.cart_indicator);
        cartIndicator.setVisibility(View.GONE);
        addToCart = findViewById(R.id.details_add_to_cart);
        detailsProgressbar = findViewById(R.id.details_progressbar);
        detailsRootLayout = findViewById(R.id.details_root_layout);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        cartLayout = findViewById(R.id.store_listing_cart);
        recyclerView = findViewById(R.id.details_recyclerview);
        productName = findViewById(R.id.details_product_name);
        shopname = findViewById(R.id.details_shopname);
        description = findViewById(R.id.details_product_description);
        price = findViewById(R.id.details_product_price);
        groceryModel = getIntent().getParcelableExtra("product");

        productName.setText(groceryModel.getProductName());
        description.setText(groceryModel.getDescription());
        price.setText(groceryModel.getPrice());

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(GroceryStoreListing.this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
        imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
        GridLayoutManager layoutManager = new GridLayoutManager(GroceryStoreListing.this,2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        GroceryListingDetailsModel groceryListingDetailsModel = new GroceryListingDetailsModel(groceryModel.getRetailerId(),groceryModel.getItemId());

        groceryListingDetailsModel.getGroceryDetails();
        groceryListingDetailsModel.setDetailsReadyListener(new GroceryListingDetailsModel.DetailsReadyListener() {
            @Override
            public void onDetailsReady(ArrayList<String> images, ArrayList<GroceryModel> groceryModelArrayList, RetailerInfoModel retailerInfoModel) {

                detailsProgressbar.setVisibility(View.GONE);
                detailsRootLayout.setVisibility(View.VISIBLE);
                adapter = new ProductImageScrollAdapter(images,GroceryStoreListing.this);
                imagesRecyclerview.setAdapter(adapter);
                productImageviewScroll.setVisibility(View.VISIBLE);
                pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
                imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
                adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());
                shopname.setText(retailerInfoModel.getShopName());
                groceryDisplayAdapter = new GroceryDetailProductAdapter(groceryModelArrayList,GroceryStoreListing.this);
                recyclerView.setAdapter(groceryDisplayAdapter);

            }
            @Override
            public void onError(String message) {
                detailsProgressbar.setVisibility(View.VISIBLE);
                detailsRootLayout.setVisibility(View.GONE);
                Toast.makeText(GroceryStoreListing.this, message, Toast.LENGTH_SHORT).show();
            }
        });


        cartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroceryStoreListing.this,GroceryCart.class);
                intent.putExtra("retailerId",groceryModel.getRetailerId());
                intent.putExtra("retailerShopName",groceryModel.getShopName());
                startActivity(intent);
            }
        });


        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = PreferenceManager.getDefaultSharedPreferences(GroceryStoreListing.this).getString("userEmail","");
                GroceryModel CartModel = new GroceryModel(groceryModel.getItemId(),groceryModel.getRetailerId(),userEmail,"1",GroceryStoreListing.this);
                CartModel.addToCart();
                CartModel.setCartListener(new GroceryModel.CartListener() {
                    @Override
                    public void onAdded() {
                        cartIndicator.setVisibility(View.VISIBLE);
                        Toast.makeText(GroceryStoreListing.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError() {
                        Toast.makeText(GroceryStoreListing.this, "Error adding to Cart", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public void onBackPressed(){
          showAlert();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deleteCart();
    }

    private void showAlert(){
        new AlertDialog.Builder(GroceryStoreListing.this)
                .setTitle("Exit Store")
                .setMessage("Are you sure you want to exit these store, your current cart will be lost")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCart();
                        GroceryStoreListing.super.onBackPressed();
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

     private void deleteCart(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(GroceryStoreListing.this);
        String userId =  preferences.getString("userEmail","");
        GroceryModel groceryModel = new GroceryModel(userId);
        groceryModel.deleteUsersCart();
    }
}
