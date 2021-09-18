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

public class EcommerceDashboardModel {
    private String baseUrl = new URL().getBaseUrl();
    private String createRetailerInfoUrl = baseUrl+"retailers";
    private String updateRetailer = baseUrl+"retailers/update";
    private String getRetailerProductUrl = baseUrl+"products/retailer/products";
    private Context context;
    private String  userId = "", shopname = "", phonenumber = "";
    private UpdateInfoListener updateInfoListener;
    private CreateInfoListener createInfoListener;
    private ArrayList<GroceryModel> retailerProducts = new ArrayList<>();
    private ProductsReadyListener productsReadyListener;

    public interface ProductsReadyListener{
        void onReady(ArrayList<GroceryModel> groceryModelArrayList);
        void onError(String message);
    }


    public EcommerceDashboardModel(String userId,String shopname, String phonenumber, Context context){
            this.context = context;
            this.phonenumber = phonenumber;
            this.userId = userId;
            this.shopname = shopname;
     }

     public EcommerceDashboardModel(Context context, String userId){
            this.context = context;
            this.userId = userId;
     }

     public interface UpdateInfoListener{
         void onSuccess();
         void onError(String message);
     }

    public interface CreateInfoListener{
        void onSuccess(String phone, String shopname);
        void onError(String message);
    }


    public void setProductsReadyListener(ProductsReadyListener productsReadyListener) {
        this.productsReadyListener = productsReadyListener;
    }

    public void setCreateInfoListener(CreateInfoListener createInfoListener) {
        this.createInfoListener = createInfoListener;
    }

    public void setUpdateInfoListener(UpdateInfoListener updateInfoListener) {
        this.updateInfoListener = updateInfoListener;
    }


    public void updateRetailerInfo(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateRetailer(userId,phonenumber,shopname));
            Request request = new Request.Builder()
                    .url(updateRetailer)
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


    private Handler retailerProductsHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++){
                        String itemId = data.getJSONObject(i).getString("id");
                        String category = data.getJSONObject(i).getString("category");
                        String name = data.getJSONObject(i).getString("name");
                        String price = data.getJSONObject(i).getString("price");
                        String displayImage   = data.getJSONObject(i).getString("displayImg");
                        String retailerId = data.getJSONObject(i).getString("retailerId");
                        String description = data.getJSONObject(i).getString("description");
                        String shopName = data.getJSONObject(i).getString("shopName");
                        GroceryModel groceryModel = new GroceryModel(itemId,category,name,price,displayImage,retailerId,description,shopName);
                        retailerProducts.add(groceryModel);
                    }
                    productsReadyListener.onReady(retailerProducts);

                }
                else if(status.equalsIgnoreCase("failure")){
                    productsReadyListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                productsReadyListener.onError(e.getLocalizedMessage());
            }
        }
    };



    public void getRetailerProduct(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildShowRetailerProduct(this.userId));
            Request request = new Request.Builder()
                    .url(getRetailerProductUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = retailerProductsHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            retailerProductsHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void createRetailerInfo(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateRetailer(userId));
            Request request = new Request.Builder()
                    .url(createRetailerInfoUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = createInfoHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            createInfoHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private Handler createInfoHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    String phone = jsonArray.getJSONObject(0).getString("phonenumber");
                    String shopName = jsonArray.getJSONObject(0).getString("shopName");
                    createInfoListener.onSuccess(phone,shopName);
                }
                else if(status.equalsIgnoreCase("failure")){
                    createInfoListener.onError("Error Occurred");
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                createInfoListener.onError(e.getLocalizedMessage());

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
                    updateInfoListener.onError("Error Occurred");
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                updateInfoListener.onError(e.getLocalizedMessage());

            }

        }
    };



    private String buildUpdateRetailer(String userId, String phonenumber, String shopname){
        JSONObject jsonObject = new JSONObject();
        try {
                jsonObject.put("phonenumber",phonenumber);
                jsonObject.put("shopName",shopname);
                jsonObject.put("userId",userId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildCreateRetailer(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildShowRetailerProduct(String retailerId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("retailerId",retailerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
