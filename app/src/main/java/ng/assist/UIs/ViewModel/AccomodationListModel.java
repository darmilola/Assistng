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

public class AccomodationListModel {

    private String houseId;
    private String houseTitle;
    private String beds;
    private String baths;
    private String houseDisplayImage;
    private String houseDesc;
    private String pricesPerMonth;
    private String totalRatings;
    private String totalRaters;
    private String bookingFee;
    private String address;
    private String agentId;
    private AgentModel agentModel;
    private String nextPageUrl;
    private String totalPage;
    private int viewType = 0;
    private AccomodationListReadyListener accomodationListReadyListener;
    private String baseUrl = new URL().getBaseUrl();
    private String getAccomodationUrl = baseUrl+"house_listings/filter/by/details";
    private String accomodationType,location,maxPrice,minPrice;
    private ArrayList<AccomodationListModel> listModelArrayList = new ArrayList<>();

    public interface AccomodationListReadyListener{
        void onListReady(ArrayList<AccomodationListModel> listModelArrayList,String nextPageUrl,String totalPage);
        void onEmpty(String message);
    }

    public AccomodationListModel(String houseId, String houseDisplayImage, String houseTitle, String beds, String baths, String totalRaters, String totalRatings, String description, String pricePerMonth){
        this.houseId = houseId;
        this.houseDisplayImage = houseDisplayImage;
        this.houseTitle = houseTitle;
        this.beds = beds;
        this.baths = baths;
        this.totalRaters = totalRaters;
        this.totalRatings = totalRatings;
        this.pricesPerMonth = pricePerMonth;
        this.houseDesc = description;
    }
    public AccomodationListModel(int viewType){
        this.viewType = viewType;
    }

    public AccomodationListModel(String accomodationType, String location, String maxPrice, String minPrice){
           this.accomodationType = accomodationType;
           this.location = location;
           this.maxPrice = maxPrice;
           this.minPrice = minPrice;
    }


    private Handler accomodationHandler = new Handler(Looper.getMainLooper()) {
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
                        String houseId = data.getJSONObject(i).getString("id");
                        String houseTitle = data.getJSONObject(i).getString("houseTitle");
                        String pricePerMonth = data.getJSONObject(i).getString("pricePerMonth");
                        String totalRaters = data.getJSONObject(i).getString("totalRaters");
                        String totalRatings   = data.getJSONObject(i).getString("totalRatings");
                        String bed = data.getJSONObject(i).getString("bed");
                        String bath = data.getJSONObject(i).getString("bath");
                        String displayImage = data.getJSONObject(i).getString("displayImg");
                        String description = data.getJSONObject(i).getString("description");
                        AccomodationListModel accomodationListModel = new AccomodationListModel(houseId,displayImage,houseTitle,bed,bath,totalRaters,totalRatings,description,pricePerMonth);
                        listModelArrayList.add(accomodationListModel);
                    }
                    accomodationListReadyListener.onListReady(listModelArrayList,nextPageUrl,totalPage);

                }
                else if(status.equalsIgnoreCase("failure")){
                    accomodationListReadyListener.onEmpty("No Accommodation to Show");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                accomodationListReadyListener.onEmpty("Error Occurred");
            }

        }
    };



    public void getAccomodations() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildSearchCredentials(accomodationType,location,maxPrice,minPrice));
            Request request = new Request.Builder()
                    .url(getAccomodationUrl)
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


    public void getAccomodationsNextPage(String url) {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildSearchCredentials(accomodationType,location,maxPrice,minPrice));
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



    public void setAccomodationListReadyListener(AccomodationListReadyListener accomodationListReadyListener) {
        this.accomodationListReadyListener = accomodationListReadyListener;
    }

    public String getBaths() {
        return baths;
    }

    public String getBeds() {
        return beds;
    }

    public String getHouseDesc() {
        return houseDesc;
    }

    public String getHouseId() {
        return houseId;
    }

    public String getHouseDisplayImage() {
        return houseDisplayImage;
    }

    public String getHouseTitle() {
        return houseTitle;
    }

    public AgentModel getAgentModel() {
        return agentModel;
    }

    public String getAddress() {
        return address;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getBookingFee() {
        return bookingFee;
    }

    public String getPricesPerMonth() {
        return pricesPerMonth;
    }

    public String getTotalRaters() {
        return totalRaters;
    }

    public String getTotalRatings() {
        return totalRatings;
    }


    private String buildSearchCredentials(String type, String location, String max_price, String min_price){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type",type);
            jsonObject.put("location",location);
            jsonObject.put("max_price",max_price);
            jsonObject.put("min_price",min_price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
