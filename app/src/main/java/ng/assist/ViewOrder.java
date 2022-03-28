package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shuhart.stepview.StepView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import ng.assist.Adapters.DashboardOrdersAdapter;
import ng.assist.Adapters.PlacedOrderAdapter;
import ng.assist.Adapters.ViewOrderAdapter;
import ng.assist.UIs.ViewModel.CartModel;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.GroceryModel;
import ng.assist.UIs.ViewModel.Orders;

public class ViewOrder extends AppCompatActivity {

    LinearLayout back;
    RecyclerView recyclerView;
    ViewOrderAdapter viewOrderAdapter;
    ArrayList<CartModel> orderList = new ArrayList<>();
    StepView stepView;
    String orderId;
    RelativeLayout gigTrackingLayout;
    ProgressBar loader;
    NestedScrollView rootLayout;
    ArrayList<String> homeStep = new ArrayList<>();
    ArrayList<String> storeStep = new ArrayList<>();
    TextView storeOrHomeDelivery,storeOrHomeAddress,pickupPersonOrDeliveryTimeTitle,pickupPersonOrDeliveryTimeValue,trackingId,totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        initView();
    }

    private void initView(){
        populateSteps();
        rootLayout = findViewById(R.id.view_order_root_layout);
        loader = findViewById(R.id.view_order_loader);
        gigTrackingLayout = findViewById(R.id.gig_tracking_layout);
        storeOrHomeAddress = findViewById(R.id.store_or_home_address);
        storeOrHomeDelivery = findViewById(R.id.store_pickup_or_home_delivery);
        pickupPersonOrDeliveryTimeTitle = findViewById(R.id.pickup_person_or_delivery_time_title);
        pickupPersonOrDeliveryTimeValue = findViewById(R.id.pickup_person_or_delivery_time_value);
        trackingId = findViewById(R.id.tracking_id);
        totalPrice = findViewById(R.id.total_costs);
        back = findViewById(R.id.view_order_back_nav);
        stepView = findViewById(R.id.step_view);
        orderId = getIntent().getStringExtra("orderId");
        Toast.makeText(ViewOrder.this, orderId, Toast.LENGTH_SHORT).show();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.view_order_recyclerview);
        stepView.setStepsNumber(3);


        EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(orderId);
        ecommerceDashboardModel.displaySingleOrders();
        ecommerceDashboardModel.setOrderReadyListener(new EcommerceDashboardModel.OrderReadyListener() {
            @Override
            public void onOrderReady(ArrayList<Orders> ordersArrayList) {
                loader.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                Orders orders = ordersArrayList.get(0);

                if(orders.getType().equalsIgnoreCase("home")){
                    stepView.setSteps(homeStep);
                }
                else{
                    stepView.setSteps(storeStep);
                }
                totalPrice.setText(orders.getTotalPrice());
                orderList = parseOrderJson(orders.getOrderJson());

                viewOrderAdapter = new ViewOrderAdapter(orderList,ViewOrder.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(ViewOrder.this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(viewOrderAdapter);

                pickupPersonOrDeliveryTimeTitle.setText(orders.getPickupName());
                if(orders.getType().equalsIgnoreCase("home")){
                    displayHomeDelivery(orders);
                }
                else if(orders.getType().equalsIgnoreCase("store")){
                    displayStorePickup(orders);
                }
                stepView.done(true);


                Log.e("onOrderReady: ", orders.getStage());

                if(orders.getStage().equalsIgnoreCase("Processing")){
                    stepView.go(0,false);
                }
                else{
                    stepView.go(1,false);
                }

            }

            @Override
            public void onError(String message) {
                loader.setVisibility(View.GONE);
                rootLayout.setVisibility(View.GONE);
                Toast.makeText(ViewOrder.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void displayHomeDelivery(Orders orders){

        gigTrackingLayout.setVisibility(View.VISIBLE);
        storeOrHomeDelivery.setText("Home Delivery");
        storeOrHomeAddress.setText(orders.getUserAddress());
        pickupPersonOrDeliveryTimeTitle.setText("Delivery Time");

        if(orders.getDeliveryDate().equalsIgnoreCase("null")){
            pickupPersonOrDeliveryTimeValue.setText("Processing...");
        }
        else{
            pickupPersonOrDeliveryTimeValue.setText(orders.getDeliveryDate());
        }

        if(orders.getTrackingId().equalsIgnoreCase("null")){
            trackingId.setText("Processing...");
        }
        else{
            trackingId.setText(orders.getTrackingId());
        }
    }


    public void displayStorePickup(Orders orders){
        storeOrHomeDelivery.setText("Store Pickup");
        gigTrackingLayout.setVisibility(View.GONE);
        if(orders.getStoreAddress().equalsIgnoreCase("null")){
            storeOrHomeAddress.setText("Processing...");
        }
        else{
            storeOrHomeAddress.setText(orders.getStoreAddress());
        }
       // pickupPersonOrDeliveryTimeTitle.setText("Pickup Person");
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    private ArrayList<CartModel> parseOrderJson(String orderJson){
        ArrayList<CartModel> cartModelArrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(orderJson);
            for(int i = 0; i < jsonArray.length(); i++){
                String name = jsonArray.getJSONObject(i).getString("name");
                String price = jsonArray.getJSONObject(i).getString("price");
                String imageUrl = jsonArray.getJSONObject(i).getString("imageUrl");
                String quantity = jsonArray.getJSONObject(i).getString("quantity");
                CartModel cartModel = new CartModel(name,price,imageUrl,quantity);
                cartModelArrayList.add(cartModel);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return cartModelArrayList;
    }

    public void populateSteps(){
        homeStep.add("Ordered");
        homeStep.add("Processing");
        homeStep.add("Handed to Logistics");

        storeStep.add("Ordered");
        storeStep.add("Processing");
        storeStep.add("Ready for Pickup");

    }

}