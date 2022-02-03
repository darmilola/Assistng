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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RefundModel {
    private String amount;
    private String status;
    private String userId;
    private Context context;
    private String baseUrl = new URL().getBaseUrl();
    private String showRefundUrl = baseUrl+"refund/user/show";
    private RefundsReadyListener refundsReadyListener;
    private ArrayList<RefundModel> refundModelArrayList = new ArrayList<>();

    public interface RefundsReadyListener{
        void onReady(ArrayList<RefundModel> refundModels);
        void onError(String message);
    }

    public RefundModel(String userId, Context context){
           this.userId = userId;
           this.context = context;
    }

    public RefundModel(String amount, String status){
           this.amount = amount;
           this.status = status;
    }

    public void setRefundsReadyListener(RefundsReadyListener refundsReadyListener) {
        this.refundsReadyListener = refundsReadyListener;
    }

    public void GetUserRefunds(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildRefundJson(this.userId));
            Request request = new Request.Builder()
                    .url(showRefundUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = UserRefundHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            UserRefundHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private Handler UserRefundHandler = new Handler(Looper.getMainLooper()) {
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
                        String refundStatus = data.getJSONObject(i).getString("status");
                        RefundModel refundModel = new RefundModel(amount,refundStatus);
                        refundModelArrayList.add(refundModel);
                    }
                    refundsReadyListener.onReady(refundModelArrayList);
                }
                else{
                    refundsReadyListener.onError("Error Occured");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                refundsReadyListener.onError(e.getLocalizedMessage());
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

    public String getStatus() {
        return status;
    }

    public String getAmount() {
        return amount;
    }
}
