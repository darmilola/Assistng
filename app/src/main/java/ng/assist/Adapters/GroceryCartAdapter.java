package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.ViewModel.GroceryModel;

public class GroceryCartAdapter extends RecyclerView.Adapter<GroceryCartAdapter.itemViewHolder> {

    ArrayList<GroceryModel> groceryList;
    Context context;
    private String userId;
    private TotalpriceUpdateListener totalpriceUpdateListener;

    public interface TotalpriceUpdateListener{
        void onNewPrice(String totalPrice);
    }

    public void setTotalpriceUpdateListener(TotalpriceUpdateListener totalpriceUpdateListener) {
        this.totalpriceUpdateListener = totalpriceUpdateListener;
    }

    public GroceryCartAdapter(ArrayList<GroceryModel> groceryList, Context context,TotalpriceUpdateListener totalpriceUpdateListener){
        this.groceryList = groceryList;
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = preferences.getString("userEmail","");
        this.totalpriceUpdateListener = totalpriceUpdateListener;
        updateTotalprice(groceryList);
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
         Locale NigerianLocale = new Locale("en","ng");
         String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(groceryModel.getPrice()));
         String formattedPrice = unFormattedPrice.replaceAll("\\.00","");
         holder.price.setText(formattedPrice);
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
                    groceryList.get(position).setQuantity(String.valueOf(currentQty-1));
                    int quantityPrice = Integer.parseInt(groceryModel.getPrice()) * (currentQty-1);
                    holder.qtyPrice.setText(Integer.toString(quantityPrice));
                    updateTotalprice(groceryList);

                    GroceryModel groceryModel1 = new GroceryModel(groceryModel.getCartIndex(),Integer.toString(currentQty-1),context);
                    groceryModel1.UpdateUsersCart();
                }
            }

        });

        holder.increaseQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentQty = Integer.parseInt(holder.quantity.getText().toString());
                holder.quantity.setText(Integer.toString(currentQty+1));
                groceryList.get(position).setQuantity(String.valueOf(currentQty+1));
                updateTotalprice(groceryList);

                int quantityPrice = Integer.parseInt(groceryModel.getPrice()) * (currentQty+1);
                holder.qtyPrice.setText(Integer.toString(quantityPrice));

                GroceryModel groceryModel1 = new GroceryModel(groceryModel.getCartIndex(),Integer.toString(currentQty+1),context);
                groceryModel1.UpdateUsersCart();
            }
        });


        holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groceryList.remove(position);
                updateTotalprice(groceryList);
                GroceryModel groceryModel1 = new GroceryModel(groceryModel.getCartIndex(),"",context);
                groceryModel1.RemoveFromCart();
                notifyDataSetChanged();
            }
        });
    }

    public ArrayList<GroceryModel> getGroceryList() {
        return groceryList;
    }

    public void updateTotalprice(ArrayList<GroceryModel> groceryModels){
        int totalPrice = 0;
        for (GroceryModel groceryModel: groceryModels) {
            int quantity = Integer.parseInt(groceryModel.getQuantity());
            int price = Integer.parseInt(groceryModel.getPrice());
            int quantityPrice = quantity * price;
            totalPrice = totalPrice + quantityPrice;
        }
        totalpriceUpdateListener.onNewPrice(String.valueOf(totalPrice));
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
