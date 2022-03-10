package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendMoneyModel {
    private String userEmail;
    LoadingDialogUtils loadingDialogUtils;
    private Context context;
    private String baseUrl = new URL().getBaseUrl();
    private String showUserUrl = baseUrl+"users/search/single/user/by/email";
    private String sendMoneyUrl = baseUrl+"bills/transfer/money";
    private ShowUserListener showUserListener;
    private sendMoneyListener sendMoneyListener;
    private String payee,payer,amount;

    public interface ShowUserListener{
        void onSuccessful(RecipientUserInfo recipientUserInfo);
        void onFailure(String message);
    }
    public interface sendMoneyListener{
        void onSuccessful();
        void onFailure(String message);
    }

   public SendMoneyModel(String payee,String payer, String amount, Context context){
        this.payee = payee;
        this.payer = payer;
        this.amount = amount;
        this.context = context;
        loadingDialogUtils = new LoadingDialogUtils(context);
   }

   public SendMoneyModel(String email, Context context){
        loadingDialogUtils = new LoadingDialogUtils(context);
        this.userEmail = email;
        this.context = context;
    }

    public void setShowUserListener(ShowUserListener showUserListener) {
        this.showUserListener = showUserListener;
    }

    public void setSendMoneyListener(SendMoneyModel.sendMoneyListener sendMoneyListener) {
        this.sendMoneyListener = sendMoneyListener;
    }

    private Handler showUserHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    String firstname = jsonObject.getJSONArray("data").getJSONObject(0).getString("firstname");
                    String lastname = jsonObject.getJSONArray("data").getJSONObject(0).getString("lastname");
                    String imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("profileImage");
                    String email = jsonObject.getJSONArray("data").getJSONObject(0).getString("email");
                    RecipientUserInfo recipientUserInfo = new RecipientUserInfo(email,imageUrl,firstname,lastname);
                    showUserListener.onSuccessful(recipientUserInfo);
                }
                else{
                    //failure
                    showUserListener.onFailure("User not found");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showUserListener.onFailure("Error Occurred please try again");
            }
            loadingDialogUtils.cancelLoadingDialog();
        }
    };



    private Handler sendMoneyHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
           // Log.e("response ", response);

            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                     sendMoneyListener.onSuccessful();
                }
                else{
                    //failure
                    sendMoneyListener.onFailure("User not found");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("handleMessage: ", response);
                sendMoneyListener.onFailure(e.getLocalizedMessage());
            }
            loadingDialogUtils.cancelLoadingDialog();
        }
    };


    public void showUser(){
        loadingDialogUtils.showLoadingDialog("Searching User...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildShowUserJson(this.userEmail));
            Request request = new Request.Builder()
                    .url(showUserUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = showUserHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            showUserHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void sendMoneyToUser(){
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildSendMoneyJson(this.payee,this.payer,this.amount));
            Request request = new Request.Builder()
                    .url(sendMoneyUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = sendMoneyHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            sendMoneyHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    private String buildShowUserJson(String email){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildSendMoneyJson(String payee,String payer,String amount){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("payee",payee);
            jsonObject.put("payer",payer);
            jsonObject.put("amount",amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }




}
