package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;
import ng.assist.UIs.ViewModel.LocationModel;

public class DashBoardLocationsAdapter extends RecyclerView.Adapter<DashBoardLocationsAdapter.itemViewHolder> {

    ArrayList<LocationModel> locationModels;
    Context context;


    public DashBoardLocationsAdapter(ArrayList<LocationModel> locationModels, Context context){
        this.locationModels = locationModels;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.ecommerce_delivery_locations, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
           holder.city.setText(locationModels.get(position).getCity());
    }

    public void addLocation(LocationModel locationModel){
        locationModels.add(locationModel);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return locationModels.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {


        private TextView city;
        private ImageView remove;

        public itemViewHolder(View ItemView){
            super(ItemView);
            city = ItemView.findViewById(R.id.delivery_location_city);
            remove = ItemView.findViewById(R.id.delivery_location_remove);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = locationModels.get(getAdapterPosition()).getId();
                    LocationModel locationModel = new LocationModel(id);
                    locationModel.deleteDeliveryLocations();
                    locationModel.setUpdateInfoListener(new LocationModel.UpdateInfoListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "Location Deleted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }
}
