package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.ProductTransaction;
import ng.assist.R;
import ng.assist.UIs.ViewModel.WalletTransactionsModel;

public class WalletAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int typeTopUp = 0;
    private static int typeSend = 1;
    private static int typeBills = 2;
    private static int typeWithdraw = 3;
    private static int typeServices = 4;
    private static int typeAccomodation = 5;
    private static int typeMarketPlace = 6;
    private static int typeTransports = 7;
    ArrayList<WalletTransactionsModel> walletTransactionsList;
    Context context;


public WalletAdapter(ArrayList<WalletTransactionsModel> walletTransactionsList, Context context){
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

    }


@Override
public int getItemCount() {
        return walletTransactionsList.size();
        }


    @Override
    public int getItemViewType(int position) {

        if (walletTransactionsList.get(position).getViewType() == typeAccomodation) {

            return typeAccomodation;
        } else if (walletTransactionsList.get(position).getViewType() == typeBills) {

            return typeBills;
        }
        else if (walletTransactionsList.get(position).getViewType() == typeMarketPlace) {

            return typeMarketPlace;
        }
        else if (walletTransactionsList.get(position).getViewType() == typeSend) {

            return typeSend;
        }
        else if (walletTransactionsList.get(position).getViewType() == typeServices) {

            return typeServices;
        }
        else if (walletTransactionsList.get(position).getViewType() == typeTopUp) {

            return typeTopUp;
        }
        else if (walletTransactionsList.get(position).getViewType() == typeTransports) {

            return typeTransports;
        }
        else if (walletTransactionsList.get(position).getViewType() == typeWithdraw) {

            return typeWithdraw;
        }

       return typeBills;


    }


    public class AccomodationItemViewHolder extends RecyclerView.ViewHolder{


    public AccomodationItemViewHolder(View ItemView){
        super(ItemView);
    }

}

    public class BillsItemViewHolder extends RecyclerView.ViewHolder{


        public BillsItemViewHolder(View ItemView){
            super(ItemView);
        }

    }

    public class MarketplaceItemViewHolder extends RecyclerView.ViewHolder{


        public MarketplaceItemViewHolder(View ItemView){
            super(ItemView);
        }

    }

    public class SendItemViewHolder extends RecyclerView.ViewHolder{


        public SendItemViewHolder(View ItemView){
            super(ItemView);
        }

    }
    public class ServicesItemViewHolder extends RecyclerView.ViewHolder{


        public ServicesItemViewHolder(View ItemView){
            super(ItemView);
        }

    }
    public class TopUpItemViewHolder extends RecyclerView.ViewHolder{


        public TopUpItemViewHolder(View ItemView){
            super(ItemView);
        }

    }
    public class TransportsItemViewHolder extends RecyclerView.ViewHolder{

         public TransportsItemViewHolder(View ItemView){
            super(ItemView);
        }

    }
    public class WithdrawItemViewHolder extends RecyclerView.ViewHolder{

     public WithdrawItemViewHolder(View ItemView){
            super(ItemView);
        }

    }

}
