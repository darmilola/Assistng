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
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class EstateDashboardModel {

    private String houseId;
    private String houseTitle;
    private int beds;
    private int baths;
    private String location;
    private String houseDisplayImage;
    private String houseDesc;
    private String pricesPerMonth;
    private String totalRatings;
    private String totalRaters;
    private String bookingFee;
    private String address;
    private String type;
    private String agentId;
    private String baseUrl = new URL().getBaseUrl();
    private String getListingUrl = baseUrl+"agent/listings";
    private String getDetailsUrl = baseUrl+"house_listing_details/houseInfo";
    private String updateDetailsUrl = baseUrl+"agents/update";
    private String updateAgentListing = baseUrl+"agent/listings/update";
    private String deleteAgentListing = baseUrl+"agent/listings/delete";
    private String uploadImageUrl = baseUrl+"agent/listings/image";
    private String creatingListingUrl = baseUrl+"house_listings";
    private String rejectedHouse = baseUrl+"agent/listings/rejected";
    private String approvedHouse = baseUrl+"agent/listings/approved";
    private String bookedHouse = baseUrl+"agent/listings/bookings";
    private String userBookings = baseUrl+"house_listings/bookings";
    private String requestRefund = baseUrl+"house_listings/refund/request";
    private String showRefunds = baseUrl+"house_listings/admin/refund";
    private String showPayments = baseUrl+"house_listings/bookings/notrefund";
    private Context context;
    private String userId;
    private int bookingId;
    private EstateDashboardListener estateDashboardListener;
    private AgentModel agentModel;
    private LoadingDialogUtils loadingDialogUtils;
    private ArrayList<AccomodationListModel> accomodationListModelArrayList = new ArrayList<>();
    private String jsonKey,value,mEmail;
    private String isAvailable;
    private String agentHouseId;
    private String encodedImage;
    private String userReason,userEvidence,agentReason,agentEvidence;
    private ImageUploadDialog imageUploadDialog;
    private ArrayList<ProductImageModel> imagesList = new ArrayList<>();
    private UpdateInfoListener updateInfoListener;
    private AccomodationDetailsListener accomodationDetailsListener;
    private HouseUploadListener houseUploadListener;
    private CreateListingListener createListingListener;


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

    public EstateDashboardModel(){}

    public EstateDashboardModel(String userId){
           this.agentId = userId;
    }

    public EstateDashboardModel(String encodedImage,String houseId, Context context, int y){
        this.context = context;
        imageUploadDialog = new ImageUploadDialog(context);
        this.encodedImage = encodedImage;
        this.houseId = houseId;
    }

    public EstateDashboardModel(String houseId, String agentId){
         this.houseId = houseId;
         this.agentId = agentId;
    }

    public EstateDashboardModel(int bookingId,String userReason, String userEvidence, Context context, boolean isUser){
        this.bookingId = bookingId;
        this.context = context;
        this.userReason = userReason;
        this.userEvidence = userEvidence;
        loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public EstateDashboardModel(int bookingId,String agentReason, String agentEvidence, Context context){
        this.bookingId = bookingId;
        this.context = context;
        this.agentReason = agentReason;
        this.agentEvidence = agentEvidence;
        loadingDialogUtils = new LoadingDialogUtils(context);
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

    public EstateDashboardModel(Context context, String houseId, String houseTitle, String pricesPerMonth, String location,String bookingFee, String address, String houseDisplayImage, String houseDesc, String type, String agentId,int beds,int bath){
           this.houseId = houseId;
           this.houseTitle = houseTitle;
           this.pricesPerMonth = pricesPerMonth;
           this.location = location;
           this.bookingFee = bookingFee;
           this.beds = beds;
           this.baths = bath;
           this.address = address;
           this.houseDisplayImage = houseDisplayImage;
           this.type = type;
           this.houseDesc = houseDesc;
           this.isAvailable = isAvailable;
           this.agentId = agentId;
           loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public interface CreateListingListener{
        void onSuccess();
        void onError();

    }
    public interface HouseUploadListener{
        void onUploadSuccessful(HouseImage houseImage);
        void onError(String message);
    }

    public interface AccomodationDetailsListener{
        void onDetailsReady(ArrayList<ProductImageModel> imageList, AgentModel agentModel);
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

    public void setCreateListingListener(CreateListingListener createListingListener) {
        this.createListingListener = createListingListener;
    }

    public void setHouseUploadListener(HouseUploadListener houseUploadListener) {
        this.houseUploadListener = houseUploadListener;
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
            Log.e("handleMessage: ",response);
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
                            String type = data.getJSONObject(i).getString("type");
                            String mStatus = data.getJSONObject(i).getString("status");
                            String availabilityStatus = data.getJSONObject(i).getString("availabilityStatus");
                            AccomodationListModel accomodationListModel = new AccomodationListModel(houseId, agentId, displayImage, houseTitle, bed, bath, totalRaters, totalRatings, description, pricePerMonth, address, bookingFee,isAvailable,type,mStatus,availabilityStatus);
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
                 estateDashboardListener.onError(e.getLocalizedMessage());
            }

        }
    };


    private Handler accomodationBookingHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("handleMessage: ",response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");

                    if(data.length() >= 1) {
                        for (int i = 0; i < data.length(); i++) {
                            int bookingId = data.getJSONObject(i).getInt("bookingId");
                            String userReason = data.getJSONObject(i).getString("userReason");
                            String agentReason = data.getJSONObject(i).getString("agentReason");
                            String userEvidence = data.getJSONObject(i).getString("userEvidence");
                            String agentEvidence = data.getJSONObject(i).getString("agentEvidence");
                            String isRefund = data.getJSONObject(i).getString("isRefund");
                            String bookingDate = data.getJSONObject(i).getString("bookDate");
                            String houseId = data.getJSONObject(i).getString("houseId");
                            String houseTitle = data.getJSONObject(i).getString("houseTitle");
                            String pricePerMonth = data.getJSONObject(i).getString("pricePerMonth");
                            String totalRaters = data.getJSONObject(i).getString("totalRaters");
                            String totalRatings = data.getJSONObject(i).getString("totalRatings");
                            String bed = data.getJSONObject(i).getString("bed");
                            String bath = data.getJSONObject(i).getString("bath");
                            String displayImage = data.getJSONObject(i).getString("displayImg");
                            String description = data.getJSONObject(i).getString("description");
                            String agentId = data.getJSONObject(i).getString("agentId");
                            String userId = data.getJSONObject(i).getString("userId");
                            String address = data.getJSONObject(i).getString("address");
                            String bookingFee = data.getJSONObject(i).getString("bookingFee");
                            String isAvailable = data.getJSONObject(i).getString("isAvailable");
                            String type = data.getJSONObject(i).getString("type");
                            AccomodationListModel accomodationListModel = new AccomodationListModel(houseId, agentId, displayImage, houseTitle, bed, bath, totalRaters, totalRatings, description, pricePerMonth, address, bookingFee,isAvailable,type,isRefund,userReason,agentReason,userEvidence,agentEvidence,bookingDate,bookingId, userId);
                            accomodationListModelArrayList.add(accomodationListModel);
                        }
                    }
                    estateDashboardListener.onInfoReady(accomodationListModelArrayList,new AgentModel("",""));
                }
                else if(status.equalsIgnoreCase("failure")){
                    estateDashboardListener.onError("No Accommodation to Show");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                estateDashboardListener.onError(e.getLocalizedMessage());
            }

        }
    };




    private Handler accomodationPaymentHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("handleMessage: ",response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");

                    if(data.length() >= 1) {
                        for (int i = 0; i < data.length(); i++) {
                            int bookingId = data.getJSONObject(i).getInt("bookingId");
                            String userReason = data.getJSONObject(i).getString("userReason");
                            String agentReason = data.getJSONObject(i).getString("agentReason");
                            String userEvidence = data.getJSONObject(i).getString("userEvidence");
                            String agentEvidence = data.getJSONObject(i).getString("agentEvidence");
                            String isRefund = data.getJSONObject(i).getString("isRefund");
                            String bookingDate = data.getJSONObject(i).getString("bookDate");
                            String houseId = data.getJSONObject(i).getString("houseId");
                            String houseTitle = data.getJSONObject(i).getString("houseTitle");
                            String pricePerMonth = data.getJSONObject(i).getString("pricePerMonth");
                            String totalRaters = data.getJSONObject(i).getString("totalRaters");
                            String totalRatings = data.getJSONObject(i).getString("totalRatings");
                            String bed = data.getJSONObject(i).getString("bed");
                            String bath = data.getJSONObject(i).getString("bath");
                            String displayImage = data.getJSONObject(i).getString("displayImg");
                            String description = data.getJSONObject(i).getString("description");
                            String agentId = data.getJSONObject(i).getString("agentId");
                            String userId = data.getJSONObject(i).getString("userId");
                            String address = data.getJSONObject(i).getString("address");
                            String bookingFee = data.getJSONObject(i).getString("bookingFee");
                            String isAvailable = data.getJSONObject(i).getString("isAvailable");
                            String type = data.getJSONObject(i).getString("type");
                            AccomodationListModel accomodationListModel = new AccomodationListModel(houseId, agentId, displayImage, houseTitle, bed, bath, totalRaters, totalRatings, description, pricePerMonth, address, bookingFee,isAvailable,type,isRefund,userReason,agentReason,userEvidence,agentEvidence,bookingDate,bookingId, userId);
                        //    if(isValidPayment(bookingDate)){
                                accomodationListModelArrayList.add(accomodationListModel);
                          //  }

                        }
                    }
                    estateDashboardListener.onInfoReady(accomodationListModelArrayList,new AgentModel("",""));
                }
                else if(status.equalsIgnoreCase("failure")){
                    estateDashboardListener.onError("No Accommodation to Show");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                estateDashboardListener.onError(e.getLocalizedMessage());
            }

        }
    };


    public boolean isValidPayment(String date){
        boolean isValid;
        String bookDate = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(bookDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 1);  // number of days to add
        String graceDate = sdf.format(c.getTime());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newDate = new Date();
        String currentDate = formatter.format(newDate);

        if(currentDate.compareTo(graceDate) > 0){
           isValid = true;
           return isValid;
        }
        else{
            isValid = false;
            return isValid;
        }

    }


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

    public void requestRefund() {
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildRequestRefund(bookingId,userReason,userEvidence));
            Request request = new Request.Builder()
                    .url(requestRefund)
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


    public void agentRequestRefund() {
        loadingDialogUtils.showLoadingDialog("Processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildAgentRequestRefund(bookingId,agentReason,agentEvidence));
            Request request = new Request.Builder()
                    .url(requestRefund)
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
            Message msg = accomodationBookingHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            accomodationBookingHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void getAccomodationsPayment() {
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
                    .url(showPayments)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = accomodationPaymentHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            accomodationPaymentHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void getAccomodationsUserBookings() {
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
                    .url(userBookings)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = accomodationBookingHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            accomodationBookingHandler.sendMessage(msg);
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
            Message msg = accomodationBookingHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            accomodationBookingHandler.sendMessage(msg);
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
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            Log.e("handleMessage: ", response);
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

    private Handler createListingHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");

           try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    createListingListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                    createListingListener.onError();
                }
                else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
                createListingListener.onError();
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


    public void createAgentListing(){
        loadingDialogUtils.showLoadingDialog("Creating Listing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildCreateHouse(houseId,houseTitle,pricesPerMonth,location,bookingFee,agentId,beds,baths,address,houseDisplayImage,houseDesc,type,isAvailable));
            Request request = new Request.Builder()
                    .url(creatingListingUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = createListingHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            createListingHandler.sendMessage(msg);
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
                    String houseId = data.getString("houseId");

                    HouseImage houseImage = new HouseImage(id,imageUrl,houseId);
                    houseUploadListener.onUploadSuccessful(houseImage);
                }
                else{
                    houseUploadListener.onError("Error Uploading image please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                houseUploadListener.onError("Error Occurred please try again");
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
            RequestBody requestBody = RequestBody.create(JSON,buildUploadImage(encodedImage,houseId));
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
                        imageUploadDialog.cancelDialog();
                    }
                }
            });
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
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

    private String buildUploadImage(String image,String houseId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("houseId",houseId);
            jsonObject.put("image",image);
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

    private String buildCreateHouse(String houseId, String houseTitle, String pricePerMonth, String location, String bookingFee, String agentId, int bed, int bath, String address, String displayImg, String description, String type, String isAvailable){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id",houseId);
            jsonObject.put("houseTitle",houseTitle);
            jsonObject.put("pricePerMonth",pricePerMonth);
            jsonObject.put("location",location);
            jsonObject.put("bookingFee",bookingFee);
            jsonObject.put("agentId",agentId);
            jsonObject.put("address",address);
            jsonObject.put("bed",bed);
            jsonObject.put("bath",bath);
            jsonObject.put("displayImg",displayImg);
            jsonObject.put("description",description);
            jsonObject.put("type",type);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public class HouseImage{
        private int id;
        private String imageUrl;
        private String houseId;

        public HouseImage(int id, String imageUrl, String houseId){
               this.id = id;
               this.imageUrl = imageUrl;
               this.houseId = houseId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getHouseId() {
            return houseId;
        }

        public int getId() {
            return id;
        }
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

    private String buildRequestRefund(int id, String userReason, String userEvidence){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bookingId",id);
            jsonObject.put("userReason",userReason);
            jsonObject.put("userEvidence",userEvidence);
            jsonObject.put("isRefund","true");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildAgentRequestRefund(int id, String agentReason, String agentEvidence){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bookingId",id);
            jsonObject.put("agentReason",agentReason);
            jsonObject.put("agentEvidence",agentEvidence);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


}
