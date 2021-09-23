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
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.CartModel;

public class PlacedOrderAdapter extends RecyclerView.Adapter<PlacedOrderAdapter.itemViewHolder> {

    ArrayList<CartModel> cartModelArrayList;
    Context context;


    public PlacedOrderAdapter(ArrayList<CartModel> cartModelArrayList, Context context){
           this.cartModelArrayList = cartModelArrayList;
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
        CartModel cartModel = cartModelArrayList.get(position);
        holder.productName.setText(cartModel.getName());
        holder.price.setText(cartModel.getPrice());
        int quantity = Integer.parseInt(cartModel.getQuantity());
        int price = Integer.parseInt(cartModel.getPrice());
        holder.qtyPrice.setText(Integer.toString(price * quantity));
        holder.quantity.setText(cartModel.getQuantity());
        Glide.with(context)
                .load(cartModel.getImageUrl())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.displayImage);


    }

    @Override
    public int getItemCount() {
        return cartModelArrayList.size();
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
