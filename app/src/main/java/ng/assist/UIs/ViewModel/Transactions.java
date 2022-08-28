package ng.assist.UIs.ViewModel;

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

public class Transactions {

    private int id;
    private String userId;
    private String type;
    private String timestamp;
    private String amount;
    private String title;
    private String orderId;
    private String isWithdrawPaid;
    private String baseUrl = new URL().getBaseUrl();
    private String create = baseUrl+"transactions/create";
    private String update = baseUrl+"transactions/update";
    private String show = baseUrl+"transactions/user";
    private getTransactionListener getTransactionListener;
    private createTransactionListener createTransactionListener;
    private ArrayList<Transactions> transactions = new ArrayList<>();

    public interface getTransactionListener{
        void isSuccessful(ArrayList<Transactions> transactions);
        void isFailed();
    }

    public interface createTransactionListener{
        void isSuccess();
        void isFailed();
    }

    public Transactions(int id, String userId, String type, String timestamp, String amount, String orderId, String isWithdrawPaid, String title){
            this.id = id;
            this.userId = userId;
            this.timestamp = timestamp;
            this.amount = amount;
            this.orderId = orderId;
            this.isWithdrawPaid = isWithdrawPaid;
            this.type = type;
            this.title = title;
    }

    public Transactions(String userId, String type,String title, String amount, String orderId){
        this.userId = userId;
        this.amount = amount;
        this.orderId = orderId;
        this.type = type;
        this.title = title;
    }

    public Transactions(String userId){
           this.userId = userId;
    }

    public Transactions(int id, String isWithdrawPaid){
           this.id = id;
           this.isWithdrawPaid = isWithdrawPaid;
    }

    public int getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getIsWithdrawPaid() {
        return isWithdrawPaid;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public void setCreateTransactionListener(Transactions.createTransactionListener createTransactionListener) {
        this.createTransactionListener = createTransactionListener;
    }

    public void setGetTransactionListener(Transactions.getTransactionListener getTransactionListener) {
        this.getTransactionListener = getTransactionListener;
    }

    private String buildCreateTransaction(String type, String title, String amount, String orderId, String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type",type);
            jsonObject.put("title",title);
            jsonObject.put("amount",amount);
            jsonObject.put("orderId",orderId);
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildShowUserTransaction(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private Handler showTransactionHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++){
                        int id = jsonArray.getJSONObject(i).getInt("id");
                        String type = jsonArray.getJSONObject(i).getString("type");
                        String title = jsonArray.getJSONObject(i).getString("title");
                        String timestamp = jsonArray.getJSONObject(i).getString("timestamp");
                        String amount = jsonArray.getJSONObject(i).getString("amount");
                        String orderId = jsonArray.getJSONObject(i).getString("orderId");
                        String isWithdrawPaid = jsonArray.getJSONObject(i).getString("isWithdrawPaid");
                        String userId = jsonArray.getJSONObject(i).getString("userId");
                        Transactions tr = new Transactions(id,userId,type,timestamp,amount,orderId,isWithdrawPaid,title);
                        transactions.add(tr);
                    }
                    getTransactionListener.isSuccessful(transactions);
                }
                else if(status.equalsIgnoreCase("failure")){
                    getTransactionListener.isFailed();
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                getTransactionListener.isFailed();

            }

        }
    };

    public void showTransactions(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildShowUserTransaction(userId));
            Request request = new Request.Builder()
                    .url(show)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = showTransactionHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            showTransactionHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    public void createTransactions(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateTransaction(type,title,amount,orderId,userId));
            Request request = new Request.Builder()
                    .url(create)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = createTransactionHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            createTransactionHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private Handler createTransactionHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                   // createTransactionListener.isSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                   // createTransactionListener.isFailed();
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
               // createTransactionListener.isFailed();

            }

        }
    };

}
