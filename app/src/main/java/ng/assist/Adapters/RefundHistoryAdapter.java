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
import ng.assist.UIs.ViewModel.RefundModel;

public class RefundHistoryAdapter extends RecyclerView.Adapter<RefundHistoryAdapter.itemViewHolder> {

    ArrayList<RefundModel> refundList;
    Context context;


    public RefundHistoryAdapter(ArrayList<RefundModel> refundList, Context context){
        this.refundList = refundList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.refund_history_item, parent, false);
        return new itemViewHolder(view2);

    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        RefundModel refundModel = refundList.get(position);
        String status = refundModel.getStatus();
        if(status.equalsIgnoreCase("pending")){
            holder.status.setText("Pending");
        }
        if(status.equalsIgnoreCase("rejected")){
            holder.status.setText("Rejected");
        }
        if(status.equalsIgnoreCase("approved")){
            holder.status.setText("Approved");
        }
        holder.amount.setText("NGN "+refundModel.getAmount());

    }


    @Override
    public int getItemCount() {
        return refundList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {

        TextView amount, status;

        public itemViewHolder(View ItemView) {
            super(ItemView);
            amount = ItemView.findViewById(R.id.refund_history_amount);
            status = ItemView.findViewById(R.id.refund_history_status);

        }

    }

}
