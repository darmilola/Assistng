package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ng.assist.R;

public class RefundHistoryAdapter extends RecyclerView.Adapter<RefundHistoryAdapter.itemViewHolder> {

    ArrayList<String> refundList;
    Context context;


    public RefundHistoryAdapter(ArrayList<String> refundList, Context context){
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

    }


    @Override
    public int getItemCount() {
        return refundList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {


        public itemViewHolder(View ItemView) {
            super(ItemView);

        }

    }

}
