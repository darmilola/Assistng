package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.AccomodationBooking;
import ng.assist.EstateDashboardListingDetails;
import ng.assist.R;
import ng.assist.UIs.ViewModel.AccomodationListModel;

public class RealEstateDashboardListingAdapter extends RecyclerView.Adapter<RealEstateDashboardListingAdapter.itemViewHolder> {

    ArrayList<AccomodationListModel> accomodationList;
    Context context;
    ItemClickListener itemClickListener;


    public interface ItemClickListener{
        void onItemClick(int position);
    }


    public RealEstateDashboardListingAdapter(ArrayList<AccomodationListModel> accomodationList, Context context){
        this.accomodationList = accomodationList;
        this.context = context;

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
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
        holder.houseTitle.setText(accomodationListModel.getHouseTitle());
        holder.pricePerMonth.setText(accomodationListModel.getPricesPerMonth());


        Locale NigerianLocale = new Locale("en","ng");
        String unFormattedPrice2 = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(accomodationListModel.getPricesPerMonth()));
        String formattedPrice2 = unFormattedPrice2.replaceAll("\\.00","");

        if(accomodationListModel.getType().equalsIgnoreCase("lodges")){
            holder.pricePerMonth.setText(formattedPrice2);
            holder.perTag.setText("/year");
        }
        else{
            holder.pricePerMonth.setText(formattedPrice2);
            holder.perTag.setText("/day");
        }

        holder.baths.setText(accomodationListModel.getBaths());
        holder.beds.setText(accomodationListModel.getBeds());


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
        TextView houseTitle,pricePerMonth,perTag,beds,baths;
        ImageView displayImage;
        public itemViewHolder(View ItemView){
            super(ItemView);
            houseTitle = ItemView.findViewById(R.id.accomodation_title);
            pricePerMonth = ItemView.findViewById(R.id.accomodation_price_per_month);
            displayImage = ItemView.findViewById(R.id.accomodation_display_image);
            perTag = ItemView.findViewById(R.id.accommodation_listing_per_tag_text);
            beds = ItemView.findViewById(R.id.accomodation_listing_beds);
            baths = ItemView.findViewById(R.id.accomodation_listing_baths);

            ItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   itemClickListener.onItemClick(getAdapterPosition());
                }
            });

        }

    }
}
