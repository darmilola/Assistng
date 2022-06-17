package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

public class TopUpModel {
    private String userId;
    LoadingDialogUtils loadingDialogUtils;
    Context context;
    private String baseUrl = new URL().getBaseUrl();
    private String topUpUrl = baseUrl+"topup";
    private TopUpListener topUpListener;
    private String morePaymentUrl = "https://api.paystack.co/transaction/initialize";
    private int amount;

    public interface TopUpListener{
        void onTopUpSucessful(String balance);
        void onFailure(String message);
    }

    public TopUpModel(String userId,int amount,Context context){
        this.userId = userId;
        this.amount = amount;
        this.context = context;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public void setTopUpListener(TopUpListener topUpListener) {
        this.topUpListener = topUpListener;
    }

    private Handler topUpUserHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    String balance = jsonObject.getString("data");
                    topUpListener.onTopUpSucessful(balance);
                }
                else{
                    //failure
                    topUpListener.onFailure("Error Occurred please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                topUpListener.onFailure("Error Occurred please try again");
            }
            loadingDialogUtils.cancelLoadingDialog();
        }
    };


    private Handler MoreTopUpHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                Boolean status = jsonObject.getBoolean("status");
                if(status){
                    String authUrl = jsonObject.getJSONObject("data").getString("authorization_url");
                    topUpListener.onTopUpSucessful(authUrl);
                }
                else{
                    //failure
                    topUpListener.onFailure("Error Occurred please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                topUpListener.onFailure("Error Occurred please try again");
            }
            loadingDialogUtils.cancelLoadingDialog();
        }
    };



    public void topUpBalance(){
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildTopUpJson(userId,amount));
            Request request = new Request.Builder()
                    .url(topUpUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = topUpUserHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            topUpUserHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    public void GetMoreTopUpMethods(){
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildMorePaymentJson(userId,amount*100));
            Request request = new Request.Builder()
                    .url(morePaymentUrl)
                    .post(requestBody)
                    .addHeader("Authorization","Bearer sk_test_e99d7b021f0213195ec330135a4ee62333993097")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = MoreTopUpHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            MoreTopUpHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private String buildTopUpJson(String id, int amount){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",id);
            jsonObject.put("amount",amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildMorePaymentJson(String userId, int amount){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",userId);
            jsonObject.put("amount",Integer.toString(amount));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
