package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.R;
import ng.assist.ServiceProviderDetails;
import ng.assist.UIs.ViewModel.ServicesModel;

public class ServiceProvidersAdapter extends RecyclerView.Adapter<ServiceProvidersAdapter.itemViewHolder> {

    ArrayList<ServicesModel> mServiceProviderList;
    Context context;


    public ServiceProvidersAdapter(ArrayList<ServicesModel> serviceProviderList, Context context){
        this.mServiceProviderList = serviceProviderList;
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

        ServicesModel servicesModel = mServiceProviderList.get(position);
        holder.title.setText(servicesModel.getJobTitle());
        holder.ratePerhour.setText(servicesModel.getRatePerHour());
        holder.ratings.setText(servicesModel.getHandymanRatings());
        holder.jobs.setText(servicesModel.getHandymanJobs());
        holder.name.setText(servicesModel.getHandymanFirstname()+" "+ servicesModel.getHandymanLastname());

        Glide.with(context)
                .load(servicesModel.getHandymanImage())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.imageView);
    }

    public void addItem(ArrayList<ServicesModel> serviceProviderList){
        mServiceProviderList.addAll(serviceProviderList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mServiceProviderList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
       ImageView imageView;
       TextView name,ratings,jobs,ratePerhour,title;

        public itemViewHolder(View ItemView){
            super(ItemView);
            imageView = ItemView.findViewById(R.id.service_provider_image);
            name = ItemView.findViewById(R.id.service_provider_name);
            ratings = ItemView.findViewById(R.id.service_provider_rating);
            jobs = ItemView.findViewById(R.id.service_provider_jobs_count);
            ratePerhour = ItemView.findViewById(R.id.services_provider_rate_per_hour);
            title = ItemView.findViewById(R.id.service_provider_job_title);
            ItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,ServiceProviderDetails.class);
            intent.putExtra("provider_info",mServiceProviderList.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }

}
