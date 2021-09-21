package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import ng.assist.UIs.Utils.ImageUploadDialog;
import okhttp3.Call;
import okhttp3.Callback;
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
    private String uploadImageUrl = baseUrl+"products/retailer/products/image";
    private String createProductUrl = baseUrl+"products";
    private String displayOrderUrl = baseUrl+"orders/show";
    private Context context;
    private String  userId = "", shopname = "", phonenumber = "";
    private UpdateInfoListener updateInfoListener;
    private CreateInfoListener createInfoListener;
    private ArrayList<GroceryModel> retailerProducts = new ArrayList<>();
    private ProductsReadyListener productsReadyListener;
    private ImageUploadDialog imageUploadDialog;
    private String encodedImage, productId;
    private ImageUploadListener imageUploadListener;
    private String retailerId, title, price, category, description,availability,displayImage;
    private CreateProductListener createProductListener;
    private OrderReadyListener orderReadyListener;
    private ArrayList<Orders> ordersArrayList = new ArrayList<>();

    public interface ProductsReadyListener{
        void onReady(ArrayList<GroceryModel> groceryModelArrayList);
        void onError(String message);
    }

    public interface ImageUploadListener{
        void onUploadSuccessful(ProductImageModel productImageModel);
        void onError(String message);
    }

    public interface OrderReadyListener{
        void onOrderReady(ArrayList<Orders> ordersArrayList);
        void onError(String message);
    }

    public interface CreateProductListener{
         void onSuccess();
         void onError(String message);
    }


    public void setImageUploadListener(ImageUploadListener imageUploadListener) {
        this.imageUploadListener = imageUploadListener;
    }

    public void setOrderReadyListener(OrderReadyListener orderReadyListener) {
        this.orderReadyListener = orderReadyListener;
    }

    public void setCreateProductListener(CreateProductListener createProductListener) {
        this.createProductListener = createProductListener;
    }

    public EcommerceDashboardModel(String userId, String shopname, String phonenumber, Context context){
            this.context = context;
            this.phonenumber = phonenumber;
            this.userId = userId;
            this.shopname = shopname;
     }

     public EcommerceDashboardModel(String productId, String retailerId, String title, String price, String category, String description, String shopname, String availability, String displayImage){
            this.retailerId = retailerId;
            this.title = title;
            this.price = price;
            this.category = category;
            this.description = description;
            this.shopname = shopname;
            this.displayImage = displayImage;
            this.availability = availability;
            this.productId = productId;
    }

    public EcommerceDashboardModel(String encodedImage,String productId, Context context){
        this.context = context;
        imageUploadDialog = new ImageUploadDialog(context);
        this.encodedImage = encodedImage;
        this.productId = productId;
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

    public void displayOrders(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateRetailer(userId));
            Request request = new Request.Builder()
                    .url(displayOrderUrl)
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


    public void createProduct(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateProduct(productId,retailerId,title,price,category,description,shopname,availability,displayImage));
            Request request = new Request.Builder()
                    .url(createProductUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = createProductHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            createProductHandler.sendMessage(msg);
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


    private Handler DisplayOrderHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    for(int i = 0; i < data.length(); i++) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        int orderId = jsonArray.getJSONObject(i).getInt("orderId");
                        String userEmail = jsonArray.getJSONObject(i).getString("email");
                        String orderJson = jsonArray.getJSONObject(i).getString("orderJson");
                        String orderStatus = jsonArray.getJSONObject(i).getString("orderStatus");
                        String totalPrice = jsonArray.getJSONObject(i).getString("totalPrice");
                        String userFirstname = jsonArray.getJSONObject(i).getString("firstname");
                        String userLastname = jsonArray.getJSONObject(i).getString("lastname");
                        Orders order = new Orders(orderId, totalPrice, orderStatus, orderJson, userFirstname, userLastname, userEmail);
                        ordersArrayList.add(order);
                    }
                    orderReadyListener.onOrderReady(ordersArrayList);
                }
                else if(status.equalsIgnoreCase("failure")){
                    orderReadyListener.onError("Error Occurred");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                orderReadyListener.onError(e.getLocalizedMessage());

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

    private Handler createProductHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                     createProductListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                     createProductListener.onError("Error Occurred");
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                createProductListener.onError(e.getLocalizedMessage());

            }

        }
    };


    public void uploadImage(){
        imageUploadDialog.showDialog();
        String mResponse = "";
        Runnable runnable = () -> {
            //client
            OkHttpClient okHttpClient = new OkHttpClient();
            //request builder
            Request.Builder builder = new Request.Builder();
            builder.url(uploadImageUrl);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUploadImage(this.productId,this.encodedImage));
            RequestBody requestBody1 = ProgressHelper.withProgress(requestBody, new ProgressUIListener() {

                @Override
                public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    imageUploadDialog.updateProgress(100 * percent);
                    Log.e(String.valueOf(percent), "onUIProgressChanged: ");

                }

                //if you don't need this method, don't override this methd. It isn't an abstract method, just an empty method.
                @Override
                public void onUIProgressFinish() {
                    super.onUIProgressFinish();
                    Log.e("TAG", "onUIProgressFinish:");
                }
            });

            //post the wrapped request body
            builder.post(requestBody1);
            Call call = okHttpClient.newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TAG", "=============onFailure===============");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response != null){
                        String  mResponse =  response.body().string();
                        Message msg = imageUploadHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("response", mResponse);
                        msg.setData(bundle);
                        imageUploadHandler.sendMessage(msg);
                    }
                }
            });
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private Handler imageUploadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            imageUploadDialog.cancelDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONObject data = jsonObject.getJSONObject("data");
                    int id = data.getInt("id");
                    String imageUrl = data.getString("imageUrl");
                    String productId = data.getString("productId");

                    ProductImageModel productImageModel = new ProductImageModel(id,imageUrl,productId);
                    imageUploadListener.onUploadSuccessful(productImageModel);
                }
                else{
                    imageUploadListener.onError("Error Uploading image please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                imageUploadListener.onError("Error Occurred please try again");
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

    private String buildUploadImage(String productId, String encodedImage){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image",encodedImage);
            jsonObject.put("productId",productId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildCreateProduct(String productId, String retailerId, String title, String price, String category, String description, String shopname, String availability, String displayImage){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("retailerId",retailerId);
            jsonObject.put("name",title);
            jsonObject.put("price",price);
            jsonObject.put("category",category);
            jsonObject.put("description",description);
            jsonObject.put("shopName",shopname);
            jsonObject.put("isAvailable",availability);
            jsonObject.put("displayImg",displayImage);
            jsonObject.put("id",productId);
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

    private String buildShowRetailer(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("retailerId",userId);
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
    public class ProductImageModel{
        private int id;
        private String imageUrl;
        private String productId;

        ProductImageModel(int id, String imageUrl, String productId){
            this.id = id;
            this.imageUrl = imageUrl;
            this.productId = productId;
        }

        public int getId() {
            return id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getProductId() {
            return productId;
        }
    }

    public class Orders{
        private String totalPrice;
        private String status;
        private String orderJson;
        private int orderId;
        private String userFirstname;
        private String userLastname;
        private String userEmail;

        public Orders(int orderId, String totalPrice,String status,String orderJson,String userFirstname, String userLastname,String userEmail){
               this.totalPrice = totalPrice;
               this.status = status;
               this.orderJson = orderJson;
               this.orderId = orderId;
               this.userFirstname = userFirstname;
               this.userLastname = userLastname;
               this.userEmail = userEmail;
        }


        public String getStatus() {
            return status;
        }


        public String getUserFirstname() {
            return userFirstname;
        }

        public int getOrderId() {
            return orderId;
        }

        public String getOrderJson() {
            return orderJson;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public String getUserLastname() {
            return userLastname;
        }

        public String getUserEmail() {
            return userEmail;
        }
    }
}
