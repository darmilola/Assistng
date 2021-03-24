package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;

public class RideDisplayAdapter extends RecyclerView.Adapter<RideDisplayAdapter.itemViewHolder> {

    ArrayList<String> rideDisplayList;
    Context context;


    public RideDisplayAdapter(ArrayList<String> rideDisplayList, Context context){
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
    }


    @Override
    public int getItemCount() {
        return rideDisplayList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public itemViewHolder(View ItemView){
            super(ItemView);

        }
        @Override
        public void onClick(View view) {

        }
    }
}
