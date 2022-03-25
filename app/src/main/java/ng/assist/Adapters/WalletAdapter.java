package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import ng.assist.ProductTransaction;
import ng.assist.R;
import ng.assist.UIs.ViewModel.Transactions;
import ng.assist.UIs.ViewModel.WalletTransactionsModel;
import ng.assist.UIs.chatkit.utils.DateFormatter;
import ng.assist.ViewOrder;

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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Timestamp timestamp = null;
            Date currentDate = new Date();
            dateFormat.format(currentDate);


        try {

            Date parsedDate = dateFormat.parse(transactions.getTimestamp());
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            Log.e("TAG ", e.getLocalizedMessage());
        }
        Date chatDate = new Date(timestamp.getTime());
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(chatDate);               // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1);      // adds one hour
        if(holder instanceof AccomodationItemViewHolder){

            if(DateFormatter.isSameDay(currentDate,chatDate)){
                ((AccomodationItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
            }
            else{
                ((AccomodationItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
            }

            ((AccomodationItemViewHolder) holder).amount.setText("₦"+transactions.getAmount());
        }

        if(holder instanceof BillsItemViewHolder){
            ((BillsItemViewHolder) holder).amount.setText("₦"+transactions.getAmount());
            ((BillsItemViewHolder) holder).name.setText(transactions.getTitle());
            if(DateFormatter.isSameDay(currentDate,chatDate)){
                ((BillsItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
            }
            else{
                ((BillsItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
            }
        }
        if(holder instanceof MarketplaceItemViewHolder){
            if(DateFormatter.isSameDay(currentDate,chatDate)){
                ((MarketplaceItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
            }
            else{
                ((MarketplaceItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
            }
        }
        if(holder instanceof SendItemViewHolder){
            ((SendItemViewHolder) holder).amount.setText("₦"+transactions.getAmount());
            if(DateFormatter.isSameDay(currentDate,chatDate)){
                ((SendItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
            }
            else{
                ((SendItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
            }
        }
        if(holder instanceof ServicesItemViewHolder){
            ((ServicesItemViewHolder) holder).amount.setText("₦"+transactions.getAmount());
            if(DateFormatter.isSameDay(currentDate,chatDate)){
                ((ServicesItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
            }
            else{
                ((ServicesItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
            }
        }

        if(holder instanceof TopUpItemViewHolder){
            ((TopUpItemViewHolder) holder).amount.setText("₦"+transactions.getAmount());
            if(DateFormatter.isSameDay(currentDate,chatDate)){
                ((TopUpItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
            }
            else{
                ((TopUpItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
            }
        }

        if(holder instanceof TransportsItemViewHolder){
            ((TransportsItemViewHolder) holder).amount.setText("₦"+transactions.getAmount());
            if(DateFormatter.isSameDay(currentDate,chatDate)){
                ((TransportsItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
            }
            else{
                ((TransportsItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
            }
        }
        if(holder instanceof WithdrawItemViewHolder){
            ((WithdrawItemViewHolder) holder).amount.setText("₦"+transactions.getAmount());
            if(DateFormatter.isSameDay(currentDate,chatDate)){
                ((WithdrawItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
            }
            else{
                ((WithdrawItemViewHolder) holder).date.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
            }
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

        TextView date,amount,name;
        public BillsItemViewHolder(View ItemView){
            super(ItemView);
            date = ItemView.findViewById(R.id.transactions_date);
            amount = ItemView.findViewById(R.id.transactions_amount);
            name = ItemView.findViewById(R.id.bills_transaction_name);
        }

    }

    public class MarketplaceItemViewHolder extends RecyclerView.ViewHolder{

        TextView date;
        MaterialButton viewOrder;
        public MarketplaceItemViewHolder(View ItemView){
            super(ItemView);
            viewOrder = ItemView.findViewById(R.id.wallet_view_order);
            date = ItemView.findViewById(R.id.transactions_date);

            viewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ViewOrder.class);
                    intent.putExtra("orderId",walletTransactionsList.get(getAdapterPosition()).getOrderId());
                    context.startActivity(intent);
                }
            });

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
