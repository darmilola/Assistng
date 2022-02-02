package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import ng.assist.Adapters.RefundHistoryAdapter;
import ng.assist.Adapters.WithdrawlHistoryAdapter;

public class RefundHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> refundList = new ArrayList<>();
    RefundHistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_history);
        initView();
    }

    private void initView(){
        recyclerView = findViewById(R.id.refund_history_recyclerview);
        refundList.add("");
        refundList.add("");
        refundList.add("");
        refundList.add("");
        refundList.add("");

        adapter = new RefundHistoryAdapter(refundList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
}