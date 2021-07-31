package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.GroceryStoreListing;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.Utils.LoadingDialogUtils;
import ng.assist.UIs.ViewModel.GroceryModel;
import ng.assist.UIs.ViewModel.ServicesModel;

public class GroceryDisplayAdapter extends RecyclerView.Adapter<GroceryDisplayAdapter.itemViewHolder> {

    ArrayList<GroceryModel> groceryList;
    Context context;


    public GroceryDisplayAdapter(ArrayList<GroceryModel> groceryList, Context context){
        this.groceryList = groceryList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_recycler_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
          GroceryModel groceryModel = groceryList.get(position);
          holder.productPrice.setText(groceryModel.getPrice());
          holder.productName.setText(groceryModel.getProductName());

        Glide.with(context)
                .load(groceryModel.getDisplayImage())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.productImage);
    }

    public void addItem(ArrayList<GroceryModel> mGroceryList){
        groceryList.addAll(mGroceryList);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
         TextView productName;
         ImageView productImage;
         TextView productPrice;
         LinearLayout addToCart;
        public itemViewHolder(View ItemView){
            super(ItemView);
            productImage = ItemView.findViewById(R.id.grocery_product_image);
            productName = ItemView.findViewById(R.id.grocery_product_name);
            productPrice = ItemView.findViewById(R.id.grocery_product_price);
            addToCart = ItemView.findViewById(R.id.grocery_add_to_cart);

            addToCart.setOnClickListener(new View.OnClickListener() {
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
            });

            ItemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(context,GroceryStoreListing.class);
            intent.putExtra("product",groceryList.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }
}
