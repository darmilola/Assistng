package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;

import ng.assist.Adapters.PlacedOrderAdapter;
import ng.assist.Adapters.ViewOrderAdapter;

public class ViewOrder extends AppCompatActivity {

    LinearLayout back;
    RecyclerView recyclerView;
    ViewOrderAdapter viewOrderAdapter;
    ArrayList<String> orderList = new ArrayList<>();
    StepView stepView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        initView();
    }

    private void initView(){
        back = findViewById(R.id.view_order_back_nav);
        stepView = findViewById(R.id.step_view);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.view_order_recyclerview);

        for(int i = 0; i < 5; i++){
            orderList.add("");
        }

        viewOrderAdapter = new ViewOrderAdapter(orderList,ViewOrder.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(viewOrderAdapter);

        stepView.go(2,true);

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

}