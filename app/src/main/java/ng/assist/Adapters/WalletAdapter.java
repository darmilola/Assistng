package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.ProductTransaction;
import ng.assist.R;
import ng.assist.UIs.ViewModel.Transactions;
import ng.assist.UIs.ViewModel.WalletTransactionsModel;

public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int typeTransports = 1;
    private static int typeMarketPlace = 2;
    private static int typeAccomodation = 3;
    private static int typeServices = 4;
    private static int typeTopUp = 5;
    private static int typeSend = 6;
    private static int typeBills = 7;
    private static int typeWithdraw = 8;
    ArrayList<Transactions> walletTransactionsList;
    Context context;


public WalletAdapter(ArrayList<Transactions> walletTransactionsList, Context context){
        this.walletTransactionsList = walletTransactionsList;
        this.context = context;

}


@NonNull
@Override
public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == typeTopUp) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transactions_topup, parent, false);
        return new TopUpItemViewHolder(view2);
    } else if (viewType == typeSend) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transactions_send_money, parent, false);
        return new SendItemViewHolder(view2);
    }
    else if(viewType == typeBills){
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transactions_bills, parent, false);
        return new BillsItemViewHolder(view2);
    }
    else if(viewType == typeWithdraw){
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transactions_withdraw, parent, false);
        return new WithdrawItemViewHolder(view2);
    }
    else if(viewType == typeServices){
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transactions_services, parent, false);
        return new ServicesItemViewHolder(view2);
    }
    else if(viewType == typeAccomodation){
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transactions_accomodations, parent, false);
        return new AccomodationItemViewHolder(view2);
    }
    else if(viewType == typeTransports){
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transactions_transports, parent, false);
        return new TransportsItemViewHolder(view2);
    }
    else if(viewType == typeMarketPlace){
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_transactions_marketplace, parent, false);
        return new MarketplaceItemViewHolder(view2);
    }
    return null;

}

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Transactions transactions = walletTransactionsList.get(position);
            if(holder instanceof AccomodationItemViewHolder){
                ((AccomodationItemViewHolder) holder).amount.setText(transactions.getAmount());
                ((AccomodationItemViewHolder) holder).date.setText(transactions.getTimestamp());
           }
        if(holder instanceof BillsItemViewHolder){
            ((BillsItemViewHolder) holder).amount.setText(transactions.getAmount());
            ((BillsItemViewHolder) holder).date.setText(transactions.getTimestamp());
        }
        if(holder instanceof MarketplaceItemViewHolder){
            ((MarketplaceItemViewHolder) holder).amount.setText(transactions.getAmount());
            ((MarketplaceItemViewHolder) holder).date.setText(transactions.getTimestamp());
        }
        if(holder instanceof SendItemViewHolder){
            ((SendItemViewHolder) holder).amount.setText(transactions.getAmount());
            ((SendItemViewHolder) holder).date.setText(transactions.getTimestamp());
        }
        if(holder instanceof ServicesItemViewHolder){
            ((ServicesItemViewHolder) holder).amount.setText(transactions.getAmount());
            ((ServicesItemViewHolder) holder).date.setText(transactions.getTimestamp());
        }

        if(holder instanceof TopUpItemViewHolder){
            ((TopUpItemViewHolder) holder).amount.setText(transactions.getAmount());
            ((TopUpItemViewHolder) holder).date.setText(transactions.getTimestamp());
        }

        if(holder instanceof TransportsItemViewHolder){
            ((TransportsItemViewHolder) holder).amount.setText(transactions.getAmount());
            ((TransportsItemViewHolder) holder).date.setText(transactions.getTimestamp());
        }
        if(holder instanceof WithdrawItemViewHolder){
            ((WithdrawItemViewHolder) holder).amount.setText(transactions.getAmount());
            ((WithdrawItemViewHolder) holder).date.setText(transactions.getTimestamp());
        }





    }


@Override
public int getItemCount() {
        return walletTransactionsList.size();
        }


    @Override
    public int getItemViewType(int position) {

        if (walletTransactionsList.get(position).getType() == typeAccomodation) {

            return typeAccomodation;
        } else if (walletTransactionsList.get(position).getType() == typeBills) {

            return typeBills;
        }
        else if (walletTransactionsList.get(position).getType() == typeMarketPlace) {

            return typeMarketPlace;
        }
        else if (walletTransactionsList.get(position).getType() == typeSend) {

            return typeSend;
        }
        else if (walletTransactionsList.get(position).getType() == typeServices) {

            return typeServices;
        }
        else if (walletTransactionsList.get(position).getType() == typeTopUp) {

            return typeTopUp;
        }
        else if (walletTransactionsList.get(position).getType() == typeTransports) {

            return typeTransports;
        }
        else if (walletTransactionsList.get(position).getType() == typeWithdraw) {

            return typeWithdraw;
        }

       return typeBills;


    }


    public class AccomodationItemViewHolder extends RecyclerView.ViewHolder{

       TextView date,amount;
    public AccomodationItemViewHolder(View ItemView){
        super(ItemView);
        date = ItemView.findViewById(R.id.transactions_date);
        amount = ItemView.findViewById(R.id.transactions_amount);
    }

}

    public class BillsItemViewHolder extends RecyclerView.ViewHolder{

        TextView date,amount;
        public BillsItemViewHolder(View ItemView){
            super(ItemView);
            date = ItemView.findViewById(R.id.transactions_date);
            amount = ItemView.findViewById(R.id.transactions_amount);
        }

    }

    public class MarketplaceItemViewHolder extends RecyclerView.ViewHolder{

        TextView date,amount;
        public MarketplaceItemViewHolder(View ItemView){
            super(ItemView);
            date = ItemView.findViewById(R.id.transactions_date);
            amount = ItemView.findViewById(R.id.transactions_amount);
        }

    }

    public class SendItemViewHolder extends RecyclerView.ViewHolder{

        TextView date,amount;
        public SendItemViewHolder(View ItemView)
        {
            super(ItemView);
            date = ItemView.findViewById(R.id.transactions_date);
            amount = ItemView.findViewById(R.id.transactions_amount);
        }

    }
    public class ServicesItemViewHolder extends RecyclerView.ViewHolder{

        TextView date,amount;
        public ServicesItemViewHolder(View ItemView){
            super(ItemView);
            date = ItemView.findViewById(R.id.transactions_date);
            amount = ItemView.findViewById(R.id.transactions_amount);
        }

    }
    public class TopUpItemViewHolder extends RecyclerView.ViewHolder{

        TextView date,amount;
        public TopUpItemViewHolder(View ItemView)
        {
            super(ItemView);
            date = ItemView.findViewById(R.id.transactions_date);
            amount = ItemView.findViewById(R.id.transactions_amount);
        }

    }
    public class TransportsItemViewHolder extends RecyclerView.ViewHolder{
          TextView date,amount;
         public TransportsItemViewHolder(View ItemView){

             super(ItemView);
             date = ItemView.findViewById(R.id.transactions_date);
             amount = ItemView.findViewById(R.id.transactions_amount);
        }

    }
    public class WithdrawItemViewHolder extends RecyclerView.ViewHolder{
     TextView date,amount;
     public WithdrawItemViewHolder(View ItemView){
            super(ItemView);
            date = ItemView.findViewById(R.id.transactions_date);
            amount = ItemView.findViewById(R.id.transactions_amount);
        }

    }

}
