package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ng.assist.UIs.Utils.*;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignupModel {

    private String firstname, lastname, emailAddress, password, encodedImage;
    private SignupListener signupListener;
    LoadingDialogUtils loadingDialogUtils;
    Context context;
    private String baseUrl = new URL().getBaseUrl();
    private String mailCheckUrl = baseUrl+"users/check/email";
    private String uploadImageUrl = baseUrl+"users/profileimage/upload/image";
    private String registerUrl = baseUrl+"users";
    private String uploadedImageUrl = "";

     private Handler mailCheckHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //mail does not exist continue with reg
                    UploadProfileImage();
                }
                else if(status.equalsIgnoreCase("failure")){
                    //mail exist
                    signupListener.isFailed("Email already exist, please try Login");
                    loadingDialogUtils.cancelLoadingDialog();
                }
                else{
                    signupListener.isFailed("Error Occured please try again");
                    loadingDialogUtils.cancelLoadingDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                signupListener.isFailed("Error Occured please try again");
            }
        }
    };


    private Handler gmailCheckHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //gmail does not exist sign it up
                    signupWithGmail();
                }
                else if(status.equalsIgnoreCase("failure")){
                    //mail exist login
                }
                else{
                    signupListener.isFailed("Error Occured please try again");
                    loadingDialogUtils.cancelLoadingDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                signupListener.isFailed("Error Occured please try again");
            }



        }
    };

    private Handler registerUserHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //registration successful
                    signupListener.isSuccessful("Sign Up Successful");

                }
                else{
                    //failure
                    signupListener.isFailed("Error Occurred please try Again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                signupListener.isFailed("Error Occured please try again");
            }
            loadingDialogUtils.cancelLoadingDialog();
        }
    };

    private Handler imageUploadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
           try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //mail does not exist continue with registration
                  uploadedImageUrl = jsonObject.getString("data");
                  completeRegistration();
                }
                else{
                    signupListener.isFailed("Error Uploading image please try again");
                    loadingDialogUtils.cancelLoadingDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
               signupListener.isFailed("Error Occured please try again");
            }

        }
    };


    public interface SignupListener {
        void isSuccessful(String message);
        void isFailed(String message);
    }

    public SignupModel(String firstname, String lastname, String emailAddress, String imageUrl, Context context) {
        loadingDialogUtils = new LoadingDialogUtils(context);
        this.context = context;
        this.emailAddress = emailAddress;
        this.firstname = firstname;
        this.lastname = lastname;
        this.uploadedImageUrl = imageUrl;
    }

    public SignupModel(String firstname, String lastname, String emailAddress, String password, String encodedImage, Context context) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.emailAddress = emailAddress;
        this.password = password;
        this.encodedImage = encodedImage;
        loadingDialogUtils = new LoadingDialogUtils(context);
        this.context = context;
    }

    public void setSignupListener(SignupListener signupListener) {
        this.signupListener = signupListener;
    }

    public void SignupUser(){
        CheckMailExist();
    }


    public void SignupUsingGmail(){
        CheckGmailExist();
    }


    private void CheckMailExist() {

        loadingDialogUtils.showLoadingDialog("Creating Account");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildMailCheckJson(emailAddress));
            Request request = new Request.Builder()
                        .url(mailCheckUrl)
                        .post(requestBody)
                        .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = mailCheckHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            mailCheckHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    private void CheckGmailExist() {

        loadingDialogUtils.showLoadingDialog("Authenticating");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildMailCheckJson(emailAddress));
            Request request = new Request.Builder()
                    .url(mailCheckUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = gmailCheckHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            gmailCheckHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    private void completeRegistration(){

        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildRegistrationJson(firstname,lastname,emailAddress,password, uploadedImageUrl));
            Request request = new Request.Builder()
                    .url(registerUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = registerUserHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            registerUserHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private void signupWithGmail(){

        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildGoogleRegistrationJson(firstname,lastname,emailAddress,uploadedImageUrl));
            Request request = new Request.Builder()
                    .url(registerUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = registerUserHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            registerUserHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }


    private void UploadProfileImage(){
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildImageUploadJson(encodedImage));
            Request request = new Request.Builder()
                    .url(uploadImageUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = imageUploadHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            imageUploadHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }



    private String buildMailCheckJson(String emailAddress){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email",emailAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildRegistrationJson(String firstname,String lastname, String emailAddress, String password, String profileImageUrl){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstname",firstname);
            jsonObject.put("lastname",lastname);
            jsonObject.put("email",emailAddress);
            jsonObject.put("password",password);
            jsonObject.put("profileImage",profileImageUrl);
            jsonObject.put("signInType","email");
            jsonObject.put("accountType","normal");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String buildGoogleRegistrationJson(String firstname,String lastname, String emailAddress, String profileImageUrl){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstname",firstname);
            jsonObject.put("lastname",lastname);
            jsonObject.put("email",emailAddress);
            jsonObject.put("profileImage",profileImageUrl);
            jsonObject.put("signInType","google");
            jsonObject.put("accountType","normal");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    private String buildImageUploadJson(String encodedImage){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image",encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
