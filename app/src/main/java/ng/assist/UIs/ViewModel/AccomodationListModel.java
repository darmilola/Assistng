package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ng.assist.UIs.Utils.LoadingDialogUtils;
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
    private String status,availabilityStatus;
    private AgentModel agentModel;
    private String nextPageUrl;
    private String totalPage;
    String houseCity;
    private AccomodationListReadyListener accomodationListReadyListener;
    private AccomodationDetailsListener accomodationDetailsListener;
    private String baseUrl = new URL().getBaseUrl();
    private String getAccomodationUrl = baseUrl+"house_listings/filter/by/details";
    private String getDetailsUrl = baseUrl+"house_listing_details/houseInfo";
    private String updateListing = baseUrl+"agent/listings/update";
    private String pendingApproval = baseUrl+"house_listings/approval/pending";
    private String approveHouse = baseUrl+"agent/listings/approve";
    private String rejectHouse = baseUrl+"agent/listings/reject";
    private String rejectedHouse = baseUrl+"agent/listings/rejected";
    private String approvedHouse = baseUrl+"agent/listings/approved";
    private String bookedHouse = baseUrl+"agent/listings/bookings";
    private String showRefunds = baseUrl+"house_listings/admin/refund";
    private String refundUser = baseUrl+"house_listings/bookings/refund";
    private String releaseFund = baseUrl+"house_listings/bookings/fund/release";
    private String accomodationType,location,maxPrice,minPrice,isAvailable,userId;
    int totalListingsAvailable;
    private ArrayList<AccomodationListModel> listModelArrayList = new ArrayList<>();
    private ArrayList<ProductImageModel> imagesList = new ArrayList<>();
    private UpdateListener updateListener;
    private LoadingDialogUtils loadingDialogUtils;
    private String isRunningWater, isNeedRepairs;
    String isRefund,userReason,agentReason,userEvidence,agentEvidence, bookingDate,city;
    int bookingId;

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
        status = in.readString();
        availabilityStatus = in.readString();
        nextPageUrl = in.readString();
        totalPage = in.readString();
        houseCity = in.readString();
        baseUrl = in.readString();
        getAccomodationUrl = in.readString();
        getDetailsUrl = in.readString();
        updateListing = in.readString();
        pendingApproval = in.readString();
        approveHouse = in.readString();
        rejectHouse = in.readString();
        rejectedHouse = in.readString();
        approvedHouse = in.readString();
        bookedHouse = in.readString();
        showRefunds = in.readString();
        refundUser = in.readString();
        releaseFund = in.readString();
        accomodationType = in.readString();
        location = in.readString();
        maxPrice = in.readString();
        minPrice = in.readString();
        isAvailable = in.readString();
        userId = in.readString();
        totalListingsAvailable = in.readInt();
        listModelArrayList = in.createTypedArrayList(AccomodationListModel.CREATOR);
        imagesList = in.createTypedArrayList(ProductImageModel.CREATOR);
        isRefund = in.readString();
        userReason = in.readString();
        agentReason = in.readString();
        userEvidence = in.readString();
        agentEvidence = in.readString();
        bookingDate = in.readString();
        bookingId = in.readInt();
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

    @Override
    public int describeContents() {
        return 0;
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
        dest.writeString(status);
        dest.writeString(availabilityStatus);
        dest.writeString(nextPageUrl);
        dest.writeString(totalPage);
        dest.writeString(houseCity);
        dest.writeString(baseUrl);
        dest.writeString(getAccomodationUrl);
        dest.writeString(getDetailsUrl);
        dest.writeString(updateListing);
        dest.writeString(pendingApproval);
        dest.writeString(approveHouse);
        dest.writeString(rejectHouse);
        dest.writeString(rejectedHouse);
        dest.writeString(approvedHouse);
        dest.writeString(bookedHouse);
        dest.writeString(showRefunds);
        dest.writeString(refundUser);
        dest.writeString(releaseFund);
        dest.writeString(accomodationType);
        dest.writeString(location);
        dest.writeString(maxPrice);
        dest.writeString(minPrice);
        dest.writeString(isAvailable);
        dest.writeString(userId);
        dest.writeInt(totalListingsAvailable);
        dest.writeTypedList(listModelArrayList);
        dest.writeTypedList(imagesList);
        dest.writeString(isRefund);
        dest.writeString(userReason);
        dest.writeString(agentReason);
        dest.writeString(userEvidence);
        dest.writeString(agentEvidence);
        dest.writeString(bookingDate);
        dest.writeInt(bookingId);

    }


    public interface AccomodationListReadyListener{
        void onListReady(ArrayList<AccomodationListModel> listModelArrayList,String nextPageUrl,String totalPage,int totalListingAvailable);
        void onEmpty(String message);
    }

    public interface AccomodationDetailsListener{
        void onDetailsReady(ArrayList<ProductImageModel> imageList, AgentModel agentModel);
        void onError(String message);
    }

    public interface UpdateListener{
        void onUpdateSuccess();
        void onError();
    }


    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public AccomodationListModel(String houseId, String agentId){
        this.houseId = houseId;
        this.agentId = agentId;
    }

    public AccomodationListModel(){

    }

    public AccomodationListModel(String agentId){
           this.agentId = agentId;
    }

    public AccomodationListModel(String houseId, String isAvailable, Context context){
        this.houseId = houseId;
        this.isAvailable = isAvailable;
    }

    public AccomodationListModel(String houseId, Context context){
        this.houseId = houseId;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public AccomodationListModel(String houseId,String agentId, String houseDisplayImage, String houseTitle, String beds, String baths, String totalRaters, String totalRatings, String description, String pricePerMonth,String address, String bookingFee,String isAvailable, String type, String status, String availabilityStatus, String houseCity,String isRunningWater, String isNeedRepairs){
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
        this.houseCity = houseCity;
        this.status = status;
        this.availabilityStatus = availabilityStatus;
        this.isNeedRepairs = isNeedRepairs;
        this.isRunningWater = isRunningWater;
    }


    public AccomodationListModel(String houseId,String agentId, String houseDisplayImage, String houseTitle, String beds, String baths, String totalRaters, String totalRatings, String description, String pricePerMonth,String address, String bookingFee,String isAvailable, String type, String isRefund, String userReason, String agentReason, String userEvidence, String agentEvidence, String bookingDate,int bookingId,String userId, String houseCity, String isRunningWater, String isNeedRepairs){
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
        this.isRefund = isRefund;
        this.userReason = userReason;
        this.agentReason = agentReason;
        this.userEvidence = userEvidence;
        this.agentEvidence = agentEvidence;
        this.bookingDate = bookingDate;
        this.bookingId = bookingId;
        this.userId = userId;
        this.houseCity = houseCity;
        this.isRunningWater = isRunningWater;
        this.isNeedRepairs = isNeedRepairs;
    }

    public AccomodationListModel(String accomodationType, String location, String maxPrice, String minPrice, String city){
           this.accomodationType = accomodationType;
           this.location = location;
           this.maxPrice = maxPrice;
           this.minPrice = minPrice;
           this.city = city;
    }

    public AccomodationListModel(String userId, String amount, int bookingId, String houseId, Context context){
        this.userId = userId;
        this.pricesPerMonth = amount;
        this.bookingId = bookingId;
        this.houseId = houseId;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public AccomodationListModel(String agentId, String amount, int bookingId, Context context){
        this.agentId = agentId;
        this.pricesPerMonth = amount;
        this.bookingId = bookingId;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }


    private Handler accomodationHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("handleMessage: ",response);
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
                        String mStatus = data.getJSONObject(i).getString("status");
                        String houseCity = data.getJSONObject(i).getString("houseCity");
                        String availabilityStatus = data.getJSONObject(i).getString("availabilityStatus");
                        String isRunningWater = data.getJSONObject(i).getString("isRunningWater");
                        String isNeedsRepairs = data.getJSONObject(i).getString("isRepairs");
                        AccomodationListModel accomodationListModel = new AccomodationListModel(houseId,agentId,displayImage,houseTitle,bed,bath,totalRaters,totalRatings,description,pricePerMonth,address,bookingFee,isAvailable,type,mStatus,availabilityStatus,houseCity,isRunningWater,isNeedsRepairs);
                        listModelArrayList.add(accomodationListModel);
                    }
                    accomodationListReadyListener.onListReady(listModelArrayList,nextPageUrl,totalPage,totalListingsAvailable);

                }
                else if(status.equalsIgnoreCase("failure")){
                    accomodationListReadyListener.onEmpty("No Accommodation to Show");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                accomodationListReadyListener.onEmpty(e.getLocalizedMessage());
            }

        }
    };



    private Handler accomodationDetailsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("handleMessage: ",response);
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

    public void ApproveRefund(){
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildAccomodationRefund(userId,pricesPerMonth,bookingId,houseId));
            Request request = new Request.Builder()
                    .url(refundUser)
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
            Log.e( "ApproveRefund: ",mResponse);
            updateInfoHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void ReleaseFund(){
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildAccomodationReleaseFund(agentId,pricesPerMonth,bookingId));
            Request request = new Request.Builder()
                    .url(releaseFund)
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
            Log.e( "releaseRefund: ",mResponse);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    public void updateHouseInfo(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateAccomodation(houseId,isAvailable));
            Request request = new Request.Builder()
                    .url(updateListing)
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


    private Handler updateInfoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            if(loadingDialogUtils != null) loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("handleMessage: ",response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    updateListener.onUpdateSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                    updateListener.onError();
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                updateListener.onError();
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
            RequestBody requestBody = RequestBody.create(JSON,buildSearchCredentials(accomodationType,location,maxPrice,minPrice,city));
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


    public void getAccomodationsRefunds() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,"");
            Request request = new Request.Builder()
                    .url(showRefunds)
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

    public void approveHouse() {
        loadingDialogUtils.showLoadingDialog("Processing");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildApproveHouse(houseId));
            Request request = new Request.Builder()
                    .url(approveHouse)
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

    public void rejectHouse() {
        loadingDialogUtils.showLoadingDialog("Processing");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildApproveHouse(houseId));
            Request request = new Request.Builder()
                    .url(rejectHouse)
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


    public void getAccomodationsPending() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,"");
            Request request = new Request.Builder()
                    .url(pendingApproval)
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

    public void getAccomodationsApproved() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildRejectedorApprove(agentId));
            Request request = new Request.Builder()
                    .url(approvedHouse)
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

    public void getAccomodationsBookings() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildRejectedorApprove(agentId));
            Request request = new Request.Builder()
                    .url(bookedHouse)
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

    public void getAccomodationsRejected() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildRejectedorApprove(agentId));
            Request request = new Request.Builder()
                    .url(rejectedHouse)
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
            RequestBody requestBody = RequestBody.create(JSON,buildSearchCredentials(accomodationType,location,maxPrice,minPrice,city));
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

    public String getHouseCity() {
        return this.houseCity;
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

    public String getIsRunningWater(){
        return isRunningWater;
    }

    public String getIsRepair(){
        return isNeedRepairs;
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

    public String getIsRefund() {
        return isRefund;
    }

    public String getUserReason() {
        return userReason;
    }

    public String getUserEvidence() {
        return userEvidence;
    }

    public String getAgentEvidence() {
        return agentEvidence;
    }

    public String getAgentReason() {
        return agentReason;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public int getBookingId() {
        return bookingId;
    }


    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }


    private String buildSearchCredentials(String type, String location, String max_price, String min_price, String city){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type",type);
            jsonObject.put("location",location);
            jsonObject.put("max_price",max_price);
            jsonObject.put("min_price",min_price);
            jsonObject.put("city",city);
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

    private String buildUpdateAccomodation(String houseId, String isAvailable){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("houseId",houseId);
            jsonObject.put("isAvailable",isAvailable);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildApproveHouse(String houseId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("houseId",houseId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildRejectedorApprove(String agentId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",agentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildAccomodationRefund(String userId, String amount, int bookingId, String houseId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
            jsonObject.put("amount",amount);
            jsonObject.put("id",bookingId);
            jsonObject.put("houseId",houseId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildAccomodationReleaseFund(String agentId, String amount, int bookingId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("agentId",agentId);
            jsonObject.put("amount",amount);
            jsonObject.put("id",bookingId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
