package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.AccomodationBooking;
import ng.assist.R;
import ng.assist.UIs.ViewModel.AccomodationListModel;

public class AccomodationListingsAdapter extends RecyclerView.Adapter<AccomodationListingsAdapter.AccomodationItemViewHolder> {

    ArrayList<AccomodationListModel> accomodationList;
    Context context;


    public AccomodationListingsAdapter(ArrayList<AccomodationListModel> accomodationList, Context context){
        this.accomodationList = accomodationList;
        this.context = context;
    }


    @NonNull
    @Override
    public  AccomodationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accomodation_list_item, parent, false);
        return new AccomodationItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccomodationItemViewHolder viewHolder, int position) {
        AccomodationListModel accomodationListModel = accomodationList.get(position);
            viewHolder.ratings.setText(accomodationListModel.getTotalRatings());
            viewHolder.baths.setText(accomodationListModel.getBaths());
            viewHolder.beds.setText(accomodationListModel.getBeds());
            viewHolder.houseTitle.setText(accomodationListModel.getHouseTitle());
            viewHolder.pricePerMonth.setText(accomodationListModel.getPricesPerMonth());
            Glide.with(context)
                    .load(accomodationListModel.getHouseDisplayImage())
                    .placeholder(R.drawable.background_image)
                    .error(R.drawable.background_image)
                    .into(viewHolder.displayImage);
        }


        public void addItem(ArrayList<AccomodationListModel> accList){
              accomodationList.addAll(accList);
              notifyDataSetChanged();
        }
    @Override
    public int getItemCount() {
        return accomodationList == null ? 0 : accomodationList.size();
    }

    public class AccomodationItemViewHolder extends RecyclerView.ViewHolder{
        TextView houseTitle,beds,baths,pricePerMonth,ratings,rateCount;
        ImageView displayImage;
        public AccomodationItemViewHolder(View ItemView){
            super(ItemView);
            houseTitle = ItemView.findViewById(R.id.accomodation_title);
            beds = ItemView.findViewById(R.id.accomodation_beds);
            baths = ItemView.findViewById(R.id.accomodation_baths);
            pricePerMonth = ItemView.findViewById(R.id.accomodation_price_per_month);
            ratings = ItemView.findViewById(R.id.accomodation_rating);
            rateCount = ItemView.findViewById(R.id.accomodation_rate_counts);
            displayImage = ItemView.findViewById(R.id.accomodation_display_image);

            ItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AccomodationListModel accomodationListModel = accomodationList.get(getAdapterPosition());
                    Intent intent = new Intent(context, AccomodationBooking.class);
                    intent.putExtra("accModel", accomodationListModel);
                    context.startActivity(intent);
                }
            });
        }
    }
}
