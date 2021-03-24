package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;

public class BusesAdapter extends RecyclerView.Adapter<BusesAdapter.itemViewHolder> {

    ArrayList<String> busList;
    Context context;
    public BusesClickedListener busesClickedListener;
    public interface BusesClickedListener{
        public void onBusCliked();
    }


    public BusesAdapter(ArrayList<String> busList, Context context,BusesClickedListener busesClickedListener){
        this.busList = busList;
        this.context = context;
        this.busesClickedListener = busesClickedListener;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_transport_itemview_layout, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public itemViewHolder(View ItemView){
            super(ItemView);
            ItemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {

            busesClickedListener.onBusCliked();
        }
    }
}
