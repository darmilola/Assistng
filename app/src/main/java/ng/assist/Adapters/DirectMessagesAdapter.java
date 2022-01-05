package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.commons.text.StringEscapeUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import ng.assist.ChatActivity;
import ng.assist.R;
import ng.assist.UIs.ViewModel.MessageConnectionModel;
import ng.assist.UIs.chatkit.utils.DateFormatter;

public class DirectMessagesAdapter extends RecyclerView.Adapter<DirectMessagesAdapter.itemViewHolder> {

    ArrayList<MessageConnectionModel> directMessagesItemList;
    Context context;


    public DirectMessagesAdapter(ArrayList<MessageConnectionModel> directMessagesItemList, Context context){
        this.directMessagesItemList = directMessagesItemList;
        this.context = context;
    }


    @NonNull
    @Override
    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.direct_message_item, parent, false);
        return new itemViewHolder(view2);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewHolder holder, int position) {
        MessageConnectionModel messageConnectionModel = directMessagesItemList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Timestamp timestamp = null;
        Date currentDate = new Date();
        dateFormat.format(currentDate);

        try {

            Date parsedDate = dateFormat.parse(messageConnectionModel.getTimestamp());
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            Log.e("TAG ", e.getLocalizedMessage());
        }
        Date chatDate = new Date(timestamp.getTime());
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(chatDate);               // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1);      // adds one hour


        holder.lastMessage.setText(StringEscapeUtils.unescapeJava(messageConnectionModel.getLastMessage()));

        if(DateFormatter.isSameDay(currentDate,chatDate)){
             holder.timestamp.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.TIME));
        }
        else{
            holder.timestamp.setText(DateFormatter.format(cal.getTime(), DateFormatter.Template.STRING_DAY_MONTH));
        }


        holder.displayName.setText(messageConnectionModel.getReceiverFirstname()+" "+messageConnectionModel.getReceiverLastname());
        if(messageConnectionModel.getUnreadCount() == 0){
            holder.unreadLayout.setVisibility(View.GONE);
        }
        else{
            holder.unreadCount.setText(Integer.toString(messageConnectionModel.getUnreadCount()));
        }
        Glide.with(context)
                .load(messageConnectionModel.getReceiverProfileImage())
                .placeholder(R.drawable.profileplaceholder)
                .error(R.drawable.profileplaceholder)
                .into(holder.profilePicture);
    }


    @Override
    public int getItemCount() {
        return directMessagesItemList.size();
    }

    public class itemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

       CircleImageView profilePicture;
       TextView displayName;
       TextView lastMessage;
       TextView timestamp;
       TextView unreadCount;
       LinearLayout unreadLayout;

        public itemViewHolder(View ItemView){
            super(ItemView);
            profilePicture = ItemView.findViewById(R.id.message_connection_sender_profile_image);
            displayName = ItemView.findViewById(R.id.message_connection_sender_name);
            lastMessage = ItemView.findViewById(R.id.message_connection_last_message);
            timestamp = ItemView.findViewById(R.id.message_connection_timestamp);
            unreadCount = ItemView.findViewById(R.id.message_connection_unread_count);
            unreadLayout = ItemView.findViewById(R.id.message_connection_unread_count_layout);
            ItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            Intent intent = new Intent(context,ChatActivity.class);
            intent.putExtra("receiverId",directMessagesItemList.get(getAdapterPosition()).getmReceiverId());
            intent.putExtra("receiverFirstname",directMessagesItemList.get(getAdapterPosition()).getReceiverFirstname());
            intent.putExtra("receiverLastname",directMessagesItemList.get(getAdapterPosition()).getReceiverLastname());
            intent.putExtra("receiverImageUrl",directMessagesItemList.get(getAdapterPosition()).getReceiverProfileImage());
            context.startActivity(intent);

        }
    }
}
