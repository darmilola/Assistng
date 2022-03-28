package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.content.SyncRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ng.assist.UIs.Utils.LoadingDialogUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerificationModel {

    private String firstname, lastname, address, phonenumber, imageLink, userId,state,lga,occupation;
    private LoadingDialogUtils loadingDialogUtils;
    private String baseUrl = new URL().getBaseUrl();
    private String verificationUrl = baseUrl+"verification";
    private String uploadImageUrl = baseUrl+"users/upload/image";
    private Context context;
    private CreateVerifyListener createVerifyListener;
    private ImageUploadListener imageUploadListener;
    private String encodedImage;

    public void setCreateVerifyListener(CreateVerifyListener createVerifyListener) {
        this.createVerifyListener = createVerifyListener;
    }

    public void setImageUploadListener(ImageUploadListener imageUploadListener) {
        this.imageUploadListener = imageUploadListener;
    }

    public interface CreateVerifyListener{
        void onSuccess();
        void onFailure(String message);
    }

    public interface ImageUploadListener{
          void onUploaded(String link);
    }

   public VerificationModel(Context context, String firstname, String lastname, String address, String phonenumber, String imageLink, String userId, String state, String lga,String occupation){
                      this.context = context;
                      this.firstname = firstname;
                      this.lastname = lastname;
                      this.address = address;
                      this.phonenumber = phonenumber;
                      this.userId = userId;
                      this.imageLink = imageLink;
                      this.state = state;
                      this.lga = lga;
                      this.occupation = occupation;
                      loadingDialogUtils = new LoadingDialogUtils(context);
    }

   public VerificationModel(Context context, String encodedImage){
                     this.encodedImage = encodedImage;
                     this.context = context;
                     loadingDialogUtils = new LoadingDialogUtils(context);
    }


    public void CreateVerification() {
        loadingDialogUtils.showLoadingDialog("Creating Verification...");
        Runnable runnable = () -> {
            String mResponse = "";
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .build();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON,buildVerify(firstname,lastname,address,phonenumber,userId,imageLink,state,lga,occupation));
            Request request = new Request.Builder()
                    .url(verificationUrl)
                    .post(requestBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if(response != null){
                    mResponse =  response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message msg = createVerifyHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("response", mResponse);
            msg.setData(bundle);
            createVerifyHandler.sendMessage(msg);
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
    }

    private Handler createVerifyHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            if(loadingDialogUtils != null)loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    createVerifyListener.onSuccess();
                }
                else if(status.equalsIgnoreCase("failure")){
                    createVerifyListener.onFailure("Error Occurred");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                createVerifyListener.onFailure(e.getLocalizedMessage());
            }
        }
    };




    private String buildVerify(String firstname, String lastname, String address, String phonenumber, String userId, String imageLink, String state, String lga,String occupation){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstname",firstname);
            jsonObject.put("lastname",lastname);
            jsonObject.put("address",address);
            jsonObject.put("phonenumber",phonenumber);
            jsonObject.put("valid_id",imageLink);
            jsonObject.put("state",state);
            jsonObject.put("lga",lga);
            jsonObject.put("userId",userId);
            jsonObject.put("status","pending");
            jsonObject.put("occupation",occupation);
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


    public void UploadValidId(){
        loadingDialogUtils.showLoadingDialog("Uploading Id...");
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


    private Handler imageUploadHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            loadingDialogUtils.cancelLoadingDialog();
            Bundle bundle = msg.getData();
            String response = bundle.getString("response");
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                if(status.equalsIgnoreCase("success")){
                    //mail does not exist continue with registration
                   String uploadedImageUrl = jsonObject.getString("data");
                   imageUploadListener.onUploaded(uploadedImageUrl);
                }
                else{
                    loadingDialogUtils.cancelLoadingDialog();

                }
            } catch (JSONException e) {
                loadingDialogUtils.cancelLoadingDialog();
                e.printStackTrace();
            }

        }
    };


}
