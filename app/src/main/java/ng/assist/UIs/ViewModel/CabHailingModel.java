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

import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CabHailingModel {
    private String driverId;
    private String driverPhone;
    private String carType;
    private String totalPassenger;
    private String baseUrl = new URL().getBaseUrl();
    private String getBusesUrl = baseUrl+"drivers/buses";
    private String getDriversUrl = baseUrl+"drivers/available/drivers";
    private String displayAssistLocation = baseUrl+"retailer_location/assist";
    private String bookTransport = baseUrl+"drivers/book";
    private String driversCity;
    private CabHailingListener cabHailingListener;
    private LocationReadyListener locationReadyListener;
    private TransportBookingListener transportBookingListener;
    private ArrayList<LocationModel> assistLocationList = new ArrayList<>();
    private String from,to;
    private int transportId,seats;
    private String type,phone,fare,userId,contactPhone,route,meetingpoint,company,fromArea,toArea,departureTime,departureDate;
    private int cost;
    private LoadingDialogUtils loadingDialogUtils;
    private Context context;

    public interface CabHailingListener{
        void onReady(ArrayList<CabHailingModel> cabHailingModelArrayList);
        void onError(String message);
    }

    public interface TransportBookingListener{
        void onSuccess();
        void onFailure(String message);
    }

    public interface LocationReadyListener{
        void onReady(ArrayList<LocationModel> retailerLocation);
        void onError(String message);
    }

    public void setLocationReadyListener(LocationReadyListener locationReadyListener) {
        this.locationReadyListener = locationReadyListener;
    }

    public void setTransportBookingListener(TransportBookingListener transportBookingListener) {
        this.transportBookingListener = transportBookingListener;
    }

    public CabHailingModel(String driverId, String driverPhone, String carType, String totalPassenger){
        this.driverId = driverId;
        this.driverPhone = driverPhone;
        this.carType = carType;
        this.totalPassenger = totalPassenger;
    }

    public CabHailingModel(int id, String from, String to, String phone, String fare, int seats, String type, String meetingpoint, String company,String fromArea, String toArea, String departureTime,String departureDate){
           this.transportId = id;
           this.from = from;
           this.to = to;
           this.type = type;
           this.seats = seats;
           this.fare = fare;
           this.phone = phone;
           this.meetingpoint = meetingpoint;
           this.company = company;
           this.departureDate = departureDate;
           this.departureTime = departureTime;
           this.fromArea = fromArea;
           this.toArea = toArea;
    }

    public CabHailingModel(int transportId, String userId, String contactPhone,String type,String route, int cost,  Context context,String fromArea, String toArea, String departureTime,String departureDate, String meetingpoint){
           this.transportId = transportId;
           this.userId = userId;
           this.contactPhone = contactPhone;
           this.context = context;
           this.type = type;
           this.cost = cost;
           this.route = route;
           this.departureDate = departureDate;
           this.departureTime = departureTime;
           this.fromArea = fromArea;
           this.toArea = toArea;
           this.meetingpoint = meetingpoint;
           loadingDialogUtils = new LoadingDialogUtils(context);
    }

    public CabHailingModel(String city){
        this.driversCity = city;
    }

    public CabHailingModel(String from, String to){
          this.from = from;
          this.to = to;
    }

    public CabHailingModel(){
    }



    private Handler transportBookingHnadler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){

                   transportBookingListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                    transportBookingListener.onFailure("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                transportBookingListener.onFailure(e.getLocalizedMessage());
            }
        }
    };



    private Handler loadLocationHandler = new Handler(Looper.getMainLooper()) {
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
                        int id = data.getJSONObject(i).getInt("id");
                        String city = data.getJSONObject(i).getString("city");
                        LocationModel locationModel = new LocationModel(id,city);
                        assistLocationList.add(locationModel);
                    }
                    locationReadyListener.onReady(assistLocationList);
                }
                else if(status.equalsIgnoreCase("failure")){
                    locationReadyListener.onError("Error Occurred");
                }

            } catch (JSONException e) {
                e.printStackTrace();
                locationReadyListener.onError(e.getLocalizedMessage());

            }


        }
    };


    private Handler cabDriversHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    JSONArray data = jsonObject.getJSONArray("data");
                    ArrayList<CabHailingModel> cabHailingModelArrayList = new ArrayList<>();
                    for(int i = 0; i < data.length(); i++){
                        int id = data.getJSONObject(i).getInt("id");
                        String type = data.getJSONObject(i).getString("mType");
                        String from  = data.getJSONObject(i).getString("mFrom");
                        String to = data.getJSONObject(i).getString("mTo");
                        int seats = data.getJSONObject(i).getInt("seats");
                        String phone = data.getJSONObject(i).getString("phone");
                        String fare = data.getJSONObject(i).getString("fare");
                        String meetingPoint = data.getJSONObject(i).getString("meetingpoint");
                        String company = data.getJSONObject(i).getString("company");
                        String fromArea = data.getJSONObject(i).getString("fromArea");
                        String toArea = data.getJSONObject(i).getString("toArea");
                        String departureTime = data.getJSONObject(i).getString("departureTime");
                        String departureDate = data.getJSONObject(i).getString("travelDate");
                        CabHailingModel cabHailingModel = new CabHailingModel(id,from,to,phone,fare,seats,type,meetingPoint,company,fromArea,toArea,departureTime,departureDate);
                        cabHailingModelArrayList.add(cabHailingModel);
                    }
                    cabHailingListener.onReady(cabHailingModelArrayList);


                }
                else if(status.equalsIgnoreCase("failure")){
                  cabHailingListener.onError("No Cab Available at these time ");
                }
                else{
                    cabHailingListener.onError("Error Occurred");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                cabHailingListener.onError(e.getLocalizedMessage());

            }

        }
    };



   public void SearchTransports(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildBusesLocation(this.from,this.to));
            Request request = new Request.Builder()
                    .url(getBusesUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = cabDriversHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            cabDriversHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    public void DisplayAssistLocation(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildAssistLocation());
            Request request = new Request.Builder()
                    .url(displayAssistLocation)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = loadLocationHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            loadLocationHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    public void BookTransport(){
       loadingDialogUtils.showLoadingDialog("processing...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildBookTransport(transportId,userId,contactPhone,cost,type,route,this.fromArea,this.toArea,this.departureDate,this.departureTime,this.meetingpoint));
            Request request = new Request.Builder()
                    .url(bookTransport)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = transportBookingHnadler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            transportBookingHnadler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private String buildCabDriversCity(String city){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("city",city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildAssistLocation(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("city","city");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildBookTransport(int transportId, String userId, String contactPhone, int cost, String type, String route, String from, String to, String date, String time, String meetingPoint){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("transportId",transportId);
            jsonObject.put("userId",userId);
            jsonObject.put("contactPhone",contactPhone);
            jsonObject.put("mCost",cost);
            jsonObject.put("mType",type);
            jsonObject.put("mRoute",route);
            jsonObject.put("from",from);
            jsonObject.put("to",to);
            jsonObject.put("date",date);
            jsonObject.put("time",time);
            jsonObject.put("meetingpoint",meetingPoint);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildBusesLocation(String from, String to){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("from",from);
            jsonObject.put("to",to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void setCabHailingListener(CabHailingListener cabHailingListener) {
        this.cabHailingListener = cabHailingListener;
    }

    public String getCarType() {
        return carType;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverPhone() {
        return phone;
    }

    public String getTotalPassenger() {
        return totalPassenger;
    }

    public int getSeats() {
        return seats;
    }

    public int getTransportId() {
        return transportId;
    }

    public String getFare() {
        return fare;
    }

    public String getFrom() {
        return from;
    }

    public String getMeetingpoint() {
        return meetingpoint;
    }

    public String getCompany() {
        return company;
    }

    public String getTo() {
        return to;
    }

    public String getType() {
        return type;
    }

    public String getPhone() {
        return phone;
    }

    public String getFromArea() {
        return fromArea;
    }

    public String getToArea() {
        return toArea;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }
}
