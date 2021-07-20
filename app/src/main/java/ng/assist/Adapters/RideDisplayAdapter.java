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
import androidx.core.content.ContextCompat;
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
        if(model.getCarType().equalsIgnoreCase("car")){
             holder.carImage.setImageResource(R.drawable.car_icon);
        }
        else if(model.getCarType().equalsIgnoreCase("bus")){
            holder.carImage.setImageResource(R.drawable.bus_transportation_vehicle_icon);
        }
        holder.passengerCount.setText(model.getTotalPassenger()+" pass");
        holder.carType.setText(model.getCarType());
    }


    @Override
    public int getItemCount() {
        return rideDisplayList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        LinearLayout callLayout,chatLayout;
        TextView carType,passengerCount;
        ImageView carImage;

        public itemViewHolder(View ItemView){
            super(ItemView);
            callLayout = ItemView.findViewById(R.id.cab_item_phone);
            chatLayout = ItemView.findViewById(R.id.cab_item_chat);
            carType = ItemView.findViewById(R.id.cab_item_type);
            passengerCount = ItemView.findViewById(R.id.cab_item_passenger);
            carImage = ItemView.findViewById(R.id.cab_item_image);
        }
        @Override
        public void onClick(View view) {

        }
    }
}
