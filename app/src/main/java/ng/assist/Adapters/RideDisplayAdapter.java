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

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.Utils.TransportDialog;
import ng.assist.UIs.ViewModel.CabHailingModel;

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
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cab_display_item, parent, false);
        return new RideDisplayAdapter.itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull RideDisplayAdapter.itemViewHolder holder, int position) {
        CabHailingModel model = rideDisplayList.get(position);
        if(model.getType().equalsIgnoreCase("car")){
             holder.carImage.setImageResource(R.drawable.car_icon);
        }
        else if(model.getType().equalsIgnoreCase("bus")){
            holder.carImage.setImageResource(R.drawable.bus_transportation_vehicle_icon);
        }
        holder.passengerCount.setText(model.getSeats()+" Seats Available");
        holder.carType.setText(model.getType());
    }


    @Override
    public int getItemCount() {
        return rideDisplayList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder{
        LinearLayout callLayout, book;
        TextView carType,passengerCount;
        ImageView carImage;

        public itemViewHolder(View ItemView){
            super(ItemView);
            callLayout = ItemView.findViewById(R.id.cab_item_phone);
            book = ItemView.findViewById(R.id.cab_item_chat);
            carType = ItemView.findViewById(R.id.cab_item_type);
            passengerCount = ItemView.findViewById(R.id.cab_item_passenger);
            carImage = ItemView.findViewById(R.id.cab_item_image);

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
    }
}
