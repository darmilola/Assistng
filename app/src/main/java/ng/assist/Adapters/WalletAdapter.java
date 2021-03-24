package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.itemViewHolder> {

        ArrayList<String> walletTransactionsList;
        Context context;


public WalletAdapter(ArrayList<String> walletTransactionsList, Context context){
        this.walletTransactionsList = walletTransactionsList;
        this.context = context;
        }


@NonNull
@Override
public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_history_recyclerview_item, parent, false);
        return new itemViewHolder(view2);
        }

@Override
public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        }


@Override
public int getItemCount() {
        return walletTransactionsList.size();
        }

public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


    public itemViewHolder(View ItemView){
        super(ItemView);

    }
    @Override
    public void onClick(View view) {

    }
}

}
