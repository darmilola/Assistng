package ng.assist.Adapters;

import android.content.Context;
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
import ng.assist.R;
import ng.assist.UIs.ViewModel.AccomodationListModel;

public class AccomodationListingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ACCOMMODATION = 1;
    private static final int TYPE_LOADING = 2;
    private boolean isLoaderVisible = false;
    ArrayList<AccomodationListModel> accomodationList;
    Context context;


    public AccomodationListingsAdapter(ArrayList<AccomodationListModel> accomodationList, Context context){
        this.accomodationList = accomodationList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_LOADING:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item_progress, parent, false);
                return new LoadingItemViewHolder(view2);

            case TYPE_ACCOMMODATION:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.accomodation_list_item, parent, false);
                return new AccomodationItemViewHolder(view);
        }
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item_progress, parent, false);
        return new LoadingItemViewHolder(view2);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          AccomodationListModel accomodationListModel = accomodationList.get(position);
          if(holder instanceof AccomodationItemViewHolder){
              AccomodationItemViewHolder viewHolder = (AccomodationItemViewHolder) holder;
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

    }


    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == accomodationList.size() - 1 ? TYPE_LOADING : TYPE_ACCOMMODATION;
        } else {
            return TYPE_ACCOMMODATION;
        }
    }

    public void addItems(List<AccomodationListModel> Items) {
        accomodationList.addAll(Items);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        accomodationList.add(new AccomodationListModel(2));
        notifyItemInserted(accomodationList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = accomodationList.size() - 1;
        AccomodationListModel item = getItem(position);
        if (item != null) {
            accomodationList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private AccomodationListModel getItem(int position) {
        return accomodationList.get(position);
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

        }
    }

    public class LoadingItemViewHolder extends RecyclerView.ViewHolder{
        public LoadingItemViewHolder(View ItemView){
            super(ItemView);
        }
    }
}
