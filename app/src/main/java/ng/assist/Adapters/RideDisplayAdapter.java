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

import ng.assist.AccomodationBooking;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.Utils.TransportDialog;
import ng.assist.UIs.ViewModel.CabHailingModel;
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
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_transport_itemview, parent, false);
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
             holder.typeImg.setImageResource(R.drawable.car_g1c8f1b297_640);
        }
        else if(model.getType().equalsIgnoreCase("luxurious bus")){
            holder.typeImg.setImageResource(R.drawable.bus_g96872f4aa_1280);
        }
        else if(model.getType().equalsIgnoreCase("bus")){
            holder.typeImg.setImageResource(R.drawable.bus_g6e36bc487_1280);
        }
        holder.seats.setText(Integer.toString(model.getSeats()));
    }


    @Override
    public int getItemCount() {
        return rideDisplayList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder{
        MaterialButton book,call;
        ImageView typeImg;
        TextView from,to,point,seats,company,departureTime,departureDate,price;

        public itemViewHolder(View ItemView){
            super(ItemView);
            typeImg = ItemView.findViewById(R.id.ride_item_type_img);
            book = ItemView.findViewById(R.id.bus_item_book);
            call = ItemView.findViewById(R.id.bus_item_call);
            from = ItemView.findViewById(R.id.bus_item_from);
            to = ItemView.findViewById(R.id.bus_item_to);
            price = ItemView.findViewById(R.id.bus_item_price);
            point = ItemView.findViewById(R.id.bus_item_meeting_point);
            seats = ItemView.findViewById(R.id.bus_item_seats);
            company = ItemView.findViewById(R.id.bus_item_company);
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
                            String bookingId = generateBillId();
                            CabHailingModel cabHailingModel = new CabHailingModel(transportId, userId, phonenumber, "1", route, Integer.parseInt(tFare), context, fromArea,toArea, time, date, meetingPoint,bookingId);

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
                                        insertTransportBooking("1", tFare, "","Transport");
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


        // function to generate a random string of length n
         String generateBillId()
        {
            // chose a Character random from this String
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789"
                    + "abcdefghijklmnopqrstuvxyz";

            // create StringBuffer size of AlphaNumericString
            StringBuilder sb = new StringBuilder(7);

            for (int i = 0; i < 7; i++) {

                // generate a random number between
                // 0 to AlphaNumericString variable length
                int index
                        = (int)(AlphaNumericString.length()
                        * Math.random());

                // add Character one by one in end of sb
                sb.append(AlphaNumericString
                        .charAt(index));
            }

            return sb.toString();
        }




        private void insertTransportBooking(String type,String amount, String orderId, String title) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String userId = preferences.getString("userEmail","");
            Transactions transactions = new Transactions(userId, type, title, amount, orderId);
            transactions.createTransactions();
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
