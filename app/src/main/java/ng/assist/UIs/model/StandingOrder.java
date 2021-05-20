package ng.assist.UIs.model;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ng.assist.UIs.Utils.DialogUtils;
import ng.assist.UIs.Utils.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StandingOrder {

    private  String urlInMotion = "https://remitademo.net/remita/exapp/api/v1/send/api/echannelsvc/echannel/mandate/setup";
    private  String validateAuthorizationUrl = "https://remitademo.net/remita/exapp/api/v1/send/api/echannelsvc/echannel/mandate/validateAuthorization";
    private  String requestAuthorizationUrl = "https://remitademo.net/remita/exapp/api/v1/send/api/echannelsvc/echannel/mandate/requestAuthorization";
    private static final String MANDATE_TYPE = "SO";
    private static final String FREQUENCY = "Month";
    private static final String MERCHANT_ID = "27768931";
    private static final String SERVICE_TYPE_ID = "35126630";
    private static final String API_KEY = "Q1dHREVNTzEyMzR8Q1dHREVNTw==";
    private static final String SALT = "f1nd1ngn3m0farm1990M0O";
    private static final int MAX_NUMBER_OF_DEBITS = 1;
    private String hash512;
    private String payerName;
    private String payerEmail;
    private String payerPhone;
    private String payerBankCode;
    private String payerAccount;
    private String requestId;
    private String amount;
    private String startDate;
    private String endDate;
    private StandingOrderGenerationListener standingOrderGenerationListener;
    private DialogUtils dialogUtils;
    protected Context context;
    private ArrayList<RemitaBank> remitaBanks;
    private String timeStamp;

    public  interface StandingOrderGenerationListener {

        public void onInitialGenerationCompleted(String statusCode, String requestId, String mandateId, String status);

        public void onGenerationFailure(String errorMessage);

        public void onRequestAuthorizationCompleted(String message);

        public void onAuthorizationValidated(String message);

        public void onAuthorizationValidationError(String errorMessage);

        public void onMandatePrintReady(String message);

    }

    public StandingOrder(Context context, String mPayerName, String mPayerEmail, String mPayerPhone, String mPayerBankCode, String mPayerAccount, String mAmount, String mStartDate, String mEndDate) {
        this.payerName = mPayerName;
        this.payerEmail = mPayerEmail;
        this.payerPhone = mPayerPhone;
        this.payerBankCode = mPayerBankCode;
        this.requestId = GenerateUniqueRequestId();
        this.amount = mAmount;
        this.startDate = mStartDate;
        this.endDate = mEndDate;
        this.payerAccount = mPayerAccount;
        this.hash512 = GenerateHash512(this.requestId, this.amount);
        this.context = context;
        RemitaBank remitaBank =  new RemitaBank();
        remitaBank.populateBanks();
        this.remitaBanks = remitaBank.getRemitaBanks();
        timeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        Log.e("date ",timeStamp);
    }


    private String GenerateHash512(String requestId, String amount) {
        String generatedHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            String stringToHash = StandingOrder.MERCHANT_ID + StandingOrder.SERVICE_TYPE_ID + requestId + amount + StandingOrder.API_KEY;
            byte[] bytes = md.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedHash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedHash;
    }



    public void setStandingOrderGenerationListener(StandingOrderGenerationListener standingOrderGenerationListener) {
        this.standingOrderGenerationListener = standingOrderGenerationListener;
    }

    public void ProcessStandingOrder() {
        new StandingOrderTask().execute();
    }

    public void StartRequestValidation(){

    }

    public void StartMandateRequestAuthorization(){

    }


    public class StandingOrderRequest {

        public String send(String remitaUrl) {

            MediaType JSON = MediaType.parse("application/json");
            try {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(50, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(50, TimeUnit.SECONDS)
                        .build();
                String jsonfiedInfo = JsonfyRequestData();
                RequestBody formBody = RequestBody.create(JSON, jsonfiedInfo);
                Request request = new Request.Builder().method("POST",formBody).addHeader("Content-Type", "application/json").url(remitaUrl).build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class RequestAuthRequest {

        public String send(String requestUrl, String mandateId, String requestId, String timeStamp) {
            try {

                OkHttpClient client = new OkHttpClient().newBuilder().build();
                MediaType mediaType = MediaType.parse("application/json");
                String jsonfiedArgs = jsonfyAuthRequest(mandateId,requestId);
                RequestBody body = RequestBody.create(mediaType,jsonfiedArgs);
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("MERCHANT_ID", MERCHANT_ID)
                        .addHeader("API_KEY", API_KEY)
                        .addHeader("REQUEST_ID", requestId)
                        .addHeader("REQUEST_TS", timeStamp)
                        .addHeader("API_DETAILS_HASH",StandingOrder.this.hash512)
                        .build();
                Response response = client.newCall(request).execute();
                return response. body().string();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class PrintMandateRequest{

        public String send(String mandateId, String requestId, String hash_mandate_view, String merchantId) {
            Log.e("The Url is ", "https://remitademo.net/remita/exapp/api/v1/send/api/mandate/form/"+merchantId+"/"+hash_mandate_view+"/"+mandateId+"/"+requestId+"/"+"rest");
            try {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request = new Request.Builder()
                        .url("https://www.remitademo.net/remita/ecomm/mandate/form/"+merchantId+"/"+hash_mandate_view+"/"+mandateId+"/"+requestId+"/"+"rest")
                        .method("GET", null)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                return response. body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private String JsonfyRequestData() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId", MERCHANT_ID);
        jsonObject.put("serviceTypeId", SERVICE_TYPE_ID);
        jsonObject.put("hash", this.hash512);
        jsonObject.put("payerName", this.payerName);
        jsonObject.put("payerEmail", this.payerEmail);
        jsonObject.put("payerPhone", this.payerPhone);
        jsonObject.put("payerBankCode", this.payerBankCode);
        jsonObject.put("payerAccount", this.payerAccount);
        jsonObject.put("requestId", this.requestId);
        jsonObject.put("amount", this.amount);
        jsonObject.put("startDate", this.startDate);
        jsonObject.put("endDate", this.endDate);
        jsonObject.put("maxNoOfDebits",MAX_NUMBER_OF_DEBITS);
        jsonObject.put("mandateType", MANDATE_TYPE);
        jsonObject.put("frequency", FREQUENCY);
        return jsonObject.toString();
    }

    private String jsonfyAuthRequest(String mandateId, String requestId) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mandateId", mandateId);
        jsonObject.put("requestId",requestId);
        return jsonObject.toString();
    }

    public class StandingOrderTask extends AsyncTask {

        String serverInfo;

        @Override
        protected void onPreExecute() {

            dialogUtils = new DialogUtils(context);
            dialogUtils.showLoanProcessingDialog();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if (!new NetworkUtils(context).isNetworkAvailable()) {
                serverInfo = "No Network Connection";
                return serverInfo;
            }

            String serverResponse = new StandingOrderRequest().send(urlInMotion);

            if (serverResponse != null) {
                return serverResponse;
            }

            return null;
        }

        protected void onPostExecute(final Object result) {

            if(result != null) {

                if(result.toString().equalsIgnoreCase("No Network Connection")){
                    dialogUtils.closeLoanProcessingDialog();
                    standingOrderGenerationListener.onGenerationFailure((String) result);
                }

                else {

                    standingOrderGenerationListener.onRequestAuthorizationCompleted("processing started");
                    ProcessInitialSetupJsonResponse(result,StandingOrder.this.payerBankCode);

                    /* dialogUtils.showLoanSuccessDialog();
                    dialogUtils.setLoanAnimationSuccessListener(new DialogUtils.loanAnimationSuccessListener() {
                        @Override
                        public void onSuccessAnimationCompleted() {
                            standingOrderGenerationListener.onInitialGenerationSuccess((String) result);
                        }
                    });*/

                }
            }
            else{
                dialogUtils.closeLoanProcessingDialog();
                standingOrderGenerationListener.onGenerationFailure("Error occurred please try again");
            }
        }
    }

    public class ValidationRequestTask extends AsyncTask {

        String serverInfo;
        String mandateId,requestId;

        public ValidationRequestTask(String mMandateId, String mRequestId){
            this.mandateId = mMandateId;
            this.requestId = mRequestId;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if (!new NetworkUtils(context).isNetworkAvailable()) {
                serverInfo = "No Network Connection";
                return serverInfo;
            }

            String serverResponse = new RequestAuthRequest().send(requestAuthorizationUrl,mandateId,requestId,timeStamp);
            if (serverResponse != null) {
                return serverResponse;
            }
            return null;
        }

        protected void onPostExecute(final Object result) {

            if(result != null) {

                if(result.toString().equalsIgnoreCase("No Network Connection2")){
                    dialogUtils.closeLoanProcessingDialog();
                    standingOrderGenerationListener.onGenerationFailure((String) result);
                }
                else {
                    //ProcessInitialSetupJsonResponse(result,StandingOrder.this.payerBankCode);


                    dialogUtils.setLoanAnimationSuccessListener(new DialogUtils.loanAnimationSuccessListener() {
                        @Override
                        public void onSuccessAnimationCompleted() {

                            standingOrderGenerationListener.onRequestAuthorizationCompleted((String) result);

                        }
                    });
                }
            }
            else{
                dialogUtils.closeLoanProcessingDialog();
                standingOrderGenerationListener.onGenerationFailure("Error 2 occurred please try again");
            }
        }
    }




    public class PrintMandateTask extends AsyncTask {

        String serverInfo;
        String mandateId,requestId;
        String mandateHash;

        public PrintMandateTask(String mMandateId, String mRequestId){
            this.mandateId = mMandateId;
            this.requestId = mRequestId;
        }

        protected String GenerateMandateHash512(String requestId) {
            String generatedHash = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                String stringToHash = StandingOrder.MERCHANT_ID + StandingOrder.API_KEY + requestId;
                byte[] bytes = md.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bytes.length; i++) {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                generatedHash = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return generatedHash;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            if (!new NetworkUtils(context).isNetworkAvailable()) {
                serverInfo = "No Network Connection";
                return serverInfo;
            }

            mandateHash = GenerateMandateHash512(requestId);
            String serverResponse = new PrintMandateRequest().send(mandateId,requestId,mandateHash,MERCHANT_ID);
            if (serverResponse != null) {
                return serverResponse;
            }
            return null;
        }

        protected void onPostExecute(final Object result) {

            if(result != null) {

                if(result.toString().equalsIgnoreCase("No Network Connection2")){
                    dialogUtils.closeLoanProcessingDialog();
                    standingOrderGenerationListener.onGenerationFailure((String) result);
                }
                else {
                    //ProcessInitialSetupJsonResponse(result,StandingOrder.this.payerBankCode);

                    dialogUtils.showLoanSuccessDialog();
                    dialogUtils.setLoanAnimationSuccessListener(new DialogUtils.loanAnimationSuccessListener() {
                        @Override
                        public void onSuccessAnimationCompleted() {

                            standingOrderGenerationListener.onMandatePrintReady((String) result);

                        }
                    });
                }
            }
            else{
                dialogUtils.closeLoanProcessingDialog();
                standingOrderGenerationListener.onGenerationFailure("Error 2 occurred please try again");
            }
        }
    }



    private void ProcessInitialSetupJsonResponse(Object result, String customerBankCode){

        String json = result.toString().substring(result.toString().indexOf("(")+1,result.toString().lastIndexOf(")"));
        standingOrderGenerationListener.onRequestAuthorizationCompleted(json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String status = jsonObject.getString("status");
            String statusCode = jsonObject.getString("statuscode");
            if(statusCode.equalsIgnoreCase("040")){
                standingOrderGenerationListener.onRequestAuthorizationCompleted(statusCode);
                String requestId = jsonObject.getString("requestId");
                String mandateId = jsonObject.getString("mandateId");

                for (RemitaBank mRemitaBank: this.remitaBanks) {
                    if(mRemitaBank.getBankCode().equals(customerBankCode)){

                        Log.e("startbank ", "ProcessInitialSetupJsonResponse: ");
                        new ValidationRequestTask(mandateId,requestId).execute();
                        break;

                    }
                    else{

                        new PrintMandateTask(mandateId,requestId).execute();
                        break;
                        // standingOrderGenerationListener.onGenerationFailure("error occured in bank");
                    }
                }


               // standingOrderGenerationListener.onInitialGenerationCompleted(statusCode,requestId,mandateId,status);
            }
            else{
                standingOrderGenerationListener.onGenerationFailure(status);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String GenerateUniqueRequestId(){
        String salter = "ABCDEFGHIJLMNOPQRSTUVWXYZamenhof123456890";
        StringBuilder salt = new StringBuilder();
        Random random = new Random();
        while (salt.length() < 18){
            int index = (int)(random.nextFloat() * salter.length());
            salt.append(salter.charAt(index));
        }
        String saltr = salt.toString();
        return  saltr;
    }

    protected class RemitaUrl{

        private String urlInMotion;
        private String urlType;

        public RemitaUrl(String mUrlInMotion, String mUrlType){
            this.urlInMotion = mUrlInMotion;
            this.urlType = mUrlType;
        }

        public String getUrlInMotion() {
            return urlInMotion;
        }

        public String getUrlType() {
            return urlType;
        }
    }

}