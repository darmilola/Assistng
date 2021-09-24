package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;
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

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
        }
        @Override
        public void onClick(View view) {

        }
    }
}
