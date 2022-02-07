package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import ng.assist.Adapters.RefundHistoryAdapter;
import ng.assist.Adapters.WithdrawlHistoryAdapter;
import ng.assist.UIs.ViewModel.RefundModel;

public class RefundHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> refundList = new ArrayList<>();
    RefundHistoryAdapter adapter;
    RefundModel refundModel;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_history);
        initView();
    }

    private void initView(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = preferences.getString("userEmail","");
        recyclerView = findViewById(R.id.refund_history_recyclerview);
        progressBar = findViewById(R.id.progressBar);
        refundModel = new RefundModel(userId,this);
        refundModel.GetUserRefunds();
        refundModel.setRefundsReadyListener(new RefundModel.RefundsReadyListener() {
            @Override
            public void onReady(ArrayList<RefundModel> refundModels) {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adapter = new RefundHistoryAdapter(refundModels,RefundHistory.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(RefundHistory.this,RecyclerView.VERTICAL,false);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onError(String message) {
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RefundHistory.this, message, Toast.LENGTH_LONG).show();

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