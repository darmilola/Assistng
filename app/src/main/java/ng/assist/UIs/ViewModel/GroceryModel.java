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
    private String shopName;
    private String address,contactPhone;
    private int cartIndex;
    private ArrayList<GroceryModel> groceryModelArrayList = new ArrayList<>();
    private String baseUrl = new URL().getBaseUrl();
    private String groceryUrl = baseUrl+"products/list/category";
    private String searchUrl = baseUrl+"products/search";
    private String deleteUrl = baseUrl+"products/delete";
    private String updateCartUrl = baseUrl+"cart/order/update";
    private String deleteFromCartUrl = baseUrl+"cart/remove";
    private String viewCartUrl = baseUrl+"cart/show";
    private String checkoutCartUrl = baseUrl+"cart/checkout";
    private String deleteUsersCartUrl = baseUrl+"cart/delete";
    private String addToCartUrl = baseUrl+"cart";
    private ProductReadyListener productReadyListener;
    private String cartRetailerId, userId, quantity;
    private LoadingDialogUtils dialogUtils;
    private CartListener cartListener;
    private String productQuantity;
    private int qtyPrice;
    private String productId;
    private String orderJson,totalPrice;
    private String searchQuery;
    private CartDisplayListener cartDisplayListener;
    private CartCheckoutListener cartCheckoutListener;
    private deleteProductListener deleteProductListener;


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
        shopName = in.readString();
        groceryModelArrayList = in.createTypedArrayList(GroceryModel.CREATOR);
        baseUrl = in.readString();
        groceryUrl = in.readString();
        updateCartUrl = in.readString();
        deleteFromCartUrl = in.readString();
        viewCartUrl = in.readString();
        addToCartUrl = in.readString();
        cartRetailerId = in.readString();
        userId = in.readString();
        quantity = in.readString();
        productQuantity = in.readString();
        qtyPrice = in.readInt();
        productId = in.readString();
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
        dest.writeString(shopName);
        dest.writeTypedList(groceryModelArrayList);
        dest.writeString(baseUrl);
        dest.writeString(groceryUrl);
        dest.writeString(updateCartUrl);
        dest.writeString(deleteFromCartUrl);
        dest.writeString(viewCartUrl);
        dest.writeString(addToCartUrl);
        dest.writeString(cartRetailerId);
        dest.writeString(userId);
        dest.writeString(quantity);
        dest.writeString(productQuantity);
        dest.writeInt(qtyPrice);
        dest.writeString(productId);
    }

    public interface CartListener{
        void onAdded();
        void onError();
    }

    public interface deleteProductListener{
        void onSuccess();
        void onFailure();
    }


    public interface CartCheckoutListener{
        void onSuccess();
        void onError();
    }



    public interface CartDisplayListener{
        void onCartReady(ArrayList<GroceryModel> groceryModels);
        void onCartError(String message);
    }

    public interface ProductReadyListener{
        void onProductReady(ArrayList<GroceryModel> groceryModels, String nextPageUrl);
        void onError(String message);
    }

    public void setDeleteProductListener(deleteProductListener deleteProductListener) {
        this.deleteProductListener = deleteProductListener;
    }

    public void setCartCheckoutListener(CartCheckoutListener cartCheckoutListener) {
        this.cartCheckoutListener = cartCheckoutListener;
    }

    public GroceryModel(int cartIndex, String quantity, Context context) {
        this.cartIndex = cartIndex;
        this.quantity = quantity;
    }

    public GroceryModel(String cartRetailerId, String userId, String itemId) {
        this.cartRetailerId = cartRetailerId;
        this.userId = userId;
        this.productId = itemId;
    }

    public GroceryModel(String userId) {
        this.userId = userId;
    }

    public GroceryModel(String cartRetailerId, String userId, Context context) {
        this.cartRetailerId = cartRetailerId;
        this.userId = userId;
    }

    public GroceryModel(String city, String query, Context context, boolean isSearch){
           this.userCity = city;
           this.searchQuery = query;
           dialogUtils = new LoadingDialogUtils(context);
    }

    public GroceryModel(String productId, Context context){
           this.productId = productId;
           dialogUtils = new LoadingDialogUtils(context);
    }

    public GroceryModel(String category, String userCity){
        this.category = category;
        this.userCity = userCity;
    }

    public GroceryModel(String itemId, String retailerId, String userId,String quantity,Context context) {
        dialogUtils = new LoadingDialogUtils(context);
        this.itemId = itemId;
        this.userId = userId;
        this.retailerId = retailerId;
        this.quantity = quantity;
        this.cartRetailerId = retailerId;
    }

    public GroceryModel(String retailerId, String userId,String orderJson,String totalPrice,String address,String contactPhone,Context context) {
        dialogUtils = new LoadingDialogUtils(context);
        this.userId = userId;
        this.retailerId = retailerId;
        this.orderJson = orderJson;
        this.totalPrice = totalPrice;
        this.address = address;
        this.contactPhone = contactPhone;
    }



    public GroceryModel(String itemId,String name,String price,String displayImage,String retailerId,String quantity,int cartIndex){
        this.itemId = itemId;
        this.productName = name;
        this.price = price;
        this.displayImage = displayImage;
        this.retailerId = retailerId;
        this.quantity = quantity;
        this.qtyPrice = Integer.parseInt(price) * Integer.parseInt(quantity);
        this.cartIndex = cartIndex;
    }

    public GroceryModel(String itemId, String category, String productName, String price, String displayImage, String retailerId, String description,String shopName){
        this.itemId = itemId;
        this.category = category;
        this.productName = productName;
        this.price = price;
        this.displayImage = displayImage;
        this.retailerId = retailerId;
        this.description = description;
        this.shopName = shopName;
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
                        String shopName = data.getJSONObject(i).getString("shopName");
                        GroceryModel groceryModel = new GroceryModel(itemId,category,name,price,displayImage,retailerId,description,shopName);
                        groceryModelArrayList.add(groceryModel);
                    }
                      productReadyListener.onProductReady(groceryModelArrayList,nextPageUrl);

                }
                else if(status.equalsIgnoreCase("failure")){
                    productReadyListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                productReadyListener.onError(e.getLocalizedMessage());
            }
        }
    };



    private Handler grocerySearchHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //JSONObject dataInfo = jsonObject.getJSONObject("data");
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
                        groceryModelArrayList.add(groceryModel);
                    }
                    nextPageUrl = "";
                    productReadyListener.onProductReady(groceryModelArrayList,nextPageUrl);

                }
                else if(status.equalsIgnoreCase("failure")){
                    productReadyListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                productReadyListener.onError(e.getLocalizedMessage());
            }
        }
    };



    private Handler cartUpdateHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("response  ", response);
             try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){

                }
                else if(status.equalsIgnoreCase("failure")){

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    };

    private Handler checkOutHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("response  ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){

                }
                else if(status.equalsIgnoreCase("failure")){

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    };


    private Handler deleteUsersCartHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            if(dialogUtils != null) dialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("response  ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    if(deleteProductListener != null) deleteProductListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                    if(deleteProductListener != null) deleteProductListener.onFailure();
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
    };


    public String getUserId() {
        return userId;
    }

    private Handler viewCartHandler = new Handler(Looper.getMainLooper()) {
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
                        String itemId = data.getJSONObject(i).getString("productId");
                        String name = data.getJSONObject(i).getString("name");
                        String price = data.getJSONObject(i).getString("price");
                        String displayImage   = data.getJSONObject(i).getString("displayImg");
                        String retailerId = data.getJSONObject(i).getString("cartRetailerId");
                        String quantity = data.getJSONObject(i).getString("quantity");
                        int cartIndex = data.getJSONObject(i).getInt("productCount");
                        GroceryModel groceryModel = new GroceryModel(itemId,name,price,displayImage,retailerId,quantity,cartIndex);
                        groceryModelArrayList.add(groceryModel);
                    }
                    cartDisplayListener.onCartReady(groceryModelArrayList);

                }
                else if(status.equalsIgnoreCase("failure")){
                    cartDisplayListener.onCartError("Nothing in Cart");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                cartDisplayListener.onCartError("Nothing in Cart");
            }
        }
    };



    public void UpdateUsersCart() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateCart(this.cartIndex,this.quantity));
            Request request = new Request.Builder()
                    .url(updateCartUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = cartUpdateHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            cartUpdateHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    public void RemoveFromCart() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildDeleteFromCart(this.cartIndex));
            Request request = new Request.Builder()
                    .url(deleteFromCartUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = cartUpdateHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            cartUpdateHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }




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

    public void searchGroceryProducts() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildSearchCredentials(this.searchQuery,this.userCity));
            Request request = new Request.Builder()
                    .url(searchUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = grocerySearchHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            grocerySearchHandler.sendMessage(msg);
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


    private Handler AddToCartHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            dialogUtils.cancelLoadingDialog();
            String response = bundle.getString("response");
            Log.e("Cart ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    cartListener.onAdded();
                }
                else if(status.equalsIgnoreCase("failure")){
                    cartListener.onError();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                cartListener.onError();
            }
        }
    };

    private Handler cartHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            dialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            dialogUtils.cancelLoadingDialog();
            String response = bundle.getString("response");
            Log.e("Cart ", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                     cartCheckoutListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                   cartCheckoutListener.onError();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                cartCheckoutListener.onError();
            }
        }
    };

    public void viewCart() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildViewCartCredentials(cartRetailerId,userId));
            Request request = new Request.Builder()
                    .url(viewCartUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = viewCartHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            viewCartHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void deleteUsersCart() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildDeleteUsersCart(this.userId));
            Request request = new Request.Builder()
                    .url(deleteUsersCartUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = deleteUsersCartHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            deleteUsersCartHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void deleteProduct() {
        dialogUtils.showLoadingDialog("Deleting product...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildDeleteProduct(this.productId));
            Request request = new Request.Builder()
                    .url(deleteUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                Log.e( "deleteProduct: ",e.getLocalizedMessage());
                e.printStackTrace();
            }
            Message msg = deleteUsersCartHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            deleteUsersCartHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void CheckOut() {
        dialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCartCheckout(retailerId,userId,orderJson,totalPrice,address,contactPhone));
            Request request = new Request.Builder()
                    .url(checkoutCartUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = cartHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            cartHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void addToCart() {
        dialogUtils.showLoadingDialog("Adding to cart");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildAddToCartCredentials(itemId,cartRetailerId,userId,quantity));
            Request request = new Request.Builder()
                    .url(addToCartUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = AddToCartHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            AddToCartHandler.sendMessage(msg);
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

    private String buildSearchCredentials(String query, String city){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("query",query);
            jsonObject.put("city",city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildAddToCartCredentials(String productId, String cartRetailerId, String userId, String quantity){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId",productId);
            jsonObject.put("cartRetailerId",cartRetailerId);
            jsonObject.put("userId",userId);
            jsonObject.put("quantity",quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildCartCheckout(String retailerId, String userId, String orderJson, String totalPrice, String address, String contactPhone){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("retailerId",retailerId);
            jsonObject.put("userId", userId);
            jsonObject.put("orderJson",orderJson);
            jsonObject.put("totalPrice",totalPrice);
            jsonObject.put("deliveryAddress",address);
            jsonObject.put("contactPhone",contactPhone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildViewCartCredentials(String cartRetailerId, String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cartRetailerId",cartRetailerId);
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private String buildUpdateCart(int cartIndex,String quantity){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productCount",cartIndex);
            jsonObject.put("quantity",quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private String buildDeleteFromCart(int cartIndex){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productCount",cartIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildDeleteUsersCart(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildDeleteProduct(String productId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("productId",productId);
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

    public String getShopName() {
        return shopName;
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

    public int getCartIndex() {
        return cartIndex;
    }

    public void setCartHandler(Handler cartHandler) {
        this.cartHandler = cartHandler;
    }

    public void setCartListener(CartListener cartListener) {
        this.cartListener = cartListener;
    }

    public void setCartDisplayListener(CartDisplayListener cartDisplayListener) {
        this.cartDisplayListener = cartDisplayListener;
    }

    public String getQuantity() {
        return quantity;
    }

    public int getQtyPrice() {
        return qtyPrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQtyPrice(int qtyPrice) {
        this.qtyPrice = qtyPrice;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public JSONObject getJsonObject(){
        JSONObject obj = new JSONObject();
        try{
            obj.put("name",productName);
            obj.put("price",price);
            obj.put("imageUrl",displayImage);
            obj.put("quantity",quantity);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return obj;
    }
}
