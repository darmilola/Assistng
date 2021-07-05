package ng.assist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;

public class ProductImageScrollAdapter extends RecyclerView.Adapter<ProductImageScrollAdapter.itemViewHolder> {

    ArrayList<String> imagesList;
    Context context;


    public ProductImageScrollAdapter(ArrayList<String> imagesList, Context context){
        this.imagesList = imagesList;
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
    }


    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public itemViewHolder(View ItemView){
            super(ItemView);
            ItemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {

        }
    }
}
