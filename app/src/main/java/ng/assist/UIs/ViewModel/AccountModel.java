package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ng.assist.MainActivity;
import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccountModel {
    private String mEmail;
    private Context mContext;
    private LoadingDialogUtils loadingDialogUtils;
    private String baseUrl = new URL().getBaseUrl();
    private String switchUrl = baseUrl+"users/account/switch";
    private AccountSwitchListener accountSwitchListener;
    private String accountType;

    public interface AccountSwitchListener{
        void onAccountSwitched();
        void onError(String message);
    }

    public void setAccountSwitchListener(AccountSwitchListener accountSwitchListener) {
        this.accountSwitchListener = accountSwitchListener;
    }

    public AccountModel(Context context,String accountType){
        this.mContext = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEmail = preferences.getString("userEmail","");
        this.accountType = accountType;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    private Handler accountSwitchHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    accountSwitchListener.onAccountSwitched();
                }
                else if(status.equalsIgnoreCase("failure")){
                    accountSwitchListener.onError("Error Occured please try again");
                }
                else{
                    accountSwitchListener.onError("Error Occured please try again");

                }
            } catch (JSONException e) {
                e.printStackTrace();
                accountSwitchListener.onError("Error Occured please try again");
            }
        }
    };

    public void SwitchAccount() {
        loadingDialogUtils.showLoadingDialog("Switching Account");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildSwitchAccount(mEmail,accountType));
            Request request = new Request.Builder()
                    .url(switchUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = accountSwitchHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            accountSwitchHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private String buildSwitchAccount(String email,String accountType){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",email);
            jsonObject.put("type",accountType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


}
