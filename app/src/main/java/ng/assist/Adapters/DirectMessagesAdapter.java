package ng.assist.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.MediaCodec;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import ng.assist.ChatActivity;
import ng.assist.R;
import ng.assist.UIs.ViewModel.MessageConnectionModel;

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
        holder.lastMessage.setText(messageConnectionModel.getLastMessage());
        holder.timestamp.setText(messageConnectionModel.getTimestamp());
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
            intent.putExtra("receiverId",directMessagesItemList.get(getAdapterPosition()).getReceiverId());
            intent.putExtra("receiverFirstname",directMessagesItemList.get(getAdapterPosition()).getReceiverFirstname());
            intent.putExtra("receiverLastname",directMessagesItemList.get(getAdapterPosition()).getReceiverLastname());
            context.startActivity(intent);

        }
    }
}
