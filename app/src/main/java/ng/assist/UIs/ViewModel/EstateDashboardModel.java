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
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EstateDashboardModel {

    private String baseUrl = new URL().getBaseUrl();
    private String getListingUrl = baseUrl+"agent/listings";
    private String getDetailsUrl = baseUrl+"house_listing_details/houseInfo";
    private Context context;
    private String userId;

    public EstateDashboardModel(Context context){
           this.context = context;
           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
           userId = preferences.getString("userEmail","");
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
                        AccomodationListModel accomodationListModel = new AccomodationListModel(houseId,agentId,displayImage,houseTitle,bed,bath,totalRaters,totalRatings,description,pricePerMonth,address,bookingFee);
                       // listModelArrayList.add(accomodationListModel);
                    }
                    //accomodationListReadyListener.onListReady(listModelArrayList,nextPageUrl,totalPage);

                }
                else if(status.equalsIgnoreCase("failure")){
                    //accomodationListReadyListener.onEmpty("No Accommodation to Show");
                }

            } catch (JSONException e) {
                e.printStackTrace();
               // accomodationListReadyListener.onEmpty("Error Occurred");
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


}
