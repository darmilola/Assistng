package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import ng.assist.BillPayment;
import ng.assist.R;
import ng.assist.UIs.ViewModel.BillsModel;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.itemViewHolder> {

    ArrayList<BillsModel> billsList;
    Context context;


    public BillsAdapter(ArrayList<BillsModel> billsList, Context context){
        this.billsList = billsList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.bills_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
            BillsModel billsModel = billsList.get(position);
            if(billsModel.getType().equalsIgnoreCase("1") )holder.type.setText("Transports");
           if(billsModel.getType().equalsIgnoreCase("3") )holder.type.setText("House Inspection");
           if(billsModel.getType().equalsIgnoreCase("2") )holder.type.setText("Marketplace");
          if(billsModel.getType().equalsIgnoreCase("4") )holder.type.setText("Service");
          if(billsModel.getType().equalsIgnoreCase("1")){
              holder.name.setText(billsModel.getRoute());
          }
          else{
              holder.name.setText(billsModel.getName());
          }
          holder.cost.setText(Integer.toString(billsModel.getCost()));



    }


    @Override
    public int getItemCount() {
        return billsList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder{

        TextView type;
        TextView name;
        TextView cost;
        MaterialButton releaseFund,requestRefund;

        public itemViewHolder(View ItemView){
            super(ItemView);
            type = ItemView.findViewById(R.id.bills_item_type);
            name = ItemView.findViewById(R.id.bills_item_name);
            cost = ItemView.findViewById(R.id.bills_item_cost);
            releaseFund = ItemView.findViewById(R.id.bills_item_release_fund);
            requestRefund = ItemView.findViewById(R.id.bills_item_request_refund);

            requestRefund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String reason = "";
                    String payerId = billsList.get(getAdapterPosition()).getPayerId();
                    String payeeId = billsList.get(getAdapterPosition()).getPayeeId();
                    int cost = billsList.get(getAdapterPosition()).getCost();
                    String type = billsList.get(getAdapterPosition()).getType();
                    int billId = billsList.get(getAdapterPosition()).getBillId();

                    if(type.equalsIgnoreCase("1"))reason = "Transport";
                    if(type.equalsIgnoreCase("3"))reason = "Accomodation";
                    if(type.equalsIgnoreCase("2"))reason = "Marketplace";
                    if(type.equalsIgnoreCase("4"))reason = "Services";


                    BillsModel billsModel = new BillsModel(payeeId,payerId,cost,reason,billId,context);

                    billsModel.RequestRefund();

                    billsModel.setBillsActionLitener(new BillsModel.BillsActionLitener() {
                        @Override
                        public void onSuccess() {
                            billsList.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            Toast.makeText(context, "You have successfully request refund for this bill", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, "Error occurred please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            releaseFund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String payerId = billsList.get(getAdapterPosition()).getPayerId();
                    String payeeId = billsList.get(getAdapterPosition()).getPayeeId();
                    int cost = billsList.get(getAdapterPosition()).getCost();
                    String type = billsList.get(getAdapterPosition()).getType();
                    int billId = billsList.get(getAdapterPosition()).getBillId();
                    int bookingId = billsList.get(getAdapterPosition()).getBookingId();

                    BillsModel billsModel = null;

                    if(type.equalsIgnoreCase("1")) {
                       billsModel =  new BillsModel(billId,bookingId,context);
                        billsModel.payTransportBill();
                    }
                    else{
                        billsModel =  new BillsModel(billId,payeeId,cost,context);
                        billsModel.payOtherBill();
                    }

                    billsModel.setBillsActionLitener(new BillsModel.BillsActionLitener() {
                        @Override
                        public void onSuccess() {
                            Date date = new Date();
                            Timestamp timestamp = new Timestamp(date.getTime());
                            insertBooking(0,7,"Bills",timestamp.toString(),Integer.toString(cost),"");
                            billsList.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            Toast.makeText(context, "Bill paid successfully", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, "Error occurred please try again", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

        }
    }

    private void insertBooking(int id,int type, String title, String timestamp, String amount, String orderId){
        TransactionDatabase db = Room.databaseBuilder(context,
                TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
        Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
        TransactionDao transactionDao = db.transactionDao();
        transactionDao.insert(transactions);
    }
}
