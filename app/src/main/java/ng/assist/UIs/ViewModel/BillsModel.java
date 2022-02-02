package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ng.assist.AccomodationBooking;
import ng.assist.Bills;
import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BillsModel {
    private String baseUrl = new URL().getBaseUrl();
    private String payTransportBillUrl = baseUrl+"bills/transport/release";
    private String payOtherBillsUrl = baseUrl+"bills/release";
    private String requestRefund = baseUrl+"bills/refund";
    private String showBillsUrl = baseUrl+"bills/show";
    private String payerId;
    private String payeeId;
    private int cost;
    private String type;
    private String route;
    private int bookingId;
    private String name;
    private int billId;
    private String userId;
    private String reason;
    private LoadingDialogUtils loadingDialogUtils = null;
    private ShowBillListener showBillListener;
    private BillsActionLitener billsActionLitener;

    public interface ShowBillListener{
        void onSuccess(ArrayList<BillsModel> billsModelArrayList);
        void onEmpty();
        void onError(String message);
    }

    public interface BillsActionLitener{
        void onSuccess();
        void onError();
    }

    public BillsModel(int billId, String payerId, String payeeId, int cost, String type, String route, int bookingId, String name){
           this.payerId = payerId;
           this.payeeId = payeeId;
           this.cost = cost;
           this.type = type;
           this.route = route;
           this.bookingId = bookingId;
           this.name = name;
           this.billId = billId;
    }

    public BillsModel(int billId, int bookingId,Context context){
          this.billId = billId;
          this.bookingId = bookingId;
          loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public BillsModel(String payeeId, String payerId, int cost, String reason,int billId,Context context){
        this.payeeId = payeeId;
        this.payerId = payerId;
        this.cost = cost;
        this.reason = reason;
        this.billId = billId;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public BillsModel(int billId, String payeeId,int cost,Context context){
        this.billId = billId;
        this.payeeId = payeeId;
        this.cost = cost;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public BillsModel(Context context){
          userId = PreferenceManager.getDefaultSharedPreferences(context).getString("userEmail","null");
    }

    public void setBillsActionLitener(BillsActionLitener billsActionLitener) {
        this.billsActionLitener = billsActionLitener;
    }

    public void setShowBillListener(ShowBillListener showBillListener) {
        this.showBillListener = showBillListener;
    }

    public void ShowBill() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, BuildShowBills(userId));
            Request request = new Request.Builder()
                    .url(showBillsUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = showBillsHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            showBillsHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void payTransportBill() {
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, buidPayTransportBill(bookingId,billId));
            Request request = new Request.Builder()
                    .url(payTransportBillUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = payTransportHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            payTransportHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void RequestRefund() {
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,BuildRefund(payerId,payeeId,cost,reason,billId));
            Request request = new Request.Builder()
                    .url(requestRefund)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = requestRefundHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            requestRefundHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    public void payOtherBill() {
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, buidPayOtherBill(billId,payeeId,cost));
            Request request = new Request.Builder()
                    .url(payOtherBillsUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = payTransportHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            payTransportHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    private Handler showBillsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("Cart ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    ArrayList<BillsModel> billsModelArrayList = new ArrayList<>();
                    for(int i = 0; i < data.length(); i++){
                        int id = data.getJSONObject(i).getInt("billId");
                        String payeeId = data.getJSONObject(i).getString("payeeId");
                        String payerId  = data.getJSONObject(i).getString("payerId");
                        String mType = data.getJSONObject(i).getString("mType");
                        String mRoute = data.getJSONObject(i).getString("mRoute");
                        int bookingId = data.getJSONObject(i).getInt("bookingId");
                        int mCost = data.getJSONObject(i).getInt("mCost");
                        String mName = data.getJSONObject(i).getString("mName");
                        BillsModel billsModel = new BillsModel(id,payerId,payeeId,mCost,mType,mRoute,bookingId,mName);
                        billsModelArrayList.add(billsModel);
                    }
                    showBillListener.onSuccess(billsModelArrayList);

                }
                else if(status.equalsIgnoreCase("failure")){
                    showBillListener.onEmpty();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showBillListener.onError(e.getLocalizedMessage());
            }
        }
    };


    private Handler payTransportHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("Cart ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                   billsActionLitener.onSuccess();

                }
                else if(status.equalsIgnoreCase("failure")){
                    billsActionLitener.onError();
                }
            } catch (JSONException e) {
                billsActionLitener.onError();
                e.printStackTrace();
            }
        }
    };

    private Handler payOtherBillHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("Cart ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                  billsActionLitener.onSuccess();

                }
                else if(status.equalsIgnoreCase("failure")){
                    billsActionLitener.onError();
                }
            } catch (JSONException e) {
                billsActionLitener.onError();
                e.printStackTrace();
            }
        }
    };



    private Handler requestRefundHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("handleMessage: ",response.toString() );
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                     billsActionLitener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                     billsActionLitener.onError();
                }
            } catch (JSONException e) {
                     billsActionLitener.onError();
                Log.e("ErrorHandleMessage: ", e.getLocalizedMessage());

            }
        }
    };



    private String buidPayTransportBill(int bookingId, int billId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("booking_id",bookingId);
            jsonObject.put("billId",billId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buidPayOtherBill(int billId, String payeeId, int amount){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("payeeId",payeeId);
            jsonObject.put("billId",billId);
            jsonObject.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String BuildShowBills(String payerId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("payerId",payerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String BuildRefund(String payerId, String payeeId, int cost, String reason,int billId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",payerId);
            jsonObject.put("providerId",payeeId);
            jsonObject.put("amount",cost);
            jsonObject.put("reason",reason);
            jsonObject.put("billId",billId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public String getPayerId() {
        return payerId;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public int getBillId() {
        return billId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getCost() {
        return cost;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public String getRoute() {
        return route;
    }
}
