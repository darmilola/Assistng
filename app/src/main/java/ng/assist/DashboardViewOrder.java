package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.PlacedOrderAdapter;
import ng.assist.UIs.ViewModel.CartModel;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.Orders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class DashboardViewOrder extends AppCompatActivity {

    Orders orders;
    RecyclerView recyclerView;
    TextView totalPrice;
    LinearLayout chatWithCustomer;
    MaterialButton deleteOrder;
    MaterialCheckBox deliveryStatus;
    ArrayList<CartModel> cartModelArrayList = new ArrayList<CartModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_view_order);
        initView();
    }
    private void initView(){
        orders = getIntent().getParcelableExtra("orderList");
        recyclerView = findViewById(R.id.dashboard_order_recyclerview);
        totalPrice = findViewById(R.id.dashboard_order_totalprice);
        chatWithCustomer = findViewById(R.id.dashboard_order_chat_with_customer);
        deleteOrder = findViewById(R.id.dashboard_order_delete);
        deliveryStatus = findViewById(R.id.dashboard_order_is_delivered);


        deliveryStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EcommerceDashboardModel ecommerceDashboardModel = null;
                if(isChecked){
                    ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),"delivered");
                }
                else{
                    ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),"pending delivery");

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


        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(DashboardViewOrder.this,orders.getOrderId(),"");
                ecommerceDashboardModel.deleteOrderInfo();
                ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                    @Override
                    public void onSuccess() {
                        setResult(1);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(DashboardViewOrder.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        totalPrice.setText(orders.getTotalPrice());
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



}
