package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;
import ng.assist.UIs.ViewModel.GroceryModel;

public class GroceryCartAdapter extends RecyclerView.Adapter<GroceryCartAdapter.itemViewHolder> {

    ArrayList<GroceryModel> groceryList;
    Context context;


    public GroceryCartAdapter(ArrayList<GroceryModel> groceryList, Context context){
        this.groceryList = groceryList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_cart_recycler_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
         GroceryModel groceryModel = groceryList.get(position);
         holder.quantity.setText(groceryModel.getQuantity());
         holder.price.setText(groceryModel.getPrice());
         holder.qtyPrice.setText(Integer.toString(groceryModel.getQtyPrice()));
         holder.productName.setText(groceryModel.getProductName());
        Glide.with(context)
                .load(groceryModel.getDisplayImage())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.displayImage);
        holder.decreaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(holder.quantity.getText().toString()) == 1){

                }
                else{
                    int currentQty = Integer.parseInt(holder.quantity.getText().toString());
                    holder.quantity.setText(Integer.toString(currentQty-1));
                }
            }

        });
        holder.increaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int currentQty = Integer.parseInt(holder.quantity.getText().toString());
                    holder.quantity.setText(Integer.toString(currentQty+1));
                }
        });
        holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groceryList.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView productName, price,qtyPrice, quantity;
        LinearLayout increaseQty, decreaseQty;
        ImageView displayImage;
        MaterialButton removeFromCart;

        public itemViewHolder(View ItemView){
            super(ItemView);
            productName = ItemView.findViewById(R.id.cart_product_name);
            price = ItemView.findViewById(R.id.cart_product_price);
            qtyPrice = ItemView.findViewById(R.id.cart_product_quantity_price);
            quantity = ItemView.findViewById(R.id.cart_product_quantity);
            increaseQty = ItemView.findViewById(R.id.cart_product_increase);
            decreaseQty = ItemView.findViewById(R.id.cart_product_decrease);
            displayImage = ItemView.findViewById(R.id.cart_product_image);
            removeFromCart = ItemView.findViewById(R.id.cart_product_remove);

        }
        @Override
        public void onClick(View view) {

        }
    }
}
