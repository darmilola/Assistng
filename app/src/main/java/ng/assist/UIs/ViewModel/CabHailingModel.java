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

public class CabHailingModel {

    private String driverId;
    private String driverPhone;
    private String carType;
    private String totalPassenger;
    private String baseUrl = new URL().getBaseUrl();
    private String getDriversUrl = baseUrl+"drivers/available/drivers";
    private String driversCity;
    private CabHailingListener cabHailingListener;

    public interface CabHailingListener{
        void onReady(ArrayList<CabHailingModel> cabHailingModelArrayList);
        void onError(String message);
    }

    public CabHailingModel(String driverId,String driverPhone, String carType, String totalPassenger){
        this.driverId = driverId;
        this.driverPhone = driverPhone;
        this.carType = carType;
        this.totalPassenger = totalPassenger;
    }

    public CabHailingModel(String city){
        this.driversCity = city;
    }


    private Handler cabDriversHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    ArrayList<CabHailingModel> cabHailingModelArrayList = new ArrayList<>();
                    for(int i = 0; i < data.length(); i++){
                        String userId = data.getJSONObject(i).getString("userId");
                        String phonenumber = data.getJSONObject(i).getString("phonenumber");
                        String carType   = data.getJSONObject(i).getString("carType");
                        String passenger = data.getJSONObject(i).getString("passenger");
                        CabHailingModel cabHailingModel = new CabHailingModel(userId,phonenumber,carType,passenger);
                        cabHailingModelArrayList.add(cabHailingModel);
                    }
                    cabHailingListener.onReady(cabHailingModelArrayList);


                }
                else if(status.equalsIgnoreCase("failure")){
                  cabHailingListener.onError("No Cab Available at these time ");
                }
                else{
                    cabHailingListener.onError("Error Occurred");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                cabHailingListener.onError("Error Occurred");

            }

        }
    };

   public void SearchCabDrivers(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCabDriversCity(CabHailingModel.this.driversCity));
            Request request = new Request.Builder()
                    .url(getDriversUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = cabDriversHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            cabDriversHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private String buildCabDriversCity(String city){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("city",city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void setCabHailingListener(CabHailingListener cabHailingListener) {
        this.cabHailingListener = cabHailingListener;
    }

    public String getCarType() {
        return carType;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public String getTotalPassenger() {
        return totalPassenger;
    }
}
