package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import ng.assist.UIs.Utils.ImageUploadDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatModel {

    private Context context;
    private String baseUrl = new URL().getBaseUrl();
    private String uploadImageUrl = baseUrl+"users/upload/image";
    private String getMessagesUrl = baseUrl+"message/show";
    private String createMessageUrl = baseUrl+"message";
    private String updateCaughtUpUrl = baseUrl+"message_connection/update/caughtup";
    private String addConnectionUrl = baseUrl+"message_connection";
    private String updateConnectionUrl = baseUrl+"message_connection/update";
    private String verifyConnectionUrl = baseUrl+"message_connection/check";
    private ChatHttpImageUploadListener chatHttpImageUploadListener;
    private String uploadedImageUrl = "";
    private ImageUploadDialog imageUploadDialog;
    private String encodedImage;
    private String senderId,receiverId;
    private String userEmail;
    private String receiverImageUrl;
    private ArrayList<ng.assist.UIs.ViewModel.Message> messageArrayList = new ArrayList<>();
    ChatGetMessageListener chatGetMessageListener;
    String sentMessage,messageImageUrl;
    String lastMessage,senderFirstname,senderLastname,receiverFirstname,receiverLastname,senderPicture,receiverPicture;
    int messageType;

    public ChatModel(String encodedImage, Context context){
        this.context = context;
        imageUploadDialog = new ImageUploadDialog(context);
        this.encodedImage = encodedImage;
    }

    public ChatModel(String senderId, String receiverId, String userEmail, String receiverImageUrl){
        this(senderId,receiverId);
        this.userEmail = userEmail;
        this.receiverImageUrl = receiverImageUrl;
    }

    public ChatModel(String senderId, String receiverId){
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public ChatModel(String senderId, String receiverId, String lastMessage, String senderFirstname, String senderLastname, String receiverFirstname, String receiverLastname, String senderPicture, String receiverPicture,String message, int messageType, String messageImage){
        this(senderId,receiverId);
        this.lastMessage = lastMessage;
        this.senderFirstname = senderFirstname;
        this.senderLastname = senderLastname;
        this.receiverFirstname = receiverFirstname;
        this.receiverLastname = receiverLastname;
        this.senderPicture = senderPicture;
        this.receiverPicture = receiverPicture;
        this.sentMessage = message;
        this.messageType = messageType;
        this.messageImageUrl = messageImage;
    }


    public ChatModel(String senderId, String receiverId, String lastMessage){
        this(senderId,receiverId);
        this.lastMessage = lastMessage;
    }






    public interface ChatHttpImageUploadListener {
        void onImageUpload(String imageUrl);
        void onError(String message);
    }

    public interface ChatGetMessageListener{
        void onMessageReady(ArrayList<ng.assist.UIs.ViewModel.Message> messageArrayList,String nextPageUrl,int total);
        void onError(String message);
    }

    public void setChatGetMessageListener(ChatGetMessageListener chatGetMessageListener) {
        this.chatGetMessageListener = chatGetMessageListener;
    }

    public void setChatHttpImageUploadListener(ChatHttpImageUploadListener chatHttpImageUploadListener) {
        this.chatHttpImageUploadListener = chatHttpImageUploadListener;
    }




    private Handler imageUploadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            imageUploadDialog.cancelDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    uploadedImageUrl = jsonObject.getString("data");
                    chatHttpImageUploadListener.onImageUpload(uploadedImageUrl);
                }
                else{
                    chatHttpImageUploadListener.onError("Error Uploading image please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                chatHttpImageUploadListener.onError("Error Occurred please try again");
            }
        }
    };




    public void uploadImage(){
        imageUploadDialog.showDialog();
        String mResponse = "";
        Runnable runnable = () -> {
            //client
            OkHttpClient okHttpClient = new OkHttpClient();
            //request builder
            Request.Builder builder = new Request.Builder();
            builder.url(uploadImageUrl);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildImageUploadJson(encodedImage));
            RequestBody requestBody1 = ProgressHelper.withProgress(requestBody, new ProgressUIListener() {

                @Override
                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    imageUploadDialog.updateProgress(100 * percent);
                    Log.e(String.valueOf(percent), "onUIProgressChanged: ");

                }

                //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                @Override
                public void onUIProgressFinish() {
                    super.onUIProgressFinish();
                    Log.e("TAG", "onUIProgressFinish:");
                }
            });

            //post the wrapped request body
            builder.post(requestBody1);
            Call call = okHttpClient.newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TAG", "=============onFailure===============");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response != null){
                     String  mResponse =  response.body().string();
                        Message msg = imageUploadHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("response", mResponse);
                        msg.setData(bundle);
                        imageUploadHandler.sendMessage(msg);
                     }
                 }
              });
            };
            Thread myThread = new Thread(runnable);
            myThread.start();
    }



    private Handler getMessagesHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONObject dataInfo = jsonObject.getJSONObject("data");
                    String mNextPageUrl = dataInfo.getString("next_page_url");
                    int total = dataInfo.getInt("total");
                    JSONArray data = dataInfo.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++){
                        String senderId = data.getJSONObject(i).getString("senderId");
                        String receiverId = data.getJSONObject(i).getString("receiverId");
                        String mMessage = data.getJSONObject(i).getString("message");
                        String messageType = data.getJSONObject(i).getString("messageType");
                        String timestamp = data.getJSONObject(i).getString("timestamp");
                        String imageUrl = data.getJSONObject(i).getString("imageUrl");
                        User user;

                        Timestamp timestamp1 = Timestamp.valueOf(timestamp);

                        Date date = addHoursToJavaUtilDate(timestamp1,1);



                        if(ChatModel.this.userEmail.equalsIgnoreCase(senderId)){
                            //you are the receiver
                             user = new User(senderId,"",receiverImageUrl,false);
                        }
                        else{
                            //you are the receiver
                            user = new User("received","",receiverImageUrl,false);
                        }
                        ng.assist.UIs.ViewModel.Message message = new ng.assist.UIs.ViewModel.Message(user, StringEscapeUtils.unescapeJava(mMessage),date);
                        if(!imageUrl.equalsIgnoreCase("null")){
                            message.setImage(new ng.assist.UIs.ViewModel.Message.Image(imageUrl));
                        }
                        messageArrayList.add(message);
                    }
                    chatGetMessageListener.onMessageReady(messageArrayList,mNextPageUrl,total);
                }

                else if(status.equalsIgnoreCase("failure")){
                     chatGetMessageListener.onError("No Message Available");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                    chatGetMessageListener.onError("Error occurred please try again");
            }
        }
    };


    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    public void verifyConnection(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildVerifyConnection(senderId,receiverId));
            Request request = new Request.Builder()
                    .url(verifyConnectionUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = verifyConnectionHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            verifyConnectionHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void getDirectMessagesNextPage(String url){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildGetMessageJson(senderId,receiverId));
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = getMessagesHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            getMessagesHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    public void getDirectMessages(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildGetMessageJson(senderId,receiverId));
            Request request = new Request.Builder()
                    .url(getMessagesUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = getMessagesHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            getMessagesHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private Handler updateCaughtUpHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //created successfully
                }
                else if(status.equalsIgnoreCase("failure")){
                    //error creating message
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    };


    private Handler verifyConnectionHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    createMessage();
                }
                else if(status.equalsIgnoreCase("failure")){
                    addConnection();
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    };


    private Handler addConnectionHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    createMessage();
                }
                else if(status.equalsIgnoreCase("failure")){
                    //error creating message
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    };


    private Handler updateConnectionHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //created successfully
                }
                else if(status.equalsIgnoreCase("failure")){
                    //error creating message
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    };


    private Handler createMessageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    updateConnection();
                }
                else if(status.equalsIgnoreCase("failure")){
                   //error creating message
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    };


    public void updateCaughtUp(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateCaughtUp(senderId,receiverId));
            Request request = new Request.Builder()
                    .url(updateCaughtUpUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = updateCaughtUpHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            updateCaughtUpHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private void updateConnection(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateConnection(senderId,receiverId,lastMessage,"false"));
            Request request = new Request.Builder()
                    .url(updateConnectionUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = updateConnectionHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            updateConnectionHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private void addConnection(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildAddConnection(senderId,receiverId,lastMessage,senderFirstname,senderLastname,receiverFirstname,receiverLastname,senderPicture,receiverPicture));
            Request request = new Request.Builder()
                    .url(addConnectionUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = addConnectionHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            addConnectionHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private void createMessage(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateMessage(this.senderId,this.receiverId,this.sentMessage,this.messageType,this.messageImageUrl));
            Request request = new Request.Builder()
                    .url(createMessageUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = createMessageHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            createMessageHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private String buildImageUploadJson(String encodedImage){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image",encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private String buildCreateMessage(String senderId, String receiverId, String message, int messageType, String imageUrl){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderId",senderId);
            jsonObject.put("receiverId",receiverId);
            jsonObject.put("message",message);
            if(!imageUrl.equalsIgnoreCase("null")){
                jsonObject.put("imageUrl",imageUrl);
            }
            jsonObject.put("messageType",messageType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private String buildGetMessageJson(String senderId, String receiverId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderId",senderId);
            jsonObject.put("receiverId",receiverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildUpdateCaughtUp(String senderId, String receiverId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderId",senderId);
            jsonObject.put("receiverId",receiverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildVerifyConnection(String senderId, String receiverId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderId",senderId);
            jsonObject.put("receiverId",receiverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildAddConnection(String senderId, String receiverId, String lastMessage, String senderFirstname, String senderLastname, String receiverFirstname, String receiverLastname, String senderPicture, String receiverPicture){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderId",senderId);
            jsonObject.put("receiverId",receiverId);
            jsonObject.put("lastMessage",lastMessage);
            jsonObject.put("senderFirstname",senderFirstname);
            jsonObject.put("senderLastname",senderLastname);
            jsonObject.put("receiverFirstname",receiverFirstname);
            jsonObject.put("receiverLastname",receiverLastname);
            jsonObject.put("senderPicture",senderPicture);
            jsonObject.put("receiverPicture",receiverPicture);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildUpdateConnection(String senderId, String receiverId, String lastMessage, String isCaughtUp){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("senderId",senderId);
            jsonObject.put("receiverId",receiverId);
            jsonObject.put("lastMessage",lastMessage);
            jsonObject.put("isCaughtUp",isCaughtUp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


}
