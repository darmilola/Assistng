package ng.assist.UIs.ViewModel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

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

public class ServicesModel implements Parcelable {
    private String handymanFirstname;
    private String handymanLastname;
    private String handymanRatings;
    private String handymanJobs;
    private String ratePerHour;
    private String handymanImage;
    private String handymanId;
    private String handymanPhone;
    private String serviceType;
    private String nextPageUrl;
    private String totalPage;
    private String jobTitle;
    private String city;
    private ArrayList<ServicesModel> servicesModelArrayList = new ArrayList<>();
    private String baseUrl = new URL().getBaseUrl();
    private String servicesUrl = baseUrl+"handyman/category/type";
    private ServiceProviderListener serviceProviderListener;

    protected ServicesModel(Parcel in) {
        handymanFirstname = in.readString();
        handymanLastname = in.readString();
        handymanRatings = in.readString();
        handymanJobs = in.readString();
        ratePerHour = in.readString();
        handymanImage = in.readString();
        handymanId = in.readString();
        handymanPhone = in.readString();
        serviceType = in.readString();
        nextPageUrl = in.readString();
        totalPage = in.readString();
        jobTitle = in.readString();
        servicesModelArrayList = in.createTypedArrayList(ServicesModel.CREATOR);
        baseUrl = in.readString();
        servicesUrl = in.readString();
    }

    public static final Creator<ServicesModel> CREATOR = new Creator<ServicesModel>() {
        @Override
        public ServicesModel createFromParcel(Parcel in) {
            return new ServicesModel(in);
        }

        @Override
        public ServicesModel[] newArray(int size) {
            return new ServicesModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(handymanFirstname);
        dest.writeString(handymanLastname);
        dest.writeString(handymanRatings);
        dest.writeString(handymanJobs);
        dest.writeString(ratePerHour);
        dest.writeString(handymanImage);
        dest.writeString(handymanId);
        dest.writeString(handymanPhone);
        dest.writeString(serviceType);
        dest.writeString(nextPageUrl);
        dest.writeString(totalPage);
        dest.writeString(jobTitle);
        dest.writeTypedList(servicesModelArrayList);
        dest.writeString(baseUrl);
        dest.writeString(servicesUrl);
    }


    public interface ServiceProviderListener{
        void onProvidersReadyListener(ArrayList<ServicesModel> servicesModelArrayList,String nextPageUrl,String totalPage);
        void onError(String message);
    }

    public ServicesModel(String handymanFirstname, String handymanLastname, String handymanRatings, String handymanJobs, String ratePerHour, String handymanImage, String handymanId, String handymanPhone, String jobTitle){
         this.handymanFirstname = handymanFirstname;
         this.handymanLastname = handymanLastname;
         this.handymanRatings = handymanRatings;
         this.handymanJobs = handymanJobs;
         this.ratePerHour = ratePerHour;
         this.handymanImage = handymanImage;
         this.handymanId = handymanId;
         this.handymanPhone = handymanPhone;
         this.jobTitle = jobTitle;
    }

    public ServicesModel(String serviceType, String city){
        this.serviceType = serviceType;
        this.city = city;
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

    public String getHandymanPhone() {
        return handymanPhone;
    }

    public String getJobTitle() {
        return jobTitle;
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
                    nextPageUrl = dataInfo.getString("next_page_url");
                    totalPage = dataInfo.getString("last_page");
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
                        String jobTitle = data.getJSONObject(i).getString("jobTitle");
                        String displayImage = data.getJSONObject(i).getString("profileImage");
                        ServicesModel servicesModel = new ServicesModel(firstname,lastname,totalRatings,jobsCount,mRatePerHour,displayImage,userId,phonenumber,jobTitle);
                        servicesModelArrayList.add(servicesModel);
                    }
                    serviceProviderListener.onProvidersReadyListener(servicesModelArrayList,nextPageUrl,totalPage);


                }
                else if(status.equalsIgnoreCase("failure")){
                    serviceProviderListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                serviceProviderListener.onError(e.getLocalizedMessage());
            }

        }
    };

    private String buildServicesCredentials(String requestType, String city){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type",requestType);
            jsonObject.put("city",city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void getServiceProvider() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildServicesCredentials(this.serviceType,this.city));
            Request request = new Request.Builder()
                    .url(servicesUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = servicesHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            servicesHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void getServiceProviderNextPage(String url) {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildServicesCredentials(this.serviceType,this.city));
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
            Message msg = servicesHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            servicesHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


}
