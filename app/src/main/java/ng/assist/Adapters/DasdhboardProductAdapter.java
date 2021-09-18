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
import ng.assist.DashboardProductDetails;
import ng.assist.GroceryStoreListing;
import ng.assist.R;
import ng.assist.UIs.ViewModel.GroceryModel;

public class DasdhboardProductAdapter extends RecyclerView.Adapter<DasdhboardProductAdapter.itemViewHolder> {

    ArrayList<GroceryModel> groceryList;
    Context context;


    public DasdhboardProductAdapter(ArrayList<GroceryModel> groceryList, Context context){
        this.groceryList = groceryList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_grocery_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        GroceryModel groceryModel = groceryList.get(position);
        holder.productPrice.setText(groceryModel.getPrice());
        holder.productName.setText(groceryModel.getProductName());
        holder.shopname.setText(groceryModel.getShopName());

        Glide.with(context)
                .load(groceryModel.getDisplayImage())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.productImage);

    }


    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView productName;
        ImageView productImage;
        TextView productPrice;
        TextView shopname;
        public itemViewHolder(View ItemView){
            super(ItemView);
            productImage = ItemView.findViewById(R.id.grocery_item_image);
            productName = ItemView.findViewById(R.id.grocery_item_title);
            productPrice = ItemView.findViewById(R.id.grocery_item_price);
            shopname = ItemView.findViewById(R.id.grocery_item_shopname);
        /*    ItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     String userEmail = PreferenceManager.getDefaultSharedPreferences(context).getString("userEmail","");
                     GroceryModel groceryModel = groceryList.get(getAdapterPosition());
                     GroceryModel CartModel = new GroceryModel(groceryModel.getItemId(),groceryModel.getRetailerId(),userEmail,"1",context);
                     CartModel.addToCart();
                     CartModel.setCartListener(new GroceryModel.CartListener() {
                         @Override
                         public void onAdded() {
                             Intent intent = new Intent(context,GroceryStoreListing.class);
                             intent.putExtra("product",groceryList.get(getAdapterPosition()));
                             context.startActivity(intent);
                             Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                         }
                         @Override
                         public void onError() {
                             Toast.makeText(context, "Error adding to Cart", Toast.LENGTH_SHORT).show();
                         }
                     });
                }
            });*/

            ItemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(context,DashboardProductDetails.class);
            intent.putExtra("product",groceryList.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }
}
