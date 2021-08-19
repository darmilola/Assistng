package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageConnectionModel {
    private String receiverFirstname;
    private String receiverLastname;
    private String receiverProfileImage;
    private String mReceiverId;
    private String lastMessage;
    private String timestamp;
    private int unreadCount;
    private String senderId;
    private String receiverId;
    Context context;
    private String baseUrl = new URL().getBaseUrl();
    private String connectionUrl = baseUrl+"message_connection/get";
    private String userEmail;
    private ConnectionListener connectionListener;
    private ArrayList<MessageConnectionModel> messageConnectionModelArrayList = new ArrayList<>();

    public interface ConnectionListener{
        void onConnectionReady(ArrayList<MessageConnectionModel> messageConnectionModels);
        void onConnectionEmpty(String message);
    }

    public MessageConnectionModel(String userEmail, Context context){
           this.userEmail = userEmail;
           this.context = context;
    }

    public MessageConnectionModel(String receiverFirstname, String receiverLastname, String receiverProfileImage,String mReceiverId, String lastMessage, String timestamp, int unreadCount, String senderId, String receiverId){
        this.receiverFirstname = receiverFirstname;
        this.receiverLastname = receiverLastname;
        this.receiverProfileImage = receiverProfileImage;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.unreadCount = unreadCount;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.mReceiverId = mReceiverId;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getReceiverLastname() {
        return receiverLastname;
    }

    public String getReceiverFirstname() {
        return receiverFirstname;
    }

    public String getReceiverProfileImage() {
        return receiverProfileImage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getmReceiverId() {
        return mReceiverId;
    }

    private Handler getConnectionHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++){
                        String senderId = data.getJSONObject(i).getString("senderId");
                        String receiverId = data.getJSONObject(i).getString("receiverId");
                        String lastMessage = data.getJSONObject(i).getString("lastMessage");
                        int unreadCount = data.getJSONObject(i).getInt("unreadCount");
                        String senderFirstname = data.getJSONObject(i).getString("senderFirstname");
                        String senderLastname = data.getJSONObject(i).getString("senderLastname");
                        String receiverFirstname = data.getJSONObject(i).getString("receiverFirstname");
                        String receiverLastname = data.getJSONObject(i).getString("receiverLastname");
                        String senderPicture = data.getJSONObject(i).getString("senderPicture");
                        String receiverPicture = data.getJSONObject(i).getString("receiverPicture");
                        String timestamp = data.getJSONObject(i).getString("timestamp");
                        String mReceiverFirstname,mReceiverLastname,mReceiverPicture,mReceiverId;
                        if(MessageConnectionModel.this.userEmail.equalsIgnoreCase(senderId)){
                               mReceiverFirstname = receiverFirstname;
                               mReceiverLastname = receiverLastname;
                               mReceiverPicture = receiverPicture;
                               mReceiverId = receiverId;
                        }
                        else{
                               mReceiverFirstname = senderFirstname;
                               mReceiverLastname = senderLastname;
                               mReceiverPicture = senderPicture;
                               mReceiverId = senderId;
                        }
                        MessageConnectionModel messageConnectionModel = new MessageConnectionModel(mReceiverFirstname,mReceiverLastname,mReceiverPicture,mReceiverId,lastMessage,timestamp,unreadCount,senderId,receiverId);
                        messageConnectionModelArrayList.add(messageConnectionModel);
                    }
                    connectionListener.onConnectionReady(messageConnectionModelArrayList);
                }
                else{
                    connectionListener.onConnectionEmpty("No Messages");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                connectionListener.onConnectionEmpty("Error Occurred please try again");
            }
        }
    };

    public void getConnection(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildConnectionJson(this.userEmail));
            Request request = new Request.Builder()
                    .url(connectionUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = getConnectionHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            getConnectionHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private String buildConnectionJson(String email){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
