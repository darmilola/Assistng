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

import ng.assist.Adapters.WithdrawlHistoryAdapter;
import ng.assist.UIs.ViewModel.WithdrawalModel;

public class WithdrawalHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    WithdrawlHistoryAdapter adapter;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_history);
        initView();
    }

    private void initView(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userId = preferences.getString("userEmail","");
        recyclerView = findViewById(R.id.withdraw_history_recyclerview);
        progressBar = findViewById(R.id.progressBar);

        WithdrawalModel withdrawalModel = new WithdrawalModel(userId);
        withdrawalModel.GetUserWithdrawals();
        withdrawalModel.setUserWithdrawalListener(new WithdrawalModel.UserWithdrawalListener() {
            @Override
            public void onSuccess(ArrayList<WithdrawalModel> withdrawalModelArrayList) {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adapter = new WithdrawlHistoryAdapter(withdrawalModelArrayList,WithdrawalHistory.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(WithdrawalHistory.this,RecyclerView.VERTICAL,false);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onError(String message) {
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WithdrawalHistory.this, message, Toast.LENGTH_SHORT).show();
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