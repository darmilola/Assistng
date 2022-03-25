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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.CartModel;
import ng.assist.UIs.ViewModel.GroceryModel;

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.itemViewHolder> {

    ArrayList<CartModel> groceryList;
    Context context;


    public ViewOrderAdapter(ArrayList<CartModel> groceryList, Context context){
        this.groceryList = groceryList;
        this.context = context;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_transactions_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        CartModel groceryModel = groceryList.get(position);
        holder.quantity.setText(groceryModel.getQuantity());
        Locale NigerianLocale = new Locale("en","ng");
        String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(groceryModel.getPrice()));
        String formattedPrice = unFormattedPrice.replaceAll("\\.00","");
        holder.price.setText(formattedPrice);
        holder.productName.setText(groceryModel.getName());
        int quantity = Integer.parseInt(groceryModel.getQuantity());
        int price = Integer.parseInt(groceryModel.getPrice());
        int quantityPrice = quantity * price;
        String unFormattedQtyPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(quantityPrice);
        String formattedQtyPrice = unFormattedQtyPrice.replaceAll("\\.00","");
        holder.qtyPrice.setText(formattedQtyPrice);
        Glide.with(context)
                .load(groceryModel.getImageUrl())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.displayImage);

    }

    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView productName, price, qtyPrice, quantity;
        ImageView displayImage;

        public itemViewHolder(View ItemView){
            super(ItemView);
            productName = ItemView.findViewById(R.id.placed_order_name);
            price = ItemView.findViewById(R.id.placed_order_price);
            qtyPrice = ItemView.findViewById(R.id.placed_order_qty_price);
            quantity = ItemView.findViewById(R.id.placed_order_qty);
            displayImage = ItemView.findViewById(R.id.placed_order_image);

        }
        @Override
        public void onClick(View view) {

        }
    }
}
