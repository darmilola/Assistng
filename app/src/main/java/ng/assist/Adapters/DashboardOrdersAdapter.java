package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.DashboardProductDetails;
import ng.assist.DashboardViewOrder;
import ng.assist.R;

public class DashboardOrdersAdapter extends RecyclerView.Adapter<DashboardOrdersAdapter.itemViewHolder> {

    ArrayList<String> groceryList;
    Context context;


    public DashboardOrdersAdapter(ArrayList<String> groceryList, Context context){
        this.groceryList = groceryList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_recycler_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return groceryList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder {


        public itemViewHolder(View ItemView){
            super(ItemView);
            ItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, DashboardViewOrder.class));
                }
            });

        }

    }
}
