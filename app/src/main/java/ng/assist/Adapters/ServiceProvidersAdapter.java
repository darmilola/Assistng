package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;
import ng.assist.ServiceProviderDetails;

public class ServiceProvidersAdapter extends RecyclerView.Adapter<ServiceProvidersAdapter.itemViewHolder> {

    ArrayList<String> servicePrividerList;
    Context context;


    public ServiceProvidersAdapter(ArrayList<String> serviceProviderList, Context context){
        this.servicePrividerList = serviceProviderList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_provider_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
    }


    @Override
    public int getItemCount() {
        return servicePrividerList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public itemViewHolder(View ItemView){
            super(ItemView);
            ItemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
           context.startActivity(new Intent(context, ServiceProviderDetails.class));
        }
    }
}
