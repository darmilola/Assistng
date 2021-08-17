package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import ng.assist.UIs.MessagesFixtures;
import ng.assist.UIs.ViewModel.Message;
import ng.assist.UIs.chatkit.commons.ImageLoader;
import ng.assist.UIs.chatkit.messages.MessageInput;
import ng.assist.UIs.chatkit.messages.MessagesList;
import ng.assist.UIs.chatkit.messages.MessagesListAdapter;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.view.View;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity   implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageInput.TypingListener, MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {


    private static final int TOTAL_MESSAGES_COUNT = 100;

    protected final String senderId = "0";
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private int selectionCount;
    private Date lastLoadedDate;
    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.messagesList = (MessagesList) findViewById(R.id.messagesList);

        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);
    }


    private void initAdapter() {

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        };

        messagesAdapter = new MessagesListAdapter<Message>(senderId, imageLoader);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.registerViewClickListener(R.id.image,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {

                        if(message.getImageUrl() != null && !message.getImageUrl().equalsIgnoreCase("")){

                            Intent intent = new Intent(ChatActivity.this,ChatFullImage.class);
                            intent.putExtra("imageUrl",message.getImageUrl());
                            startActivity(intent);
                        }
                    }
                });
        messagesAdapter.setOnMessageViewClickListener(new MessagesListAdapter.OnMessageViewClickListener<Message>() {
            @Override
            public void onMessageViewClick(View view, Message message) {

            }
        });
        messagesList.setAdapter(messagesAdapter);
    }

    @Override
    public void onStartTyping() {

    }

    @Override
    public void onStopTyping() {

    }


    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.addToStart(MessagesFixtures.getTextMessage(), true);
    }





    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }


    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;

    }

    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
                lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
                messagesAdapter.addToEnd(messages, false);
            }
        }, 1000);
    }


    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }

    @Override
    public void onAddAttachments() {

    }

    @Override
    public boolean onSubmit(CharSequence input) {
        return true;
    }


    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }
}