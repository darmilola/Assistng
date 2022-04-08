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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ng.assist.Adapters.LoanHistoryAdapter;
import ng.assist.UIs.ViewModel.LoanHistoryModel;

public class LoanHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    LoanHistoryAdapter adapter;
    ImageView back;
    TextView noLoan;
    ProgressBar progressBar;
    LoanHistoryModel loanHistoryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_history);
        initView();
    }

    private void initView(){
        recyclerView = findViewById(R.id.loan_history_recyclerview);
        back = findViewById(R.id.back_nav);
        noLoan = findViewById(R.id.no_loan_text);
        progressBar = findViewById(R.id.loan_history_progress);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoanHistory.this);
        String userId = preferences.getString("userEmail","");
        loanHistoryModel = new LoanHistoryModel(userId,LoanHistory.this);
        loanHistoryModel.GetLoanHistory();
        loanHistoryModel.setLoanHistoryReadyListener(new LoanHistoryModel.LoanHistoryReadyListener() {
            @Override
            public void onReady(ArrayList<LoanHistoryModel> loanHistoryModels) {
                adapter = new LoanHistoryAdapter(loanHistoryModels,LoanHistory.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LoanHistory.this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(linearLayoutManager);
                progressBar.setVisibility(View.GONE);
                noLoan.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                noLoan.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}