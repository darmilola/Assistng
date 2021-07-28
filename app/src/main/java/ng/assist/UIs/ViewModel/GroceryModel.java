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

public class GroceryModel implements Parcelable {

    private String itemId;
    private String category;
    private String productName;
    private String price;
    private String displayImage;
    private String description;
    private String userCity;
    private String nextPageUrl;
    private String totalPage;
    private String retailerId;
    private ArrayList<GroceryModel> groceryModelArrayList = new ArrayList<>();
    private String baseUrl = new URL().getBaseUrl();
    private String groceryUrl = baseUrl+"products/list/category";
    private ProductReadyListener productReadyListener;

    protected GroceryModel(Parcel in) {
        itemId = in.readString();
        category = in.readString();
        productName = in.readString();
        price = in.readString();
        displayImage = in.readString();
        description = in.readString();
        userCity = in.readString();
        nextPageUrl = in.readString();
        totalPage = in.readString();
        retailerId = in.readString();
        groceryModelArrayList = in.createTypedArrayList(GroceryModel.CREATOR);
        baseUrl = in.readString();
        groceryUrl = in.readString();
    }

    public static final Creator<GroceryModel> CREATOR = new Creator<GroceryModel>() {
        @Override
        public GroceryModel createFromParcel(Parcel in) {
            return new GroceryModel(in);
        }

        @Override
        public GroceryModel[] newArray(int size) {
            return new GroceryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(category);
        dest.writeString(productName);
        dest.writeString(price);
        dest.writeString(displayImage);
        dest.writeString(description);
        dest.writeString(userCity);
        dest.writeString(nextPageUrl);
        dest.writeString(totalPage);
        dest.writeString(retailerId);
        dest.writeTypedList(groceryModelArrayList);
        dest.writeString(baseUrl);
        dest.writeString(groceryUrl);
    }

    public interface ProductReadyListener{
        void onProductReady(ArrayList<GroceryModel> groceryModels, String nextPageUrl);
        void onError(String message);
    }

    public GroceryModel(String category, String userCity){
        this.category = category;
        this.userCity = userCity;
    }
    public GroceryModel(String itemId, String category, String productName, String price, String displayImage, String retailerId, String description){
        this.itemId = itemId;
        this.category = category;
        this.productName = productName;
        this.price = price;
        this.displayImage = displayImage;
        this.retailerId = retailerId;
        this.description = description;
    }

    public void setProductReadyListener(ProductReadyListener productReadyListener) {
        this.productReadyListener = productReadyListener;
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
                    JSONObject dataInfo = jsonObject.getJSONObject("dataInfo");
                    nextPageUrl = dataInfo.getString("next_page_url");
                    totalPage = dataInfo.getString("last_page");
                    JSONArray data = dataInfo.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++){
                        String itemId = data.getJSONObject(i).getString("id");
                        String category = data.getJSONObject(i).getString("category");
                        String name = data.getJSONObject(i).getString("name");
                        String price = data.getJSONObject(i).getString("price");
                        String displayImage   = data.getJSONObject(i).getString("displayImg");
                        String retailerId = data.getJSONObject(i).getString("retailerId");
                        String description = data.getJSONObject(i).getString("description");
                        GroceryModel groceryModel = new GroceryModel(itemId,category,name,price,displayImage,retailerId,description);
                        groceryModelArrayList.add(groceryModel);
                    }
                      productReadyListener.onProductReady(groceryModelArrayList,nextPageUrl);

                }
                else if(status.equalsIgnoreCase("failure")){
                    productReadyListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                productReadyListener.onError("Error Occurred");
            }
        }
    };


    public void getGroceryProducts() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildGroceryCredentials(this.category,this.userCity));
            Request request = new Request.Builder()
                    .url(groceryUrl)
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


    public void getGroceryProductsNextPage(String url) {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildGroceryCredentials(this.category,this.userCity));
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
            Message msg = groceryHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            groceryHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    private String buildGroceryCredentials(String category, String city){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("category",category);
            jsonObject.put("city",city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayImage() {
        return displayImage;
    }

    public String getItemId() {
        return itemId;
    }

    public String getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public String getUserCity() {
        return userCity;
    }

    public ArrayList<GroceryModel> getGroceryModelArrayList() {
        return groceryModelArrayList;
    }

    public Handler getGroceryHandler() {
        return groceryHandler;
    }

    public ProductReadyListener getProductReadyListener() {
        return productReadyListener;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getGroceryUrl() {
        return groceryUrl;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public String getTotalPage() {
        return totalPage;
    }
}
