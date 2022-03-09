package ng.assist.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.assist.R;
import ng.assist.UIs.ViewModel.ProviderBookingsModel;
import ng.assist.UIs.ViewModel.RefundModel;
import ng.assist.UIs.chatkit.utils.DateFormatter;

public class ProviderBookingAdapter extends RecyclerView.Adapter<ProviderBookingAdapter.itemViewHolder> {

    ArrayList<ProviderBookingsModel> bookingList;
    Context context;


    public ProviderBookingAdapter(ArrayList<ProviderBookingsModel> bookingList, Context context){
        this.bookingList = bookingList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_bookings_item, parent, false);
        return new itemViewHolder(view2);

    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
           ProviderBookingsModel providerBookingsModel = bookingList.get(position);
           holder.userName.setText(providerBookingsModel.getUserFirstname()+" "+providerBookingsModel.getUserLastname());
           holder.status.setText(providerBookingsModel.getBookingStatus());
           holder.amount.setText("NGN "+providerBookingsModel.getBookingAmount());

        Glide.with(context)
                .load(providerBookingsModel.getUserImageUrl())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(holder.imageView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Timestamp timestamp = null;
        Date currentDate = new Date();
        dateFormat.format(currentDate);

        try {

            Date parsedDate = dateFormat.parse(providerBookingsModel.getBookingTimestamp());
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            Log.e("TAG ", e.getLocalizedMessage());
        }
        Date chatDate = new Date(timestamp.getTime());
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(chatDate);               // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1);      // adds one hour


        if(DateFormatter.isSameDay(currentDate,chatDate)){
            holder.timestamp.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
        }
        else{
            holder.timestamp.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
        }

    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }



    public class itemViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView userName, status, timestamp, amount;

        public itemViewHolder(View ItemView) {
            super(ItemView);
            imageView = ItemView.findViewById(R.id.bookings_profile_image);
            userName = ItemView.findViewById(R.id.booking_user_name);
            status = ItemView.findViewById(R.id.booking_status);
            timestamp = ItemView.findViewById(R.id.bookings_timestamp);
            amount = ItemView.findViewById(R.id.bookings_amount);
        }

    }

}
