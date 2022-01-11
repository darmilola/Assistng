package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.GroceryDetailProductAdapter;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.ViewModel.GroceryListingDetailsModel;
import ng.assist.UIs.ViewModel.GroceryModel;
import ng.assist.UIs.ViewModel.RetailerInfoModel;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DashboardProductDetails extends AppCompatActivity {

    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView productName,description,price,shopname;
    RecyclerView recyclerView;
    ProgressBar detailsProgressbar;
    ScrollView detailsRootLayout;
    GroceryModel groceryModel;
    LinearLayout productImageviewScroll;
    RetailerInfoModel retailerInfoModel;
    MaterialButton deleteProduct;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_product_details);
        initView();
    }

    private void initView(){
        deleteProduct = findViewById(R.id.ecc_dashboard_products_delete);
        productImageviewScroll = findViewById(R.id.scroll_image_layout);
        detailsProgressbar = findViewById(R.id.details_progressbar);
        detailsRootLayout = findViewById(R.id.ecc_dashboard_rootview);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);

        productName = findViewById(R.id.ecc_dashboard_products_title);
        description = findViewById(R.id.ecc_dashboard_products_description);
        price = findViewById(R.id.ecc_dashboard_products_price);
        detailsProgressbar = findViewById(R.id.ecc_dashboard_progress_bar);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(DashboardProductDetails.this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
        imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);

        groceryModel = getIntent().getParcelableExtra("product");
        position = getIntent().getIntExtra("position",0);
        productName.setText(groceryModel.getProductName());
        description.setText(groceryModel.getDescription());

        Locale NigerianLocale = new Locale("en","ng");
        String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(groceryModel.getPrice()));
        String formattedPrice = unFormattedPrice.replaceAll("\\.00","");
        price.setText(formattedPrice);

        GroceryListingDetailsModel groceryListingDetailsModel = new GroceryListingDetailsModel(groceryModel.getRetailerId(),groceryModel.getItemId());

        groceryListingDetailsModel.getGroceryDetails();
        groceryListingDetailsModel.setDetailsReadyListener(new GroceryListingDetailsModel.DetailsReadyListener() {
            @Override
            public void onDetailsReady(ArrayList<String> images, ArrayList<GroceryModel> groceryModelArrayList, RetailerInfoModel retailerInfoModel) {
                detailsProgressbar.setVisibility(View.GONE);
                detailsRootLayout.setVisibility(View.VISIBLE);
                adapter = new ProductImageScrollAdapter(images,DashboardProductDetails.this);
                imagesRecyclerview.setAdapter(adapter);
                productImageviewScroll.setVisibility(View.VISIBLE);
                pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
                imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
                adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());

            }

            @Override
            public void onError(String message) {
                detailsProgressbar.setVisibility(View.VISIBLE);
                detailsRootLayout.setVisibility(View.GONE);
                Toast.makeText(DashboardProductDetails.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroceryModel mGroceryModel = new GroceryModel(groceryModel.getItemId(),DashboardProductDetails.this);
                mGroceryModel.deleteProduct();
                mGroceryModel.setDeleteProductListener(new GroceryModel.deleteProductListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DashboardProductDetails.this, "Product Deleted Successfully", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("position", getIntent().getIntExtra("position",0));
                        setResult(200,resultIntent);
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(DashboardProductDetails.this, "Error Occurred please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }




    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }
}

