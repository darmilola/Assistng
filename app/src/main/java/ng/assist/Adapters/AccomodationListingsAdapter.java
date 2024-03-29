package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            viewHolder.houseTitle.setText(accomodationListModel.getHouseTitle());


        Locale NigerianLocale = new Locale("en","ng");
        String unFormattedPrice2 = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(accomodationListModel.getPricesPerMonth()));
        String formattedPrice2 = unFormattedPrice2.replaceAll("\\.00","");

        if(accomodationListModel.getType().equalsIgnoreCase("lodges")){
            viewHolder.pricePerMonth.setText(formattedPrice2);
            viewHolder.perTag.setText("/year");
        }
        else{
            viewHolder.pricePerMonth.setText(formattedPrice2);
            viewHolder.perTag.setText("/day");
        }


        if(accomodationListModel.getIsRunningWater().equalsIgnoreCase("true")){
            viewHolder.runningWater.setVisibility(View.VISIBLE);
        }



        if(accomodationListModel.getIsRepair().equalsIgnoreCase("true")){
            viewHolder.needsRepair.setVisibility(View.VISIBLE);
        }




        viewHolder.baths.setText(accomodationListModel.getBaths());
        viewHolder.beds.setText(accomodationListModel.getBeds());

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
        TextView houseTitle,pricePerMonth,perTag,beds,baths;
        ImageView displayImage,runningWater,needsRepair;
        public AccomodationItemViewHolder(View ItemView){
            super(ItemView);
            houseTitle = ItemView.findViewById(R.id.accomodation_title);
            pricePerMonth = ItemView.findViewById(R.id.accomodation_price_per_month);
            displayImage = ItemView.findViewById(R.id.accomodation_display_image);
            perTag = ItemView.findViewById(R.id.accommodation_listing_per_tag_text);
            beds = ItemView.findViewById(R.id.accomodation_listing_beds);
            baths = ItemView.findViewById(R.id.accomodation_listing_baths);
            runningWater = ItemView.findViewById(R.id.running_water);
            needsRepair = ItemView.findViewById(R.id.needs_repair);

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

