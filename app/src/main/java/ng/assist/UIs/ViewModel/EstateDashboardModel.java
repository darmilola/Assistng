package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EstateDashboardModel {

    private String baseUrl = new URL().getBaseUrl();
    private String getListingUrl = baseUrl+"agent/listings";
    private String getDetailsUrl = baseUrl+"house_listing_details/houseInfo";
    private String updateDetailsUrl = baseUrl+"agents/update";
    private String updateAgentListing = baseUrl+"agent/listings/update";
    private String deleteAgentListing = baseUrl+"agent/listings/delete";
    private Context context;
    private String userId;
    private EstateDashboardListener estateDashboardListener;
    private AgentModel agentModel;
    private LoadingDialogUtils loadingDialogUtils;
    private ArrayList<AccomodationListModel> accomodationListModelArrayList = new ArrayList<>();
    private String jsonKey,value,mEmail;
    private String houseId,agentId;
    private String agentHouseId;
    private ArrayList<String> imagesList = new ArrayList<>();
    private UpdateInfoListener updateInfoListener;
    private AccomodationDetailsListener accomodationDetailsListener;

    public EstateDashboardModel(Context context){
           this.context = context;
           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
           userId = preferences.getString("userEmail","");
    }

    public EstateDashboardModel(Context context,String houseId){
        this.context = context;
        this.agentHouseId = houseId;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public EstateDashboardModel(String houseId, String agentId){
         this.houseId = houseId;
         this.agentId = agentId;
    }

    public EstateDashboardModel(String jsonKey, String value, Context context){
        this.jsonKey = jsonKey;
        this.value = value;
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEmail = preferences.getString("userEmail","");
    }

    public EstateDashboardModel(String agentHouseId,String value,int x){
        this.agentHouseId = agentHouseId;
        this.context = context;
        this.value = value;
    }

    public interface AccomodationDetailsListener{
        void onDetailsReady(ArrayList<String> imageList, AgentModel agentModel);
        void onError(String message);
    }

    public interface EstateDashboardListener{
        void onInfoReady(ArrayList<AccomodationListModel> accomodationListModelArrayList, AgentModel agentModel);
        void onError(String message);
    }

    public interface UpdateInfoListener{
        void onSuccess();
        void onError(String message);
    }

    public void setAccomodationDetailsListener(AccomodationDetailsListener accomodationDetailsListener) {
        this.accomodationDetailsListener = accomodationDetailsListener;
    }

    public void setUpdateInfoListener(UpdateInfoListener updateInfoListener) {
        this.updateInfoListener = updateInfoListener;
    }

    public void setEstateDashboardListener(EstateDashboardListener estateDashboardListener) {
        this.estateDashboardListener = estateDashboardListener;
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
                    JSONArray data = jsonObject.getJSONArray("listings");
                    JSONArray agentInfo = jsonObject.getJSONArray("agentInfo");

                    for(int j = 0; j < agentInfo.length(); j++){
                        String userId = agentInfo.getJSONObject(j).getString("userId");
                        String phonenumber = agentInfo.getJSONObject(j).getString("phonenumber");
                        agentModel = new AgentModel(userId,phonenumber);
                    }
                    if(data.length() >= 1) {
                        for (int i = 0; i < data.length(); i++) {
                            String houseId = data.getJSONObject(i).getString("id");
                            String houseTitle = data.getJSONObject(i).getString("houseTitle");
                            String pricePerMonth = data.getJSONObject(i).getString("pricePerMonth");
                            String totalRaters = data.getJSONObject(i).getString("totalRaters");
                            String totalRatings = data.getJSONObject(i).getString("totalRatings");
                            String bed = data.getJSONObject(i).getString("bed");
                            String bath = data.getJSONObject(i).getString("bath");
                            String displayImage = data.getJSONObject(i).getString("displayImg");
                            String description = data.getJSONObject(i).getString("description");
                            String agentId = data.getJSONObject(i).getString("agentId");
                            String address = data.getJSONObject(i).getString("address");
                            String bookingFee = data.getJSONObject(i).getString("bookingFee");
                            String isAvailable = data.getJSONObject(i).getString("isAvailable");
                            AccomodationListModel accomodationListModel = new AccomodationListModel(houseId, agentId, displayImage, houseTitle, bed, bath, totalRaters, totalRatings, description, pricePerMonth, address, bookingFee,isAvailable);
                            accomodationListModelArrayList.add(accomodationListModel);
                        }
                    }
                    estateDashboardListener.onInfoReady(accomodationListModelArrayList,agentModel);
                }
                else if(status.equalsIgnoreCase("failure")){
                    estateDashboardListener.onError("No Accommodation to Show");
                }
            } catch (JSONException e) {
                 e.printStackTrace();
                 estateDashboardListener.onError("Error Occurred");
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
            RequestBody requestBody = RequestBody.create(JSON,buildAgentListModel(userId));
            Request request = new Request.Builder()
                    .url(getListingUrl)
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


    private String buildAgentListModel(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private Handler deleteAgentListingHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            loadingDialogUtils.cancelLoadingDialog();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    updateInfoListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){

                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    private Handler updateInfoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    updateInfoListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                       updateInfoListener.onError("Error Occured");
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                updateInfoListener.onError(e.getLocalizedMessage());
            }

        }
    };


    public void deleteAgentListing(){
        loadingDialogUtils.showLoadingDialog("Deleting Listing");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildDeleteeAgentListing());
            Request request = new Request.Builder()
                    .url(deleteAgentListing)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = deleteAgentListingHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            deleteAgentListingHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void updateProviderInfo(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateAgentInfo());
            Request request = new Request.Builder()
                    .url(updateDetailsUrl)
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

    public void updateAgentListingInfo(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateAgentListing());
            Request request = new Request.Builder()
                    .url(updateAgentListing)
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

    private String buildUpdateAgentInfo(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(jsonKey,value);
            jsonObject.put("userId",mEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildUpdateAgentListing(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("houseId",agentHouseId);
            jsonObject.put("isAvailable",value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildDeleteeAgentListing(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("houseId",agentHouseId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

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
                    String phonenumber = agentJson.getJSONObject(0).getString("phonenumber");

                    agentModel = new AgentModel(agentId,phonenumber);

                    JSONArray images = jsonObject.getJSONArray("images");
                    for(int i = 0; i < images.length(); i++){
                        String imageUrl = images.getJSONObject(i).getString("imageUrl");
                        imagesList.add(imageUrl);
                    }
                    accomodationDetailsListener.onDetailsReady(imagesList,agentModel);
                }
                else if(status.equalsIgnoreCase("failure")){
                    accomodationDetailsListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                accomodationDetailsListener.onError("Error Occurred");
            }

        }
    };

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
