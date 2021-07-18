package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

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

public class LoginModel {
    private String mEmail;
    private String mPassword;
    private Context mContext;
    private LoginListener loginListener;
    private LoadingDialogUtils loadingDialogUtils;
    private String baseUrl = new URL().getBaseUrl();
    private String loginUrl = baseUrl+"users/auth";
    private String gmailLoginUrl = baseUrl+"users/auth/gmail/login/credential";

    public interface LoginListener{
        boolean isLoginSuccessful(String email);
        boolean isLoginFailed(String message);
    }

    public LoginModel(String email,String password,Context context){
           this.mEmail = email;
           this.mPassword = password;
           this.mContext = context;
           loadingDialogUtils = new LoadingDialogUtils(mContext);
    }

    public LoginModel(String email,Context context){
        this.mEmail = email;
        this.mContext = context;
        loadingDialogUtils = new LoadingDialogUtils(mContext);
    }

    public void LoginUser(){
       LoginWithCredentials();
    }

    public void loginWithGmail(){
        LoginWithGmailCredentials();
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    private Handler loginHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //mail does not exist continue with registration
                    loginListener.isLoginSuccessful(mEmail);
                }
                else if(status.equalsIgnoreCase("failure")){
                    loginListener.isLoginFailed("Incorrect Email or Password");
                }
                else{
                    loginListener.isLoginFailed("Error occurred please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loginListener.isLoginFailed("Error occurred please try again");
            }

        }
    };



    private Handler gmailLoginHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //mail does not exist continue with registration
                    loginListener.isLoginSuccessful(mEmail);
                }
                else if(status.equalsIgnoreCase("failure")){
                    loginListener.isLoginFailed("Error occurred please try again");
                }
                else{
                    loginListener.isLoginFailed("Error occurred please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loginListener.isLoginFailed("Error occurred please try again");
            }

        }
    };


    private void LoginWithCredentials(){
        loadingDialogUtils.showLoadingDialog("Login In...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildLoginCredentials(mEmail,mPassword));
            Request request = new Request.Builder()
                    .url(loginUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = loginHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            loginHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private void LoginWithGmailCredentials(){
        loadingDialogUtils.showLoadingDialog("Login In...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildGmailLoginCredentials(mEmail));
            Request request = new Request.Builder()
                    .url(gmailLoginUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = gmailLoginHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            gmailLoginHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private String buildLoginCredentials(String email,String password){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",email);
            jsonObject.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildGmailLoginCredentials(String email){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
