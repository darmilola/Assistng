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
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProviderBookingsModel {
    private String providerId;
    private String bookingId;
    private String userFirstname;
    private String userLastname;
    private String bookingAmount;
    private String bookingTimestamp;
    private String bookingStatus;
    private String userImageUrl;
    private String userId;
    private String baseUrl = new URL().getBaseUrl();
    private String createBookingUrl = baseUrl+"bookings/create";
    private String updateBookingUrl = baseUrl+"bookings/update";
    private String showAllProviderBookingsUrl = baseUrl+"bookings/all";
    private String showBookingsUrl = baseUrl+"bookings/show";
    private UpdateListener updateListener;
    private ProviderBookingsReadyListener providerBookingsReadyListener;
    private ArrayList<ProviderBookingsModel> providerBookingsModelArrayList = new ArrayList<>();


    public interface UpdateListener{
        void onUpdateSuccess();
        void onError(String message);
    }

    public interface ProviderBookingsReadyListener{
        void onListReady(ArrayList<ProviderBookingsModel> listModelArrayList);
        void onEmpty(String message);
    }


    public void setProviderBookingsReadyListener(ProviderBookingsReadyListener providerBookingsReadyListener) {
        this.providerBookingsReadyListener = providerBookingsReadyListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public ProviderBookingsModel(String providerId, String bookingId, String bookingAmount, String userId){
           this.providerId = providerId;
           this.bookingId = bookingId;
           this.bookingAmount = bookingAmount;
           this.userId = userId;
    }

    public ProviderBookingsModel(String userFirstname, String userLastname,String bookingTimestamp, String userImageUrl, String providerId, String bookingId, String bookingAmount, String userId, String status){
        this.providerId = providerId;
        this.bookingId = bookingId;
        this.bookingAmount = bookingAmount;
        this.userId = userId;
        this.userFirstname = userFirstname;
        this.userLastname = userLastname;
        this.bookingTimestamp = bookingTimestamp;
        this.userImageUrl = userImageUrl;
        this.bookingStatus = status;
    }

    public ProviderBookingsModel(String bookingId, String status){
        this.bookingStatus = status;
        this.bookingId = bookingId;
    }

    public ProviderBookingsModel(String providerId){
        this.providerId = providerId;
    }

    public ProviderBookingsModel(String providerId, String bookingStatus, Context context){
        this.providerId = providerId;
        this.bookingStatus = bookingStatus;
    }


    public String getBookingAmount() {
        return bookingAmount;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public String getBookingTimestamp() {
        return bookingTimestamp;
    }

    public String getCreateBookingUrl() {
        return createBookingUrl;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getUserFirstname() {
        return userFirstname;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getUserLastname() {
        return userLastname;
    }




    private Handler bookingOpsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){

                     updateListener.onUpdateSuccess();
                    }
                else{
                    updateListener.onError("Error Occurred");
                }

                }
            catch (JSONException e) {
                e.printStackTrace();
                updateListener.onError(e.getLocalizedMessage());
            }

        }
    };


    public void updateBookings() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateBookings(bookingId,bookingStatus));
            Request request = new Request.Builder()
                    .url(updateBookingUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = bookingOpsHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            bookingOpsHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void getAllProviderBookings() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildShowAllProviderBookings(providerId));
            Request request = new Request.Builder()
                    .url(showAllProviderBookingsUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = bookingsHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            bookingsHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    public void getProviderBookings() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildShowProviderBookings(providerId,bookingStatus));
            Request request = new Request.Builder()
                    .url(showBookingsUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = bookingsHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            bookingsHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void createBookings() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateBookings(bookingId,userId,providerId,bookingAmount));
            Request request = new Request.Builder()
                    .url(createBookingUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = bookingOpsHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            bookingOpsHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private Handler bookingsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++){
                        String providerId = data.getJSONObject(i).getString("providerId");
                        String userId = data.getJSONObject(i).getString("userId");
                        String bookingId = data.getJSONObject(i).getString("bookingId");
                        String amount = data.getJSONObject(i).getString("amount");
                        String firstname = data.getJSONObject(i).getString("firstname");
                        String lastname = data.getJSONObject(i).getString("lastname");
                        String imageUrl = data.getJSONObject(i).getString("profileImage");
                        String timestamp = data.getJSONObject(i).getString("timestamp");
                        String bookingStatus = data.getJSONObject(i).getString("status");
                        ProviderBookingsModel providerBookingsModel = new ProviderBookingsModel(firstname,lastname,timestamp,imageUrl,providerId,bookingId,amount,userId,bookingStatus);
                        providerBookingsModelArrayList.add(providerBookingsModel);
                    }
                    providerBookingsReadyListener.onListReady(providerBookingsModelArrayList);

                }
                else if(status.equalsIgnoreCase("failure")){
                    providerBookingsReadyListener.onEmpty("No Booking to Show");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                providerBookingsReadyListener.onEmpty("No Booking to Show");
            }

        }
    };


    private String buildCreateBookings(String bookingId, String userId, String providerId, String amount){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bookingId",bookingId);
            jsonObject.put("userId",userId);
            jsonObject.put("providerId",providerId);
            jsonObject.put("amount",amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildUpdateBookings(String bookingId, String bookingStatus){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bookingId",bookingId);
            jsonObject.put("status",bookingStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildShowAllProviderBookings(String providerId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerId",providerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildShowProviderBookings(String providerId, String bookingStatus){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerId",providerId);
            jsonObject.put("bookingStatus",bookingStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}

