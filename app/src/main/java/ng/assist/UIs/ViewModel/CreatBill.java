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
import java.util.concurrent.TimeUnit;

import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatBill {
    private String baseUrl = new URL().getBaseUrl();
    private String createBillUrl = baseUrl+"bills";
    private String payerId,payeeId,type,name;
    private int cost;
    private CreateBillListener createBillListener;
    private LoadingDialogUtils loadingDialogUtils = null;

    public interface CreateBillListener{
        void onSuccess();
        void onError();
    }

    public void setCreateBillListener(CreateBillListener createBillListener) {
        this.createBillListener = createBillListener;
    }

    public CreatBill(String payerId, String payeeId, int cost, String type, String name, Context context, int x){
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.cost = cost;
        this.type = type;
        this.name = name;
        this.createBillUrl = baseUrl+"bills/service/create";
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public CreatBill(String payerId, String payeeId, int cost, String type, String name){
           this.payerId = payerId;
           this.payeeId = payeeId;
           this.cost = cost;
           this.type = type;
           this.name = name;
    }

    public CreatBill(String payerId, String payeeId, int cost, String type, String name,Context context,String y){
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.cost = cost;
        this.type = type;
        this.name = name;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public void CreateBill() {
        if(loadingDialogUtils != null)loadingDialogUtils.showLoadingDialog("booking...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildcreateBill(payerId,payeeId,cost,type,name));
            Request request = new Request.Builder()
                    .url(createBillUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = createBillHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            createBillHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private Handler createBillHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            if(loadingDialogUtils != null)loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("Cart ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    createBillListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                    createBillListener.onError();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                 createBillListener.onError();
            }
        }
    };



    private String buildcreateBill(String payerId, String payeeId, int cost, String type, String name){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("payerId",payerId);
            jsonObject.put("payeeId",payeeId);
            jsonObject.put("mCost",cost);
            jsonObject.put("mType",type);
            jsonObject.put("mName",name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
