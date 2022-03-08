package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ng.assist.R;
import ng.assist.UIs.ViewModel.RefundModel;

public class ProviderBookingAdapter extends RecyclerView.Adapter<ProviderBookingAdapter.itemViewHolder> {

    ArrayList<String> bookingList;
    Context context;


    public ProviderBookingAdapter(ArrayList<String> bookingList, Context context){
        this.bookingList = bookingList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_bookings_item, parent, false);
        return new itemViewHolder(view2);

    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {

        public itemViewHolder(View ItemView) {
            super(ItemView);


        }

    }

}
