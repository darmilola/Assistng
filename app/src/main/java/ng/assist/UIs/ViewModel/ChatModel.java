package ng.assist.UIs.ViewModel;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

public class ChatModel {

    private Context context;
    private String baseUrl = new URL().getBaseUrl();
    private String uploadImageUrl = baseUrl+"users/upload/image";
    private ChatHttpListener chatHttpListener;
    private String uploadedImageUrl = "";
    private ImageUploadDialog imageUploadDialog;
    private String encodedImage;

    public ChatModel(String encodedImage, Context context){
        this.context = context;
        imageUploadDialog = new ImageUploadDialog(context);
        this.encodedImage = encodedImage;
    }


    public interface ChatHttpListener{
        void onImageUpload(String imageUrl);
        void onError(String message);
    }

    public void setChatHttpListener(ChatHttpListener chatHttpListener) {
        this.chatHttpListener = chatHttpListener;
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
                    uploadedImageUrl = jsonObject.getString("data");
                    chatHttpListener.onImageUpload(uploadedImageUrl);
                }
                else{
                    chatHttpListener.onError("Error Uploading image please try again");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                chatHttpListener.onError("Error Occurred please try again");
            }
        }
    };


    private String buildImageUploadJson(String encodedImage){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image",encodedImage);
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
            RequestBody requestBody = RequestBody.create(JSON,buildImageUploadJson(encodedImage));
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


}
