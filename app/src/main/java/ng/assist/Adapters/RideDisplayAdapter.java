package ng.assist.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.Utils.TransportDialog;
import ng.assist.UIs.ViewModel.CabHailingModel;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;

public class RideDisplayAdapter extends RecyclerView.Adapter<RideDisplayAdapter.itemViewHolder> {

    ArrayList<CabHailingModel> rideDisplayList;
    Context context;


    public RideDisplayAdapter(ArrayList<CabHailingModel> rideDisplayList, Context context){
        this.rideDisplayList = rideDisplayList;
        this.context = context;
    }


    @NonNull
    @Override
    public RideDisplayAdapter.itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_transport_itemview_layout, parent, false);
        return new RideDisplayAdapter.itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull RideDisplayAdapter.itemViewHolder holder, int position) {
       CabHailingModel model = rideDisplayList.get(position);
       holder.from.setText(model.getFromArea());
       holder.to.setText(model.getToArea());
       holder.price.setText("₦"+model.getFare());
       holder.departureDate.setText(model.getDepartureDate());
       holder.departureTime.setText(model.getDepartureTime());
       holder.point.setText(model.getMeetingpoint());
       holder.company.setText(model.getCompany());
        if(model.getType().equalsIgnoreCase("car")){
             holder.type.setText("Car");
        }
        else if(model.getType().equalsIgnoreCase("bus")){
            holder.type.setText("Bus");
        }
        holder.seats.setText(model.getSeats()+" Seats");
    }


    @Override
    public int getItemCount() {
        return rideDisplayList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder{
        MaterialButton book,call;
        TextView from,to,point,seats,company,type,departureTime,departureDate,price;

        public itemViewHolder(View ItemView){
            super(ItemView);
            book = ItemView.findViewById(R.id.bus_item_book);
            call = ItemView.findViewById(R.id.bus_item_call);
            from = ItemView.findViewById(R.id.bus_item_from);
            to = ItemView.findViewById(R.id.bus_item_to);
            price = ItemView.findViewById(R.id.bus_item_price);
            point = ItemView.findViewById(R.id.bus_item_meeting_point);
            seats = ItemView.findViewById(R.id.bus_item_seats);
            company = ItemView.findViewById(R.id.bus_item_company);
            type = ItemView.findViewById(R.id.bus_item_type);
            departureDate = ItemView.findViewById(R.id.bus_departure_date);
            departureTime = ItemView.findViewById(R.id.bus_departure_time);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String walletBalance = preferences.getString("walletBalance","0");

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", rideDisplayList.get(getAdapterPosition()).getDriverPhone(), null));
                    context.startActivity(intent);
                }
            });


            book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int transportId = rideDisplayList.get(getAdapterPosition()).getTransportId();
                    String from = rideDisplayList.get(getAdapterPosition()).getFrom();
                    String to = rideDisplayList.get(getAdapterPosition()).getTo();
                    String tFare = rideDisplayList.get(getAdapterPosition()).getFare();
                    String fromArea = rideDisplayList.get(getAdapterPosition()).getFromArea()+" "+rideDisplayList.get(getAdapterPosition()).getFrom();
                    String toArea = rideDisplayList.get(getAdapterPosition()).getToArea()+" "+rideDisplayList.get(getAdapterPosition()).getTo();
                    String date = rideDisplayList.get(getAdapterPosition()).getDepartureDate();
                    String time = rideDisplayList.get(getAdapterPosition()).getDepartureTime();
                    String meetingPoint = rideDisplayList.get(getAdapterPosition()).getMeetingpoint();

                    String route = from+" - "+to;

                    TransportDialog transportDialog = new TransportDialog(context,from,to,tFare);
                    transportDialog.ShowTransportDialog();
                    transportDialog.setDialogActionClickListener(new TransportDialog.OnDialogActionClickListener() {
                        @Override
                        public void bookClicked(String phonenumber) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            String userId = preferences.getString("userEmail", "");
                            CabHailingModel cabHailingModel = new CabHailingModel(transportId, userId, phonenumber, "1", route, Integer.parseInt(tFare), context, fromArea,toArea, time, date, meetingPoint);

                            if (Integer.parseInt(walletBalance) < Integer.parseInt(tFare)) {
                                Toast.makeText(context, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                            } else {

                                cabHailingModel.BookTransport();
                                cabHailingModel.setTransportBookingListener(new CabHailingModel.TransportBookingListener() {
                                    @Override
                                    public void onSuccess() {
                                        reduceWalletBalanceInSharedPref(context,tFare);
                                        Date date = new Date();
                                        Timestamp timestamp = new Timestamp(date.getTime());
                                        insertTransportBooking(0, 1, "Paid", timestamp.toString(), tFare, "");
                                        showAlert();

                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });


                }
            });
        }

        private void showAlert(){
            new AlertDialog.Builder(context)
                    .setTitle("Success")
                    .setMessage("Your transport has been booked successfully, you can check your mail for details")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            ((Activity)context).finish();
                        }
                    })
                    .show();
        }


        private void insertTransportBooking(int id,int type, String title, String timestamp, String amount, String orderId){
            TransactionDatabase db = Room.databaseBuilder(context,
                    TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
            Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
            TransactionDao transactionDao = db.transactionDao();
            transactionDao.insert(transactions);
        }
    }

    private void showAlert(){
        new AlertDialog.Builder(context)
                .setTitle("Booking Successful")
                .setMessage("Thank you for booking your transport with Assist, a description has been forwarded to your mail")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                })
                .show();
    }

    private void reduceWalletBalanceInSharedPref(Context context, String amount){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String walletBalance = preferences.getString("walletBalance","0");
        preferences.edit().putString("walletBalance",Integer.toString(Integer.parseInt(walletBalance) - Integer.parseInt(amount))).apply();
    }


}
