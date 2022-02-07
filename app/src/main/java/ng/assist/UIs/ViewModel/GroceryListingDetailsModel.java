package ng.assist.UIs.ViewModel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.gson.JsonArray;

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

public class GroceryListingDetailsModel {
    private String productName;
    private String storeName;
    private ArrayList<ProductImageModel> productImages = new ArrayList<>();
    private String productDescription;
    private String productId;
    private String productPrice;
    private String retailerId;
    private ArrayList<GroceryModel> groceryModelArrayList = new ArrayList<>();
    private String baseUrl = new URL().getBaseUrl();
    private String detailsUrl = baseUrl+"product/details";
    RetailerInfoModel retailerInfoModel;
    private DetailsReadyListener detailsReadyListener;

    public interface DetailsReadyListener{
        void onDetailsReady(ArrayList<ProductImageModel> images, ArrayList<GroceryModel> groceryModelArrayList, RetailerInfoModel retailerInfoModel);
        void onError(String message);
    }

    public GroceryListingDetailsModel(String retailerId, String productId){
           this.retailerId = retailerId;
           this.productId = productId;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public String getProductId() {
        return productId;
    }

    private Handler groceryHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){

                    JSONArray images = jsonObject.getJSONArray("images");
                    for(int i = 0; i < images.length(); i++){
                        String imageUrl = images.getJSONObject(i).getString("imageUrl");
                        int id = images.getJSONObject(i).getInt("id");
                        String productId = images.getJSONObject(i).getString("productId");
                        productImages.add(new ProductImageModel(id,imageUrl,productId));
                    }

                    JSONArray products = jsonObject.getJSONArray("retailerProducts");
                    for(int i = 0; i < products.length(); i++){
                        String itemId = products.getJSONObject(i).getString("id");
                        String category = products.getJSONObject(i).getString("category");
                        String name = products.getJSONObject(i).getString("name");
                        String price = products.getJSONObject(i).getString("price");
                        String displayImage   = products.getJSONObject(i).getString("displayImg");
                        String retailerId = products.getJSONObject(i).getString("retailerId");
                        String description = products.getJSONObject(i).getString("description");
                        String shopName = products.getJSONObject(i).getString("shopName");
                        GroceryModel groceryModel = new GroceryModel(itemId,category,name,price,displayImage,retailerId,description,shopName);
                        groceryModelArrayList.add(groceryModel);
                    }

                    JSONArray retailerInfo = jsonObject.getJSONArray("retailerInfo");
                    for(int i = 0; i < retailerInfo.length(); i++){
                        String userId = retailerInfo.getJSONObject(i).getString("userId");
                        String shopName = retailerInfo.getJSONObject(i).getString("shopName");
                        String phonenumber = retailerInfo.getJSONObject(i).getString("phonenumber");
                        retailerInfoModel = new RetailerInfoModel(userId,phonenumber,shopName);
                    }
                    detailsReadyListener.onDetailsReady(productImages,groceryModelArrayList,retailerInfoModel);


                }
                else if(status.equalsIgnoreCase("failure")){
                    detailsReadyListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                detailsReadyListener.onError(e.getLocalizedMessage());
            }
        }
    };


    public void getGroceryDetails() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildGroceryDetail(productId,retailerId));
            Request request = new Request.Builder()
                    .url(detailsUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = groceryHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            groceryHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private String buildGroceryDetail(String productId, String retailerId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId",productId);
            jsonObject.put("retailerId",retailerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public void setDetailsReadyListener(DetailsReadyListener detailsReadyListener) {
        this.detailsReadyListener = detailsReadyListener;
    }

}
