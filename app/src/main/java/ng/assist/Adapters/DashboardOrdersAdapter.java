package ng.assist.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.DashboardProductDetails;
import ng.assist.DashboardViewOrder;
import ng.assist.R;
import ng.assist.UIs.ViewModel.Orders;

public class DashboardOrdersAdapter extends RecyclerView.Adapter<DashboardOrdersAdapter.itemViewHolder> {

    ArrayList<Orders> groceryList;
    Context context;
    ViewOrderClickedListener viewOrderClickedListener;

    public interface ViewOrderClickedListener{
        void onViewClicked(int position);
    }


    public DashboardOrdersAdapter(ArrayList<Orders> groceryList, Context context){
        this.groceryList = groceryList;
        this.context = context;
    }

    public void setViewOrderClickedListener(ViewOrderClickedListener viewOrderClickedListener) {
        this.viewOrderClickedListener = viewOrderClickedListener;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_recycler_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        Orders orders = groceryList.get(position);
        Locale NigerianLocale = new Locale("en","ng");
        String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(orders.getTotalPrice()));
        String formattedPrice = unFormattedPrice.replaceAll("\\.00","");
              holder.totalPrice.setText(formattedPrice);
              holder.customerName.setText(orders.getUserFirstname()+" "+orders.getUserLastname());
              holder.status.setText(orders.getStatus());
    }

    public void removeItem(int position){
        groceryList.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {

        TextView totalPrice,customerName,status;
        MaterialButton viewOrder;

        public itemViewHolder(View ItemView){
            super(ItemView);
            totalPrice = ItemView.findViewById(R.id.dashboard_order_totalprice);
            customerName = ItemView.findViewById(R.id.dashboard_order_customer_name);
            status = ItemView.findViewById(R.id.dashboard_order_delivery_status);
            viewOrder = ItemView.findViewById(R.id.dashboard_order_view_order);

            viewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewOrderClickedListener.onViewClicked(getAdapterPosition());
                }
            });

        }

    }
}
