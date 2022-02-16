package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import ng.assist.R;

public class ServiceCategoryAdapter extends RecyclerView.Adapter<ServiceCategoryAdapter.itemViewHolder> {

    ArrayList<String> mServiceCategoryList;
    Context context;
    ItemClickedListener itemClickedListener;


    public interface ItemClickedListener{
        void onItemClicked(String item);
    }

    public void setItemClickedListener(ItemClickedListener itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    public ServiceCategoryAdapter(ArrayList<String> mServiceCategoryList, Context context){
        this.mServiceCategoryList = mServiceCategoryList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_category_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        String category = mServiceCategoryList.get(position);
        holder.title.setText(category);
    }


    @Override
    public int getItemCount() {
        return mServiceCategoryList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;

        public itemViewHolder(View ItemView){
            super(ItemView);
            title = ItemView.findViewById(R.id.service_category_title);
            ItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
           itemClickedListener.onItemClicked(mServiceCategoryList.get(getAdapterPosition()));
        }
    }

}
