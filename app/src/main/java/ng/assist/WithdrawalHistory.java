package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import ng.assist.Adapters.WithdrawlHistoryAdapter;

public class WithdrawalHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> withdrawlList = new ArrayList<>();
    WithdrawlHistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_history);
        initView();
    }

    private void initView(){
        recyclerView = findViewById(R.id.withdraw_history_recyclerview);
        withdrawlList.add("");
        withdrawlList.add("");
        withdrawlList.add("");
        withdrawlList.add("");
        withdrawlList.add("");
        adapter = new WithdrawlHistoryAdapter(withdrawlList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}