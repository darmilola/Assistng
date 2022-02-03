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

import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WithdrawalModel {
    private int amount;
    private int mAmount;
    private String status;
    private String accountName;
    private String bankName;
    private String accountNumber;
    private String userId;
    private Context context;
    private LoadingDialogUtils loadingDialogUtils;
    private ArrayList<WithdrawalModel> withdrawalModelArrayList = new ArrayList<>();
    private WithdrawListener withdrawListener;
    private String baseUrl = new URL().getBaseUrl();
    private String withdrawUrl = baseUrl+"withdrawal";
    private String withdrawalHistoryUrl = baseUrl+"withdrawal/show";
    private UserWithdrawalListener userWithdrawalListener;

    public interface WithdrawListener{
        void onSuccess();
        void onFailure();
    }

    public interface UserWithdrawalListener{
        void onSuccess(ArrayList<WithdrawalModel> withdrawalModelArrayList);
        void onError(String message);
    }

    public void setWithdrawListener(WithdrawListener withdrawListener) {
        this.withdrawListener = withdrawListener;
    }

    public void setUserWithdrawalListener(UserWithdrawalListener userWithdrawalListener) {
        this.userWithdrawalListener = userWithdrawalListener;
    }

    public WithdrawalModel(int amount, String accountName, String bankName, String accountNumber, String userId, Context context){
           this.amount = amount;
           this.accountName = accountName;
           this.bankName = bankName;
           this.accountNumber = accountNumber;
           this.userId = userId;
           this.context = context;
           loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public WithdrawalModel(String userId){
        this.userId = userId;
    }

    public WithdrawalModel(int amount, String status){
           this.mAmount = amount;
           this.status = status;
    }

    public void makeWithdrawal(){
        loadingDialogUtils.showLoadingDialog("Processing");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildWithdrawlJson(userId,amount,accountName,accountNumber,bankName));
            Request request = new Request.Builder()
                    .url(withdrawUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = withdrawHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            withdrawHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private Handler withdrawHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    withdrawListener.onSuccess();
                }
                else{
                    withdrawListener.onFailure();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                withdrawListener.onFailure();
            }

        }
    };

    public void GetUserWithdrawals(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildRefundJson(this.userId));
            Request request = new Request.Builder()
                    .url(withdrawalHistoryUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = UserWithdrawalHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            UserWithdrawalHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private Handler UserWithdrawalHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                JSONArray data = jsonObject.getJSONArray("data");

                if(status.equalsIgnoreCase("success")) {
                    for (int i = 0; i < data.length(); i++) {
                        int amount = data.getJSONObject(i).getInt("amount");
                        String withdrawalStatus = data.getJSONObject(i).getString("status");
                        WithdrawalModel withdrawalModel = new WithdrawalModel(amount,withdrawalStatus);
                        withdrawalModelArrayList.add(withdrawalModel);
                    }
                    userWithdrawalListener.onSuccess(withdrawalModelArrayList);
                }
                else{
                    userWithdrawalListener.onError("No Withdrawals Made");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                userWithdrawalListener.onError(e.getLocalizedMessage());
            }
        }
    };


    private String buildRefundJson(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }




    private String buildWithdrawlJson(String userId, int amount, String accountName, String accountNumber, String bankName){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
            jsonObject.put("amount",amount);
            jsonObject.put("accountName",accountName);
            jsonObject.put("accountNumber",accountNumber);
            jsonObject.put("bankName",bankName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public int getmAmount() {
        return mAmount;
    }

    public String getStatus() {
        return status;
    }
}
