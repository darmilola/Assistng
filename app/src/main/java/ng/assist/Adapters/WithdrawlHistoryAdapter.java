package ng.assist.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.button.MaterialButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import ng.assist.R;
import ng.assist.UIs.ViewModel.WithdrawalModel;


public class WithdrawlHistoryAdapter extends RecyclerView.Adapter<WithdrawlHistoryAdapter.itemViewHolder> {

        ArrayList<WithdrawalModel> withdrwalList;
        Context context;


public WithdrawlHistoryAdapter(ArrayList<WithdrawalModel> withdrwalList, Context context){
        this.withdrwalList = withdrwalList;
        this.context = context;
        }


@NonNull
@Override
public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.withdrawal_history_item, parent, false);
        return new itemViewHolder(view2);

        }

@Override
public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
      WithdrawalModel withdrawalModel = withdrwalList.get(position);
      holder.status.setText(withdrawalModel.getStatus());
      holder.amount.setText(Integer.toString(withdrawalModel.getmAmount()));
}


@Override
public int getItemCount() {
        return withdrwalList.size();
        }

public class itemViewHolder extends RecyclerView.ViewHolder {

    TextView amount,status;

    public itemViewHolder(View ItemView) {
        super(ItemView);
        amount = ItemView.findViewById(R.id.withdrawal_history_amount);
        status = ItemView.findViewById(R.id.withdrawal_history_status);

    }

}

}
