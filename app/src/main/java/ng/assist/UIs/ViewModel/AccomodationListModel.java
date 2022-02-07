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

public class AccomodationListModel implements Parcelable {

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
    private String type;
    private AgentModel agentModel;
    private String nextPageUrl;
    private String totalPage;
    private AccomodationListReadyListener accomodationListReadyListener;
    private AccomodationDetailsListener accomodationDetailsListener;
    private String baseUrl = new URL().getBaseUrl();
    private String getAccomodationUrl = baseUrl+"house_listings/filter/by/details";
    private String getDetailsUrl = baseUrl+"house_listing_details/houseInfo";
    private String accomodationType,location,maxPrice,minPrice,isAvailable;
    int totalListingsAvailable;
    private ArrayList<AccomodationListModel> listModelArrayList = new ArrayList<>();
    private ArrayList<ProductImageModel> imagesList = new ArrayList<>();




    public interface AccomodationListReadyListener{
        void onListReady(ArrayList<AccomodationListModel> listModelArrayList,String nextPageUrl,String totalPage,int totalListingAvailable);
        void onEmpty(String message);
    }

    public interface AccomodationDetailsListener{
        void onDetailsReady(ArrayList<ProductImageModel> imageList, AgentModel agentModel);
        void onError(String message);
    }

    public AccomodationListModel(String houseId, String agentId){
        this.houseId = houseId;
        this.agentId = agentId;
    }

    public AccomodationListModel(String houseId,String agentId, String houseDisplayImage, String houseTitle, String beds, String baths, String totalRaters, String totalRatings, String description, String pricePerMonth,String address, String bookingFee,String isAvailable, String type){
        this.houseId = houseId;
        this.agentId = agentId;
        this.houseDisplayImage = houseDisplayImage;
        this.houseTitle = houseTitle;
        this.beds = beds;
        this.baths = baths;
        this.totalRaters = totalRaters;
        this.totalRatings = totalRatings;
        this.pricesPerMonth = pricePerMonth;
        this.houseDesc = description;
        this.address  = address;
        this.bookingFee = bookingFee;
        this.isAvailable = isAvailable;
        this.type = type;
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
                    totalListingsAvailable = dataInfo.getInt("total");
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
                        String agentId = data.getJSONObject(i).getString("agentId");
                        String address = data.getJSONObject(i).getString("address");
                        String bookingFee = data.getJSONObject(i).getString("bookingFee");
                        String isAvailable = data.getJSONObject(i).getString("isAvailable");
                        String type = data.getJSONObject(i).getString("type");
                        AccomodationListModel accomodationListModel = new AccomodationListModel(houseId,agentId,displayImage,houseTitle,bed,bath,totalRaters,totalRatings,description,pricePerMonth,address,bookingFee,isAvailable,type);
                        listModelArrayList.add(accomodationListModel);
                    }
                    accomodationListReadyListener.onListReady(listModelArrayList,nextPageUrl,totalPage,totalListingsAvailable);

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



    private Handler accomodationDetailsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){

                    JSONArray agentJson = jsonObject.getJSONArray("agentInfo");
                    String agentId = agentJson.getJSONObject(0).getString("userId");
                    String firstname = agentJson.getJSONObject(0).getString("firstname");
                    String lastname = agentJson.getJSONObject(0).getString("lastname");
                    String phonenumber = agentJson.getJSONObject(0).getString("phonenumber");
                    String agentImage = agentJson.getJSONObject(0).getString("profileImage");

                    agentModel = new AgentModel(agentId,firstname,lastname,phonenumber,agentImage);

                    JSONArray images = jsonObject.getJSONArray("images");
                    for(int i = 0; i < images.length(); i++){
                        String imageUrl = images.getJSONObject(i).getString("imageUrl");
                        String houseId = images.getJSONObject(i).getString("houseId");
                        int id = images.getJSONObject(i).getInt("id");
                        imagesList.add(new ProductImageModel(id,imageUrl,houseId));
                    }
                     accomodationDetailsListener.onDetailsReady(imagesList,agentModel);
                }
                else if(status.equalsIgnoreCase("failure")){
                     accomodationDetailsListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                accomodationDetailsListener.onError(e.getLocalizedMessage());
            }

        }
    };


    protected AccomodationListModel(Parcel in) {
        houseId = in.readString();
        houseTitle = in.readString();
        beds = in.readString();
        baths = in.readString();
        houseDisplayImage = in.readString();
        houseDesc = in.readString();
        pricesPerMonth = in.readString();
        totalRatings = in.readString();
        totalRaters = in.readString();
        bookingFee = in.readString();
        address = in.readString();
        agentId = in.readString();
        type = in.readString();
        nextPageUrl = in.readString();
        totalPage = in.readString();
        baseUrl = in.readString();
        getAccomodationUrl = in.readString();
        getDetailsUrl = in.readString();
        accomodationType = in.readString();
        location = in.readString();
        maxPrice = in.readString();
        minPrice = in.readString();
        isAvailable = in.readString();
        totalListingsAvailable = in.readInt();
        listModelArrayList = in.createTypedArrayList(AccomodationListModel.CREATOR);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(houseId);
        dest.writeString(houseTitle);
        dest.writeString(beds);
        dest.writeString(baths);
        dest.writeString(houseDisplayImage);
        dest.writeString(houseDesc);
        dest.writeString(pricesPerMonth);
        dest.writeString(totalRatings);
        dest.writeString(totalRaters);
        dest.writeString(bookingFee);
        dest.writeString(address);
        dest.writeString(agentId);
        dest.writeString(type);
        dest.writeString(nextPageUrl);
        dest.writeString(totalPage);
        dest.writeString(baseUrl);
        dest.writeString(getAccomodationUrl);
        dest.writeString(getDetailsUrl);
        dest.writeString(accomodationType);
        dest.writeString(location);
        dest.writeString(maxPrice);
        dest.writeString(minPrice);
        dest.writeString(isAvailable);
        dest.writeInt(totalListingsAvailable);
        dest.writeTypedList(listModelArrayList);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AccomodationListModel> CREATOR = new Creator<AccomodationListModel>() {
        @Override
        public AccomodationListModel createFromParcel(Parcel in) {
            return new AccomodationListModel(in);
        }

        @Override
        public AccomodationListModel[] newArray(int size) {
            return new AccomodationListModel[size];
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

    public void getAccomodationDetails() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildAccomodationDetails(houseId,agentId));
            Request request = new Request.Builder()
                    .url(getDetailsUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = accomodationDetailsHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            accomodationDetailsHandler.sendMessage(msg);
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

    public void setAccomodationDetailsListener(AccomodationDetailsListener accomodationDetailsListener) {
        this.accomodationDetailsListener = accomodationDetailsListener;
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

    public String getIsAvailable() {
        return isAvailable;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
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

    private String buildAccomodationDetails(String houseId, String agentId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("houseId",houseId);
            jsonObject.put("agentId",agentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
