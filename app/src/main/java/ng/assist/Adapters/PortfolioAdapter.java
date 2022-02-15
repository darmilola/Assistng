package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;
import ng.assist.UIs.ViewModel.GroceryModel;
import ng.assist.UIs.ViewModel.ServiceProviderDashboardModel;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.itemViewHolder> {

    ArrayList<ServiceProviderDashboardModel.ProviderPortfolio> portfolioItemList;
    Context context;


    public PortfolioAdapter(ArrayList<ServiceProviderDashboardModel.ProviderPortfolio> portfolioItemList, Context context){
        this.portfolioItemList = portfolioItemList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_image_recycler_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        Glide.with(context)
                .load(portfolioItemList.get(position).getImageUrl())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.ImageItem);
    }

    public void addItem(ServiceProviderDashboardModel.ProviderPortfolio providerPortfolio){
        portfolioItemList.add(providerPortfolio);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return portfolioItemList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView ImageItem;
        ImageView removeButton;
        public itemViewHolder(View ItemView){
            super(ItemView);
            ImageItem = ItemView.findViewById(R.id.recycler_image_item);
            removeButton = ItemView.findViewById(R.id.image_remove);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(portfolioItemList.size() > 1) {

                        ServiceProviderDashboardModel serviceProviderDashboardModel = new ServiceProviderDashboardModel(portfolioItemList.get(getAdapterPosition()).getId());
                        serviceProviderDashboardModel.deletePortfolioImage();
                        serviceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {

                            }
                        });
                        portfolioItemList.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }

                }
            });


        }
        @Override
        public void onClick(View view) {

        }
    }
}
