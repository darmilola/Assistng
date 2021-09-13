package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.AccomodationBooking;
import ng.assist.EstateDashboardListingDetails;
import ng.assist.R;
import ng.assist.UIs.ViewModel.AccomodationListModel;

public class RealEstateDashboardListingAdapter extends RecyclerView.Adapter<RealEstateDashboardListingAdapter.itemViewHolder> {

    ArrayList<AccomodationListModel> accomodationList;
    Context context;


    public RealEstateDashboardListingAdapter(ArrayList<AccomodationListModel> accomodationList, Context context){
        this.accomodationList = accomodationList;
        this.context = context;

    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.accomodation_list_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        AccomodationListModel accomodationListModel = accomodationList.get(position);
        holder.ratings.setText(accomodationListModel.getTotalRatings());
        holder.baths.setText(accomodationListModel.getBaths());
        holder.beds.setText(accomodationListModel.getBeds());
        holder.houseTitle.setText(accomodationListModel.getHouseTitle());
        holder.pricePerMonth.setText(accomodationListModel.getPricesPerMonth());
        Glide.with(context)
                .load(accomodationListModel.getHouseDisplayImage())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.displayImage);
    }

    @Override
    public int getItemCount() {
        return accomodationList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {
        TextView houseTitle,beds,baths,pricePerMonth,ratings,rateCount;
        ImageView displayImage;
        public itemViewHolder(View ItemView){
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
                    Intent intent = new Intent(context,EstateDashboardListingDetails.class);
                    intent.putExtra("accModel", accomodationListModel);
                    context.startActivity(intent);
                }
            });

        }

    }
}
