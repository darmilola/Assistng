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
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServicesModel {

    private static  int REQUEST_TYPE_SERVICES = 1;
    private String handymanFirstname;
    private String handymanLastname;
    private String handymanRatings;
    private String handymanJobs;
    private String ratePerHour;
    private String handymanImage;
    private String handymanId;
    private String handymanPhone;
    private int requestType;
    private String serviceType;
    private ArrayList<ServicesModel> servicesModelArrayList = new ArrayList<>();
    private String baseUrl = new URL().getBaseUrl();
    private String homeServicesUrl = baseUrl+"users/auth";
    private ServiceProviderListener serviceProviderListener;


    public interface ServiceProviderListener{
        void onProvidersReadyListener(ArrayList<ServicesModel> servicesModelArrayList);
        void onError(String message);
    }

    public ServicesModel(String handymanFirstname, String handymanLastname, String handymanRatings, String handymanJobs, String ratePerHour, String handymanImage, String handymanId, String handymanPhone){
         this.handymanFirstname = handymanFirstname;
         this.handymanLastname = handymanLastname;
         this.handymanRatings = handymanRatings;
         this.handymanJobs = handymanJobs;
         this.ratePerHour = ratePerHour;
         this.handymanImage = handymanImage;
         this.handymanId = handymanId;
         this.handymanPhone = handymanPhone;
    }

    public ServicesModel(String serviceType, int requestType){
        this.serviceType = serviceType;
        this.requestType = requestType;
    }


    public void setServiceProviderListener(ServiceProviderListener serviceProviderListener) {
        this.serviceProviderListener = serviceProviderListener;
    }

    public String getHandymanFirstname() {
        return handymanFirstname;
    }

    public String getHandymanJobs() {
        return handymanJobs;
    }

    public String getHandymanLastname() {
        return handymanLastname;
    }

    public String getHandymanRatings() {
        return handymanRatings;
    }

    public String getRatePerHour() {
        return ratePerHour;
    }

    public String getHandymanId() {
        return handymanId;
    }

    public String getHandymanImage() {
        return handymanImage;
    }


    private Handler servicesHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONObject dataInfo = jsonObject.getJSONObject("dataInfo");
                    JSONArray data = dataInfo.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++){
                        String firstname = data.getJSONObject(i).getString("firstname");
                        String lastname = data.getJSONObject(i).getString("lastname");
                        String totalRatings = data.getJSONObject(i).getString("totalRatings");
                        String totalRaters = data.getJSONObject(i).getString("totalRaters");
                        String jobsCount   = data.getJSONObject(i).getString("jobsCount");
                        String userId = data.getJSONObject(i).getString("userId");
                        String mRatePerHour = data.getJSONObject(i).getString("ratePerHour");
                        String phonenumber = data.getJSONObject(i).getString("phonenumber");
                        String displayImage = data.getJSONObject(i).getString("profileImage");
                        ServicesModel servicesModel = new ServicesModel(firstname,lastname,totalRatings,jobsCount,mRatePerHour,displayImage,userId,phonenumber);
                        servicesModelArrayList.add(servicesModel);
                    }
                    serviceProviderListener.onProvidersReadyListener(servicesModelArrayList);


                }
                else if(status.equalsIgnoreCase("failure")){
                    serviceProviderListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                serviceProviderListener.onError("Error Occurred");
            }

        }
    };

    private String buildServicesCredentials(String requestType){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type",requestType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void getServiceProvider(String url) {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildServicesCredentials(this.serviceType));
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = accomodationHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            accomodationHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


}
