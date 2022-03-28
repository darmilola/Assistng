package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.PlacedOrderAdapter;
import ng.assist.UIs.Utils.InputDialog;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.CartModel;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.Orders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DashboardViewOrder extends AppCompatActivity {
    Orders orders;
    RecyclerView recyclerView;
    TextView totalPrice,address,landmark,state,lga;
    LinearLayout chatWithCustomer,callCustomer;
    MaterialButton deleteOrder;
    MaterialCheckBox deliveryStatus;
    ArrayList<CartModel> cartModelArrayList = new ArrayList<CartModel>();
    ImageView backNav;
    ImageView selectDeliveryTime,selectStoreAddress,selectTrackingId;
    TextView deliveryTime, storeAddress, trackingId;
    InputDialog inputDialog;
    TextView callCustomerText,orderState;
    LinearLayout homeDeliveryInfo;
    ListDialog listDialog;
    ArrayList<String> orderStates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_view_order);
        initView();
    }
    private void initView(){


        callCustomerText = findViewById(R.id.call_customer_text);
        orderState = findViewById(R.id.order_state_select);
        homeDeliveryInfo = findViewById(R.id.home_delivery_info_layout);
        backNav = findViewById(R.id.back_nav);
        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectDeliveryTime = findViewById(R.id.dashboard_view_order_delivery_time_type_select);
        selectStoreAddress = findViewById(R.id.dashboard_view_order_store_address_type_select);
        selectTrackingId = findViewById(R.id.dashboard_view_order_tracking_id_type_select);
        deliveryTime = findViewById(R.id.dashboard_view_order_delivery_time_type_text);
        storeAddress = findViewById(R.id.dashboard_view_order_store_address_type_text);
        trackingId = findViewById(R.id.dashboard_view_order_tracking_id_type_text);
        orders = getIntent().getParcelableExtra("orderList");
        recyclerView = findViewById(R.id.dashboard_order_recyclerview);
        totalPrice = findViewById(R.id.dashboard_order_totalprice);
        chatWithCustomer = findViewById(R.id.dashboard_order_chat_with_customer);
        deleteOrder = findViewById(R.id.dashboard_order_delete);
        deliveryStatus = findViewById(R.id.dashboard_order_is_delivered);
        callCustomer = findViewById(R.id.dashboard_order_call_customer);
        address = findViewById(R.id.dashboard_order_address);
        landmark = findViewById(R.id.dashboard_order_landmark);
        state = findViewById(R.id.dashboard_order_state);
        lga = findViewById(R.id.dashboard_order_lga);

        populateStages();

        listDialog = new ListDialog(orderStates,DashboardViewOrder.this);
        listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
            @Override
            public void onItemClicked(String city) {
                orderState.setText(city);

                EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),city,"stage");
                ecommerceDashboardModel.updateOrderInfo();
                ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DashboardViewOrder.this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String message) {
                        Toast.makeText(DashboardViewOrder.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if(orders.getType().equalsIgnoreCase("home")){
            homeDeliveryInfo.setVisibility(View.VISIBLE);
        }
        else{
            homeDeliveryInfo.setVisibility(View.GONE);
        }

        orderState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog.showListDialog();

            }
        });

        if(orders.getStoreAddress().equalsIgnoreCase("null")){
            storeAddress.setText("Not Available");
        }
        else{
            storeAddress.setText(orders.getStoreAddress());
        }

        if(orders.getDeliveryDate().equalsIgnoreCase("null")){
            storeAddress.setText("Not Available");
        }
        else{
            deliveryTime.setText(orders.getDeliveryDate());
        }

        if(orders.getTrackingId().equalsIgnoreCase("null")){
            trackingId.setText("Not Available");
        }
        else{
            trackingId.setText(orders.getTrackingId());
        }

        if(orders.getStage().equalsIgnoreCase("null")){
            orderState.setText("Not Available");
        }
        else{
            orderState.setText(orders.getStage());
        }

        Intent intent  = null;

        if(orders.getType().equalsIgnoreCase("store")){
            intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", orders.getPickupPhone(), null));
            callCustomerText.setText("Call Pickup");
        }
        else{
            intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", orders.getUserPhone(), null));
        }


        Intent finalIntent = intent;
        callCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(finalIntent);
            }
        });

        selectDeliveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new InputDialog(DashboardViewOrder.this,"Delivery Time");
                inputDialog.showInputDialog();
                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {

                        EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),text,"deliveryDate");
                        ecommerceDashboardModel.updateOrderInfo();
                        ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                deliveryTime.setText(text);
                                Toast.makeText(DashboardViewOrder.this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(String message) {
                                Toast.makeText(DashboardViewOrder.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void cancelClicked() {

                    }
                });
            }
        });

        selectTrackingId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputDialog = new InputDialog(DashboardViewOrder.this,"Tracking ID");
                inputDialog.showInputDialog();
                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {

                        EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),text,"trackingId");
                        ecommerceDashboardModel.updateOrderInfo();
                        ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                trackingId.setText(text);
                                Toast.makeText(DashboardViewOrder.this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(String message) {
                                Toast.makeText(DashboardViewOrder.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void cancelClicked() {

                    }
                });

            }
        });

        selectStoreAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputDialog = new InputDialog(DashboardViewOrder.this,"Store Address");
                inputDialog.showInputDialog();
                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {

                        EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),text,"storeAddress");
                        ecommerceDashboardModel.updateOrderInfo();
                        ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                storeAddress.setText(text);
                                Toast.makeText(DashboardViewOrder.this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(String message) {
                                Toast.makeText(DashboardViewOrder.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void cancelClicked() {

                    }
                });
            }
        });

        address.setText(orders.getUserAddress());
        landmark.setText(orders.getLandmark());
        state.setText(orders.getState());
        lga.setText(orders.getLga());

        deliveryStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EcommerceDashboardModel ecommerceDashboardModel = null;
                if(isChecked){
                    ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),"delivered","OrderStatus");
                }
                else{
                    ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),"pending delivery","OrderStatus");

                }

                ecommerceDashboardModel.updateOrderInfo();
                ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DashboardViewOrder.this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String message) {
                        Toast.makeText(DashboardViewOrder.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        chatWithCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardViewOrder.this,ChatActivity.class);
                intent.putExtra("receiverId", orders.getUserEmail());
                intent.putExtra("receiverFirstname", orders.getUserFirstname());
                intent.putExtra("receiverLastname", orders.getUserLastname());
                intent.putExtra("receiverImageUrl","https://cdn.pixabay.com/photo/2021/12/27/17/47/animal-6897849__340.jpg");
                startActivity(intent);
            }
        });


        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),"","");
                ecommerceDashboardModel.deleteOrderInfo();
                ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                    @Override
                    public void onSuccess() {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("orderId", getIntent().getIntExtra("position",0));
                        setResult(1,resultIntent);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(DashboardViewOrder.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Locale NigerianLocale = new Locale("en","ng");
        String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(orders.getTotalPrice()));
        String formattedPrice = unFormattedPrice.replaceAll("\\.00","");



        totalPrice.setText(formattedPrice);
        if(orders.getStatus().equalsIgnoreCase("pending delivery")){
            deliveryStatus.setChecked(false);
        }
        else{
            deliveryStatus.setChecked(true);
        }
        cartModelArrayList = parseOrderJson(orders.getOrderJson());
        PlacedOrderAdapter adapter = new PlacedOrderAdapter(cartModelArrayList,DashboardViewOrder.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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

    public void populateStages(){
        orderStates.add("Processing");
        orderStates.add("Ready for pickup");
        orderStates.add("Handed to Logistics");
    }



}
