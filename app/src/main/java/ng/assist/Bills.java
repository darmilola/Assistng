package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.BillsAdapter;
import ng.assist.UIs.ViewModel.BillsModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class Bills extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<BillsModel> billList = new ArrayList<>();
    BillsAdapter adapter;
    ProgressBar progressBar;
    LinearLayout refundImage;
    LinearLayout navBack;
    LinearLayout errorLayout;
    MaterialButton retry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);
        initView();
    }

    private void initView(){
        removePending();
        retry = findViewById(R.id.error_occurred_retry);
        errorLayout = findViewById(R.id.error_occurred_layout_root);
        navBack = findViewById(R.id.bills_back_nav);
        refundImage = findViewById(R.id.bills_refund_image);
        progressBar = findViewById(R.id.bills_loading_bar);
        recyclerView = findViewById(R.id.bills_recyclerview);

        showBills();

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBills();
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
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void showBills(){
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        BillsModel billsModel = new BillsModel(Bills.this);
        billsModel.ShowBill();
        billsModel.setShowBillListener(new BillsModel.ShowBillListener() {
            @Override
            public void onSuccess(ArrayList<BillsModel> billsModelArrayList) {
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
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
                errorLayout.setVisibility(View.GONE);
                Toast.makeText(Bills.this, "Transaction is empty", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
            }
        });
    }

    private void removePending(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Bills.this);
        preferences.edit().remove("isPending").apply();
    }

}
