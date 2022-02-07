package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ng.assist.R;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.ProductImageModel;

public class EditProductImageAdapter extends RecyclerView.Adapter<EditProductImageAdapter.itemViewHolder> {

    ArrayList<ProductImageModel> imagesList;
    Context context;


    public EditProductImageAdapter(ArrayList<ProductImageModel> imagesList, Context context){
        this.imagesList = imagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_image_recycler_item, parent, false);
        return new itemViewHolder(view2);
    }

    public void addItem(ProductImageModel imageModel){
        imagesList.add(imageModel);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        Glide.with(context)
                .load(imagesList.get(position).getImageUrl())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.imageView);

    }


    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        ImageView removeItem;
        public itemViewHolder(View ItemView){
            super(ItemView);
            imageView = ItemView.findViewById(R.id.recycler_image_item);
            removeItem = ItemView.findViewById(R.id.image_remove);
            ItemView.setOnClickListener(this);

            removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imagesList.size() == 1){

                    }
                    else{
                        EcommerceDashboardModel ecommerceDashboardModel = new EcommerceDashboardModel(context,imagesList.get(getAdapterPosition()).getId());
                        imagesList.remove(getAdapterPosition());
                        notifyDataSetChanged();
                        ecommerceDashboardModel.deleteProductImage();
                        ecommerceDashboardModel.setUpdateInfoListener(new EcommerceDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(String message) {

                            }
                        });


                    }
                }
            });

        }
        @Override
        public void onClick(View view) {

        }
    }
}
