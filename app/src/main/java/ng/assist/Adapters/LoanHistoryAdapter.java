package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ng.assist.R;
import ng.assist.UIs.ViewModel.LoanHistoryModel;
import ng.assist.UIs.ViewModel.RefundModel;

public class LoanHistoryAdapter extends RecyclerView.Adapter<LoanHistoryAdapter.itemViewHolder> {

    ArrayList<LoanHistoryModel> loanHistoryModelArrayList;
    Context context;


    public LoanHistoryAdapter(ArrayList<LoanHistoryModel> loanHistoryModelArrayList, Context context){
        this.loanHistoryModelArrayList = loanHistoryModelArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_history_item, parent, false);
        return new itemViewHolder(view2);

    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        LoanHistoryModel loanHistoryModel = loanHistoryModelArrayList.get(position);
        holder.status.setText(loanHistoryModel.getStatus());
        holder.amount.setText("NGN "+loanHistoryModel.getAmount());
        holder.date.setText(loanHistoryModel.getDate());
    }


    @Override
    public int getItemCount() {
        return loanHistoryModelArrayList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {

        TextView amount,status,date;

        public itemViewHolder(View ItemView) {
            super(ItemView);
            amount = ItemView.findViewById(R.id.loan_history_amount);
            status = ItemView.findViewById(R.id.loan_history_status);
            date = ItemView.findViewById(R.id.loan_history_date);
        }

    }

}
