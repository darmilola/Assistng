package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.BillsAdapter;
import ng.assist.UIs.ViewModel.BillsModel;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class Bills extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<BillsModel> billList = new ArrayList<>();
    BillsAdapter adapter;
    ProgressBar progressBar;
    LinearLayout refundImage;
    LinearLayout navBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);
        initView();
    }

    private void initView(){
        navBack = findViewById(R.id.bills_back_nav);
        refundImage = findViewById(R.id.bills_refund_image);
        progressBar = findViewById(R.id.bills_loading_bar);
        recyclerView = findViewById(R.id.bills_recyclerview);
        BillsModel billsModel = new BillsModel(Bills.this);
        billsModel.ShowBill();
        billsModel.setShowBillListener(new BillsModel.ShowBillListener() {
            @Override
            public void onSuccess(ArrayList<BillsModel> billsModelArrayList) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter = new BillsAdapter(billsModelArrayList,Bills.this);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(Bills.this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onEmpty() {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(Bills.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        refundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Bills.this,RefundHistory.class));
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
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}
