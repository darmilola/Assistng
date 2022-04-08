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

public class LoanHistoryModel {
    LoadingDialogUtils loadingDialogUtils;
    private String amount;
    private String status;
    private String userId;
    private String date;
    private Context context;
    private String baseUrl = new URL().getBaseUrl();
    private String showLoanUrl = baseUrl+"loan/history/show";
    private String createLoanUrl = baseUrl+"loan/history/create";
    private LoanHistoryReadyListener loanHistoryReadyListener;
    private ArrayList<LoanHistoryModel> loanHistoryModelArrayList = new ArrayList<>();
    private createLoanListener createLoanListener;

    public interface LoanHistoryReadyListener{
        void onReady(ArrayList<LoanHistoryModel> loanHistoryModels);
        void onError(String message);
    }

    public interface createLoanListener{
        void onSuccessful();
        void onFailure(String message);
    }

    public void setCreateLoanListener(LoanHistoryModel.createLoanListener createLoanListener) {
        this.createLoanListener = createLoanListener;
    }

    public LoanHistoryModel(String userId, Context context){
        this.userId = userId;
        this.context = context;
    }

    public LoanHistoryModel(String amount, String status, String date){
        this.amount = amount;
        this.status = status;
        this.date = date;
    }

    public LoanHistoryModel(String userId, String amount){
        this.amount = amount;
        this.userId = userId;
    }

    public void setLoanHistoryReadyListener(LoanHistoryReadyListener loanHistoryReadyListener) {
        this.loanHistoryReadyListener = loanHistoryReadyListener;
    }

    public void GetLoanHistory(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildRefundJson(this.userId));
            Request request = new Request.Builder()
                    .url(showLoanUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            android.os.Message msg = LoanHistoryHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            LoanHistoryHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private Handler CreateLoanHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                JSONArray data = jsonObject.getJSONArray("data");

                if(status.equalsIgnoreCase("success")) {
                    createLoanListener.onSuccessful();
                }
                else{
                    createLoanListener.onFailure("Error Occurred");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                createLoanListener.onFailure(e.getLocalizedMessage());
            }
        }
    };

    private Handler LoanHistoryHandler = new Handler(Looper.getMainLooper()) {
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
                        String amount = data.getJSONObject(i).getString("amount");
                        String loanStatus = data.getJSONObject(i).getString("status");
                        String date = data.getJSONObject(i).getString("date");
                        LoanHistoryModel loanHistoryModel = new LoanHistoryModel(amount,loanStatus,date);
                        loanHistoryModelArrayList.add(loanHistoryModel);
                    }
                    loanHistoryReadyListener.onReady(loanHistoryModelArrayList);
                }
                else{
                    loanHistoryReadyListener.onError("Error Occured");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loanHistoryReadyListener.onError(e.getLocalizedMessage());
            }
        }
    };

    public void createLoan(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateLoanJson(this.amount,this.userId));
            Request request = new Request.Builder()
                    .url(createLoanUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = CreateLoanHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            CreateLoanHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private String buildRefundJson(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    private String buildCreateLoanJson(String amount,String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("amount",amount);
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
