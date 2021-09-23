package ng.assist.UIs.ViewModel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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

public class LocationModel {
    private int id;
    private String city;
    private String baseUrl = new URL().getBaseUrl();
    private String loadRetailerLocation = baseUrl+"retailer_location/show";
    private String deleteRetailerLocation = baseUrl+"retailer_location/delete";
    private String createRetailerLocation = baseUrl+"retailer_location";
    private String retailerId;
    private ArrayList<LocationModel> assistLocationList = new ArrayList<>();
    private ArrayList<LocationModel> retailerLocationList = new ArrayList<>();
    private LocationReadyListener locationReadyListener;
    private UpdateInfoListener updateInfoListener;

    public interface LocationReadyListener{
          void onReady(ArrayList<LocationModel> assistLocations, ArrayList<LocationModel> retailerLocation);
          void onError(String message);
    }

    public interface UpdateInfoListener{
        void onSuccess();
        void onError(String message);
    }

    public LocationModel(String retailerId){
           this.retailerId = retailerId;
    }

    public LocationModel(int id){
        this.id = id;
    }

    public LocationModel(String retailerId,String city){
        this.retailerId = retailerId;
        this.city = city;
    }

    public LocationModel(int id, String city){
           this.id = id;
           this.city = city;
    }

    public void setUpdateInfoListener(UpdateInfoListener updateInfoListener) {
        this.updateInfoListener = updateInfoListener;
    }

    public void setLocationReadyListener(LocationReadyListener locationReadyListener) {
        this.locationReadyListener = locationReadyListener;
    }

    public void LoadDeliveryLocations(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildLoadRetailerLocation(this.retailerId));
            Request request = new Request.Builder()
                    .url(loadRetailerLocation)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = loadLocationHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            loadLocationHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private Handler updateInfoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e( "handleMessage:   ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    updateInfoListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                    updateInfoListener.onError("Error Occurred");
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                updateInfoListener.onError(e.getLocalizedMessage());

            }


        }
    };

    public void CreateDeliveryLocations(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateRetailerLocation(this.retailerId,this.city));
            Request request = new Request.Builder()
                    .url(createRetailerLocation)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = updateInfoHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            updateInfoHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void deleteDeliveryLocations(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildDeleteRetailerLocation(this.id));
            Request request = new Request.Builder()
                    .url(deleteRetailerLocation)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = updateInfoHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            updateInfoHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private Handler loadLocationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    JSONArray assist = jsonObject.getJSONArray("assist");
                    for(int i = 0; i < data.length(); i++){
                        int id = data.getJSONObject(i).getInt("id");
                        String city = data.getJSONObject(i).getString("city");
                        LocationModel locationModel = new LocationModel(id,city);
                        retailerLocationList.add(locationModel);
                    }
                    for(int i = 0; i < assist.length(); i++){
                        int id = assist.getJSONObject(i).getInt("id");
                        String city = assist.getJSONObject(i).getString("city");
                        LocationModel locationModel = new LocationModel(id,city);
                        assistLocationList.add(locationModel);
                    }
                    locationReadyListener.onReady(assistLocationList,retailerLocationList);
                }
                else if(status.equalsIgnoreCase("failure")){
                    locationReadyListener.onError("Error Occurred");
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                locationReadyListener.onError(e.getLocalizedMessage());

            }


        }
    };

    public int getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    private String buildLoadRetailerLocation(String retailerId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("retailerId",retailerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildCreateRetailerLocation(String retailerId,String city){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("retailerId",retailerId);
            jsonObject.put("city",city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildDeleteRetailerLocation(int locationId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("locationId",locationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
