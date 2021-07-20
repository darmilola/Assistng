package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.GroceryStoreListing;
import ng.assist.R;

public class ListDialogAdapter extends RecyclerView.Adapter<ListDialogAdapter.itemViewHolder> {

    ArrayList<String> itemList;
    Context context;
    private ListItemClickListener listItemClickListener;

    public interface  ListItemClickListener{
        void onItemClick(String city);
    }


    public ListDialogAdapter(ArrayList<String> itemList, Context context){
        this.itemList = itemList;
        this.context = context;
    }

    public void setListItemClickListener(ListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {

        holder.city.setText(itemList.get(position));
        holder.city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              listItemClickListener.onItemClick(itemList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder{

        TextView city;

        public itemViewHolder(View ItemView){
            super(ItemView);
            city = ItemView.findViewById(R.id.location_city);

        }

    }
}
