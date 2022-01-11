package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import ng.assist.Adapters.GroceryCartAdapter;
import ng.assist.UIs.ViewModel.CreatBill;
import ng.assist.UIs.ViewModel.GroceryModel;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GroceryCart extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<GroceryModel> storeProductList = new ArrayList<>();
    GroceryCartAdapter adapter;
    NestedScrollView rootLayout;
    ProgressBar loadingProgress;
    TextView mTotalprice;
    MaterialButton checkout;
    String mTotalPriceValue,retailerShopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_cart);
        initView();
    }

    private void initView(){
        String retailerId = getIntent().getStringExtra("retailerId");
        String retailerShopName = getIntent().getStringExtra("retailerShopName");
        String userId = PreferenceManager.getDefaultSharedPreferences(GroceryCart.this).getString("userEmail","null");
        recyclerView = findViewById(R.id.grocery_cart_recyclerview);
        rootLayout = findViewById(R.id.cart_root_layout);
        loadingProgress = findViewById(R.id.cart_loading_progress);
        mTotalprice = findViewById(R.id.cart_total_price);
        checkout = findViewById(R.id.cart_checkout_button);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        GroceryModel groceryModel = new GroceryModel(retailerId,userId,GroceryCart.this);
        groceryModel.viewCart();
        groceryModel.setCartDisplayListener(new GroceryModel.CartDisplayListener() {
            @Override
            public void onCartReady(ArrayList<GroceryModel> groceryModels) {
                if(!(groceryModels.size() < 1)) {
                    rootLayout.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.GONE);
                    adapter = new GroceryCartAdapter(groceryModels, GroceryCart.this, new GroceryCartAdapter.TotalpriceUpdateListener() {
                        @Override
                        public void onNewPrice(String totalPrice) {
                            mTotalPriceValue = totalPrice;
                            Locale NigerianLocale = new Locale("en","ng");
                            String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(totalPrice));
                            String formattedPrice = unFormattedPrice.replaceAll("\\.00","");
                            mTotalprice.setText(formattedPrice);
                        }
                    });
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

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<GroceryModel> groceryModelArrayList = adapter.getGroceryList();
                if(groceryModelArrayList.size() < 1){
                    Toast.makeText(GroceryCart.this, "Nothing in Cart", Toast.LENGTH_SHORT).show();
                }
                else {
                    String orderJson = convertListToJsonString(groceryModelArrayList);
                    GroceryModel groceryModel1 = new GroceryModel(retailerId, userId,orderJson,mTotalPriceValue,GroceryCart.this,0);
                    groceryModel1.CheckOut();
                    groceryModel1.setCartCheckoutListener(new GroceryModel.CartCheckoutListener() {
                        @Override
                        public void onSuccess() {
                            CreatBill createBill = new CreatBill(userId,retailerId,Integer.parseInt(mTotalPriceValue),"2",retailerShopName);
                            createBill.CreateBill();
                            createBill.setCreateBillListener(new CreatBill.CreateBillListener() {
                                @Override
                                public void onSuccess() {
                                    Date date = new Date();
                                    Timestamp timestamp = new Timestamp(date.getTime());
                                    insertBooking(0,2,"Marketplace",timestamp.toString(),mTotalPriceValue,"");
                                    Toast.makeText(GroceryCart.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onError() {

                                }
                            });

                            ;
                        }
                        @Override
                        public void onError() {

                        }
                    });
                }
                }

        });
    }

    private String convertListToJsonString(ArrayList<GroceryModel> orderlist){
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < orderlist.size(); i++){
            jsonArray.put(orderlist.get(i).getJsonObject());
        }
        return jsonArray.toString();
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

    private void insertBooking(int id,int type, String title, String timestamp, String amount, String orderId){
        TransactionDatabase db = Room.databaseBuilder(GroceryCart.this,
                TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
        Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
        TransactionDao transactionDao = db.transactionDao();
        transactionDao.insert(transactions);
    }
}
