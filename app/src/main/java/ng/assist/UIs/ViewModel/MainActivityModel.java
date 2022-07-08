package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ng.assist.MainActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivityModel {

    private String userEmail;
    private String userFirstname;
    private String userLastname;
    private String userImageUrl;
    private String userWalletBalance;
    private String accountType;
    private String isVerified;
    private String role;
    private String verificationType;
    private String verificationStatus;
    private String canApplyForLoan;
    private Context context;
    private MainactivityContentListener mainactivityContentListener;
    private String baseUrl = new URL().getBaseUrl();
    private String getInfoUrl = baseUrl+"users/search/single/user/by/email";

    public interface MainactivityContentListener{
         void onContentReady(MainActivityModel mainActivityModel);
         void onError(String message);
    }

    public String getUserWalletBalance() {
        return userWalletBalance;
    }

    public String getUserFirstname() {
        return userFirstname;
    }

    public MainActivityModel(String userEmail, Context context){
        this.userEmail = userEmail;
        this.context = context;
    }
    public MainActivityModel(String userFirstname, String userWalletBalance,String userLastname,String userImageUrl,String accountType,String isVerified, String role,String verificationStatus){
        this.userFirstname = userFirstname;
        this.userWalletBalance = userWalletBalance;
        this.userLastname = userLastname;
        this.userImageUrl = userImageUrl;
        this.accountType = accountType;
        this.isVerified = isVerified;
        this.role = role;
        this.verificationStatus = verificationStatus;
    }

    public MainActivityModel(String userFirstname, String userWalletBalance,String userLastname,String userImageUrl,String accountType,String isVerified, String role,String verificationStatus, String verificationType){
        this.userFirstname = userFirstname;
        this.userWalletBalance = userWalletBalance;
        this.userLastname = userLastname;
        this.userImageUrl = userImageUrl;
        this.accountType = accountType;
        this.isVerified = isVerified;
        this.role = role;
        this.verificationStatus = verificationStatus;
        this.verificationType = verificationType;
    }

    public MainActivityModel(String userFirstname, String userWalletBalance,String userLastname,String userImageUrl,String accountType,String isVerified, String role,String verificationStatus, String verificationType, String canApplyForLoan){
        this.userFirstname = userFirstname;
        this.userWalletBalance = userWalletBalance;
        this.userLastname = userLastname;
        this.userImageUrl = userImageUrl;
        this.accountType = accountType;
        this.isVerified = isVerified;
        this.role = role;
        this.verificationStatus = verificationStatus;
        this.verificationType = verificationType;
        this.canApplyForLoan = canApplyForLoan;
    }

    public void setMainactivityContentListener(MainactivityContentListener mainactivityContentListener) {
        this.mainactivityContentListener = mainactivityContentListener;
    }

    public void getUserInfo(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUserInfoCredentials(MainActivityModel.this.userEmail));
            Request request = new Request.Builder()
                    .url(getInfoUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            android.os.Message msg = InfoHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            InfoHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    private Handler InfoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                   JSONArray data = jsonObject.getJSONArray("data");
                   String firstname = data.getJSONObject(0).getString("firstname");
                   String walletBalance = data.getJSONObject(0).getString("walletBalance");
                   String lastname = data.getJSONObject(0).getString("lastname");
                   String imageUrl = data.getJSONObject(0).getString("profileImage");
                   String accountType = data.getJSONObject(0).getString("accountType");
                   String isVerified = data.getJSONObject(0).getString("isVerified");
                   String role = data.getJSONObject(0).getString("role");
                   String verificationType = data.getJSONObject(0).getString("verificationType");
                   String verificationStatus = data.getJSONObject(0).getString("verificationStatus");
                   String canApplyForLoan = data.getJSONObject(0).getString("canApplyForLoan");
                   MainActivityModel mainActivityModel = new MainActivityModel(firstname,walletBalance,lastname,imageUrl,accountType,isVerified,role,verificationStatus,verificationType,canApplyForLoan);
                   mainactivityContentListener.onContentReady(mainActivityModel);
                }
                else if(status.equalsIgnoreCase("failure")){
                    getUserInfo();
                }
                else{
                   mainactivityContentListener.onError("Error occurred");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                   mainactivityContentListener.onError("Error occurred");
            }

        }
    };

    private String buildUserInfoCredentials(String email){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getUserLastname() {
        return userLastname;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public String getRole() {
        return role;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public String getCanApplyForLoan() {
        return canApplyForLoan;
    }
}
