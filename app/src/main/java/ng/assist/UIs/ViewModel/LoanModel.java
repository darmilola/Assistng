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

public class LoanModel {
    private String baseUrl = new URL().getBaseUrl();
    private String loanApplyUrl = baseUrl+"products/list/category";
    private String userId;
    private String payback;
    private String amount;
    private String monthlyDue;
    private String accountCode;
    private String status;
    private String accountToken;
    private LoanApplyListener loanApplyListener;
    private LoadingDialogUtils loadingDialogUtils;

    public interface LoanApplyListener{
        void onSuccess();
        void onError(String message);
    }

    public void setLoanApplyListener(LoanApplyListener loanApplyListener) {
        this.loanApplyListener = loanApplyListener;
    }

    public LoanModel(Context context, String userId, String payback, String amount, String monthlyDue, String accountCode, String accountToken, String status){
            this.userId = userId;
            this.payback = payback;
            this.amount = amount;
            this.monthlyDue = monthlyDue;
            this.accountCode = accountCode;
            this.status = status;
            this.accountToken = accountToken;
            loadingDialogUtils = new LoadingDialogUtils(context);
    }

    private String buildLoanApply(String userId,String amount, String payback, String monthlyDue, String accountCode, String status, String accountToken){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
            jsonObject.put("amount",amount);
            jsonObject.put("payback",payback);
            jsonObject.put("monthlyDue",monthlyDue);
            jsonObject.put("accountCode",accountCode);
            jsonObject.put("accountToken",accountToken);
            jsonObject.put("status",status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void Apply(){
        loadingDialogUtils.showLoadingDialog("Creating Application...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildLoanApply(userId,amount,payback,monthlyDue,accountCode,status,accountToken));
            Request request = new Request.Builder()
                    .url(loanApplyUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = loanApplyHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            loanApplyHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private Handler loanApplyHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e( "handleMessage:   ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    loanApplyListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                    loanApplyListener.onError("Error Occurred");
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                loanApplyListener.onError(e.getLocalizedMessage());

            }


        }
    };


}
