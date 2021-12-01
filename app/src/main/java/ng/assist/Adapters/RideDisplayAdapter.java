package ng.assist.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.datatransport.Transport;
import com.google.android.material.button.MaterialButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
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
       holder.from.setText(model.getFrom());
       holder.to.setText(model.getTo());
       holder.book.setText(model.getFare());
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
        TextView from,to,point,seats,company,type;

        public itemViewHolder(View ItemView){
            super(ItemView);
            book = ItemView.findViewById(R.id.bus_item_book);
            call = ItemView.findViewById(R.id.bus_item_call);
            from = ItemView.findViewById(R.id.bus_item_from);
            to = ItemView.findViewById(R.id.bus_item_to);
            point = ItemView.findViewById(R.id.bus_item_meeting_point);
            seats = ItemView.findViewById(R.id.bus_item_seats);
            company = ItemView.findViewById(R.id.bus_item_company);
            type = ItemView.findViewById(R.id.bus_item_type);

           book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int transportId = rideDisplayList.get(getAdapterPosition()).getTransportId();
                    String from = rideDisplayList.get(getAdapterPosition()).getFrom();
                    String to = rideDisplayList.get(getAdapterPosition()).getTo();
                    String tFare = rideDisplayList.get(getAdapterPosition()).getFare();
                    String route = from+" - "+to;

                    TransportDialog transportDialog = new TransportDialog(context,from,to,tFare);
                    transportDialog.ShowTransportDialog();
                    transportDialog.setDialogActionClickListener(new TransportDialog.OnDialogActionClickListener() {
                        @Override
                        public void bookClicked(String phonenumber) {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            String userId =  preferences.getString("userEmail","");
                            CabHailingModel cabHailingModel = new CabHailingModel(transportId,userId,phonenumber,"1",route,Integer.parseInt(tFare),context);
                            cabHailingModel.BookTransport();
                            cabHailingModel.setTransportBookingListener(new CabHailingModel.TransportBookingListener() {
                                @Override
                                public void onSuccess() {

                                    Date date = new Date();
                                    Timestamp timestamp = new Timestamp(date.getTime());
                                    insertTransportBooking(0,1,"Transport",timestamp.toString(),tFare,"");
                                    Toast.makeText(context, "Booking Successful", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            });
        }

        private void insertTransportBooking(int id,int type, String title, String timestamp, String amount, String orderId){
            TransactionDatabase db = Room.databaseBuilder(context,
                    TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
            Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
            TransactionDao transactionDao = db.transactionDao();
            transactionDao.insert(transactions);
        }
    }


}
