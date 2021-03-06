package ng.assist.Adapters;

import android.app.Activity;
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
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

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
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_grocery_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
          GroceryModel groceryModel = groceryList.get(position);
          holder.productName.setText(groceryModel.getProductName());
          holder.shopName.setText(groceryModel.getShopName());


        Locale NigerianLocale = new Locale("en","ng");
        String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(groceryModel.getPrice()));
        String formattedPrice = unFormattedPrice.replaceAll("\\.00","");
        holder.productPrice.setText(formattedPrice);

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
         TextView shopName;
        public itemViewHolder(View ItemView){
            super(ItemView);
            productImage = ItemView.findViewById(R.id.grocery_item_image);
            productName = ItemView.findViewById(R.id.grocery_item_title);
            productPrice = ItemView.findViewById(R.id.grocery_item_price);
            shopName = ItemView.findViewById(R.id.grocery_product_shop_name);
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
