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

public class ProviderDetailsModel {

    private String userId;
    private ArrayList<ProductImageModel> imagesList = new ArrayList<>();
    private String baseUrl = new URL().getBaseUrl();
    private String imagesUrl = baseUrl+"handyman_portfolio/userId";
    private ImagesReadyListener imagesReadyListener;
    public ProviderDetailsModel(String userId){
        this.userId = userId;
    }

    public interface ImagesReadyListener{
        void onImageReady(ArrayList<ProductImageModel> imagesList);
        void onError(String message);
    }

    private Handler imagesHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull android.os.Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++){
                        String imageUrl = data.getJSONObject(i).getString("imageUrl");
                        String userId = data.getJSONObject(i).getString("userId");
                        int id = data.getJSONObject(i).getInt("id");
                         imagesList.add(new ProductImageModel(id,imageUrl,userId));
                    }
                    imagesReadyListener.onImageReady(imagesList);
                }
                else if(status.equalsIgnoreCase("failure")){
                    imagesReadyListener.onError("No Portfolio to display");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                imagesReadyListener.onError("Error Occurred");
            }

        }
    };

    public void setImagesReadyListener(ImagesReadyListener imagesReadyListener) {
        this.imagesReadyListener = imagesReadyListener;
    }

    private String buildImagesCredentials(String mUserId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",mUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void getProviderPortfolio() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildImagesCredentials(this.userId));
            Request request = new Request.Builder()
                    .url(imagesUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = imagesHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            imagesHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }
}
