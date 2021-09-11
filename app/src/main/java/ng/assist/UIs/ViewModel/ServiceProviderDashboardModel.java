package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressUIListener;
import ng.assist.UIs.Utils.ImageUploadDialog;
import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServiceProviderDashboardModel {
    private String mEmail;
    private Context mContext;
    private LoadingDialogUtils loadingDialogUtils;
    private String baseUrl = new URL().getBaseUrl();
    private String showHandymanUrl = baseUrl+"handyman/show";
    private String userId,serviceType,phonenumber,jobTitle,ratePerHour,isAvailable;
    private int totalRaters,totalRatings;
    private Context context;
    private ArrayList<ServiceProviderDashboardModel> serviceProviderDashboardModels = new ArrayList<>();
    private ArrayList<ProviderPortfolio> providerPortfolios = new ArrayList<>();
    private ProviderListener providerListener;
    private String uploadImageUrl = baseUrl+"users/upload/portfolio";
    private String updateHandymanUrl = baseUrl+"handyman/update";
    private ImageUploadDialog imageUploadDialog;
    private String encodedImage;
    private PortfolioUploadListener portfolioUploadListener;
    private UpdateInfoListener updateInfoListener;
    private String jsonKey,value;

    public interface PortfolioUploadListener{
        void onImageUpload(ProviderPortfolio providerPortfolio);
        void onError(String message);
    }

    public interface UpdateInfoListener{
        void onSuccess();
    }

    public interface ProviderListener{
        void onInfoReady(ArrayList<ServiceProviderDashboardModel> serviceProviderDashboardModels, ArrayList<ProviderPortfolio> providerPortfolios);
        void onErrorOccurred(String message);
    }

    public void setPortfolioUploadListener(PortfolioUploadListener portfolioUploadListener) {
        this.portfolioUploadListener = portfolioUploadListener;
    }

    public void setProviderListener(ProviderListener providerListener) {
        this.providerListener = providerListener;
    }

    public void setUpdateInfoListener(UpdateInfoListener updateInfoListener) {
        this.updateInfoListener = updateInfoListener;
    }

    public ServiceProviderDashboardModel(String jsonKey, String value, Context context){
        this.jsonKey = jsonKey;
        this.value = value;
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEmail = preferences.getString("userEmail","");
    }

    public ServiceProviderDashboardModel(String encodedImage, Context context){
           this.context = context;
           imageUploadDialog = new ImageUploadDialog(context);
           this.encodedImage = encodedImage;
           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
           mEmail = preferences.getString("userEmail","");
    }

    public ServiceProviderDashboardModel(Context context){
           this.context = context;
           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
           mEmail = preferences.getString("userEmail","");
    }

    public ServiceProviderDashboardModel(String userId, String serviceType, String phonenumber, String jobTitle, String ratePerHour, int totalRaters, int totalRatings, String isAvailable){
           this.userId = userId;
           this.serviceType = serviceType;
           this.phonenumber = phonenumber;
           this.jobTitle = jobTitle;
           this.ratePerHour = ratePerHour;
           this.totalRaters = totalRaters;
           this.totalRatings = totalRatings;
           this.isAvailable = isAvailable;
    }


    public String getUserId() {
        return userId;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(String ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public int getTotalRaters() {
        return totalRaters;
    }

    public void setTotalRaters(int totalRaters) {
        this.totalRaters = totalRaters;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getIsAvailable() {
        return isAvailable;
    }


    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public class ProviderPortfolio{
        private int id;
        private String userId;
        private String imageUrl;

        public ProviderPortfolio(int id, String userId, String imageUrl){
               this.id = id;
               this.userId = userId;
               this.imageUrl = imageUrl;
        }

        public String getUserId() {
            return userId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public int getId() {
            return id;
        }
    }


    private Handler showProviderHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    JSONArray portfolio = jsonObject.getJSONArray("portfolio");

                    if(portfolio.length() >= 1){

                        for(int j = 0; j < portfolio.length(); j++){
                            String userId = portfolio.getJSONObject(j).getString("userId");
                            int  id = portfolio.getJSONObject(j).getInt("id");
                            String imageUrl = portfolio.getJSONObject(j).getString("imageUrl");

                            ProviderPortfolio providerPortfolio = new ProviderPortfolio(id,userId,imageUrl);
                            providerPortfolios.add(providerPortfolio);
                        }
                    }

                    for(int i = 0; i < data.length(); i++){
                        String userId = data.getJSONObject(i).getString("userId");
                        String type = data.getJSONObject(i).getString("type");
                        String phonenumber = data.getJSONObject(i).getString("phonenumber");
                        String jobTitle = data.getJSONObject(i).getString("jobTitle");
                        String ratePerHour = data.getJSONObject(i).getString("ratePerHour");
                        int totalRaters = data.getJSONObject(i).getInt("totalRaters");
                        int totalRatings = data.getJSONObject(i).getInt("totalRatings");
                        String isAvailable = data.getJSONObject(i).getString("isAvailable");
                        ServiceProviderDashboardModel serviceProviderDashboardModel = new ServiceProviderDashboardModel(userId,type,phonenumber,jobTitle,ratePerHour,totalRaters,totalRatings,isAvailable);
                        serviceProviderDashboardModels.add(serviceProviderDashboardModel);
                    }
                    providerListener.onInfoReady(serviceProviderDashboardModels,providerPortfolios);
                }

                else if(status.equalsIgnoreCase("failure")){
                    providerListener.onErrorOccurred("Error Occurred");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                providerListener.onErrorOccurred(e.getLocalizedMessage());
            }
        }
    };

    public void ShowProvider() {
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildShowProvider(this.mEmail));
            Request request = new Request.Builder()
                    .url(showHandymanUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = showProviderHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            showProviderHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private String buildImageUploadJson(String encodedImage,String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image",encodedImage);
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private String buildShowProvider(String userId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
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
            RequestBody requestBody = RequestBody.create(JSON,buildImageUploadJson(encodedImage,mEmail));
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
                   String userId = data.getString("userId");

                   ProviderPortfolio providerPortfolio = new ProviderPortfolio(id,userId,imageUrl);
                   portfolioUploadListener.onImageUpload(providerPortfolio);
                }
                else{
                    portfolioUploadListener.onError("Error Uploading image please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                portfolioUploadListener.onError("Error Occurred please try again");
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

                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    public void updateProviderInfo(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildUpdateProviderInfo());
            Request request = new Request.Builder()
                    .url(updateHandymanUrl)
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

    private String buildUpdateProviderInfo(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(jsonKey,value);
            jsonObject.put("userId",mEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
