package ng.assist.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import ng.assist.AccomodationBooking;
import ng.assist.BillPayment;
import ng.assist.GroceryCart;
import ng.assist.R;
import ng.assist.UIs.Utils.RefundDialog;
import ng.assist.UIs.ViewModel.BillsModel;
import ng.assist.UIs.ViewModel.CreatBill;
import ng.assist.UIs.ViewModel.ProviderBookingsModel;
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
           if(billsModel.getType().equalsIgnoreCase("2") )holder.type.setText("Shopping");
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
        AlertDialog.Builder builder;

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

                    RefundDialog refundDialog = new RefundDialog(context);
                    refundDialog.setDialogActionClickListener(new RefundDialog.OnDialogActionClickListener() {
                        @Override
                        public void onActionClicked(String text) {
                            refundDialog.CloseRefundDialog();
                            String reason = text;
                            String payerId = billsList.get(getAdapterPosition()).getPayerId();
                            String payeeId = billsList.get(getAdapterPosition()).getPayeeId();
                            int cost = billsList.get(getAdapterPosition()).getCost();
                            String billId = billsList.get(getAdapterPosition()).getBillId();

                            BillsModel billsModel = new BillsModel(payeeId,payerId,cost,reason,billId,context);
                            billsModel.RequestRefund();

                            billsModel.setBillsActionLitener(new BillsModel.BillsActionLitener() {
                                @Override
                                public void onSuccess() {

                                    billsList.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "You have requested refund for this bill", Toast.LENGTH_SHORT).show();
                                    UpdateServiceBooking(billId,"Refund");
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(context, "Error occurred please try again", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                    refundDialog.ShowRefundDialog();

                }
            });


            releaseFund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showBillsDialog();
                }
            });

        }

        private void showBillsDialog() {
            builder = new AlertDialog.Builder(context);
            builder.setMessage("You are about to complete this transaction")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            String payerId = billsList.get(getAdapterPosition()).getPayerId();
                            String payeeId = billsList.get(getAdapterPosition()).getPayeeId();
                            int cost = billsList.get(getAdapterPosition()).getCost();
                            String type = billsList.get(getAdapterPosition()).getType();
                            String billId = billsList.get(getAdapterPosition()).getBillId();
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
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                                    String currentWalletBalance = preferences.getString("walletBalance","0");
                                    int newBalance = Integer.parseInt(currentWalletBalance) + cost;
                                    preferences.edit().putString("walletBalance",Integer.toString(newBalance));
                                    if(billsList.get(getAdapterPosition()).getType().equalsIgnoreCase("1") ){
                                        insertBooking(0,7,"Transport",timestamp.toString(),Integer.toString(cost),"");
                                    }
                                    else if(billsList.get(getAdapterPosition()).getType().equalsIgnoreCase("3") ){
                                        insertBooking(0,7,"House Inspection",timestamp.toString(),Integer.toString(cost),"");
                                    }
                                    else if(billsList.get(getAdapterPosition()).getType().equalsIgnoreCase("2") ){
                                        insertBooking(0,7,"Shopping",timestamp.toString(),Integer.toString(cost),"");
                                    }else if(billsList.get(getAdapterPosition()).getType().equalsIgnoreCase("4") ){
                                        insertBooking(0,7,"Service",timestamp.toString(),Integer.toString(cost),"");
                                    }
                                    billsList.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Bill paid successfully", Toast.LENGTH_LONG).show();
                                    UpdateServiceBooking(billId,"Completed");
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(context, "Error occurred please try again", Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Complete Transaction");
            alert.show();
        }
    }



    private void insertBooking(int id,int type, String title, String timestamp, String amount, String orderId){
        TransactionDatabase db = Room.databaseBuilder(context,
                TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
        Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
        TransactionDao transactionDao = db.transactionDao();
        transactionDao.insert(transactions);
    }

    private void UpdateServiceBooking(String billId,String status){
        ProviderBookingsModel providerBookingsModel = new ProviderBookingsModel(billId,status);
        providerBookingsModel.updateBookings();
        providerBookingsModel.setUpdateListener(new ProviderBookingsModel.UpdateListener() {
            @Override
            public void onUpdateSuccess() {

            }

            @Override
            public void onError(String message) {

            }
        });
    }
}
