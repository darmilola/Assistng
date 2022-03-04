package ng.assist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.CreatBill;
import ng.assist.UIs.ViewModel.VerificationModel;

public class ServiceProviderVerifications extends AppCompatActivity {

    static int PICK_IMAGE = 1;
    String uploadIdUrl = "";
    EditText firstname,lastname,address,phonenumber,lga;
    LinearLayout idLayout;
    ImageView uploadIdImg;
    MaterialButton apply;
    String mFirstname,mLastname,mAddress,mPhonenumber,mState,mLga;
    String userId;
    ScrollView rootView;
    TextView statusText,uploadedImageName,state;
    ImageView navBack;
    AlertDialog.Builder builder;
    ListDialog listDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_verifications);
        initView();
    }

    private void initView(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ServiceProviderVerifications.this);
        String status = preferences.getString("verificationStatus","notVerified");


        initDialog();
        navBack = findViewById(R.id.nav_back);
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        rootView = findViewById(R.id.verification_form_root);
        statusText = findViewById(R.id.verification_status_text);
        state = findViewById(R.id.verification_state);
        lga = findViewById(R.id.verification_lga);
        uploadedImageName = findViewById(R.id.verification_uploaded_image_name);

        if(status.equalsIgnoreCase("pending")){
            statusText.setText("Your Verification is Pending...");
            statusText.setVisibility(View.VISIBLE);
            rootView.setVisibility(View.GONE);
        }
        else if(status.equalsIgnoreCase("approved")){
          // statusText.setText("You have been successfully Verified");
          // statusText.setVisibility(View.VISIBLE);
           rootView.setVisibility(View.VISIBLE);
        }
        else{
            statusText.setVisibility(View.GONE);
            rootView.setVisibility(View.VISIBLE);
        }

        firstname = findViewById(R.id.verification_firstname);
        lastname = findViewById(R.id.verification_lastname);
        address = findViewById(R.id.verification_address);
        phonenumber = findViewById(R.id.verification_phonenumber);
        idLayout = findViewById(R.id.verification_id_layout);
        uploadIdImg = findViewById(R.id.verification_upload_img);
        apply = findViewById(R.id.verification_apply);
        userId = preferences.getString("userEmail","");

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mFirstname = firstname.getText().toString().trim();
              mLastname = lastname.getText().toString().trim();
              mAddress = address.getText().toString().trim();
              mPhonenumber = phonenumber.getText().toString().trim();
              mState = state.getText().toString().trim();
              mLga = lga.getText().toString().trim();
              if(isValidForm()){
                  VerificationModel verificationModel = new VerificationModel(ServiceProviderVerifications.this,mFirstname,mLastname,mAddress,mPhonenumber,uploadIdUrl,userId,mState,mLga);
                  verificationModel.CreateVerification();
                  verificationModel.setCreateVerifyListener(new VerificationModel.CreateVerifyListener() {
                      @Override
                      public void onSuccess() {
                         showSuccessDialog();
                      }

                      @Override
                      public void onFailure(String message) {
                          Toast.makeText(ServiceProviderVerifications.this, message, Toast.LENGTH_LONG).show();
                      }
                  });
              }


            }
        });

        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog.showListDialog();
            }
        });

        idLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Valid Id"), PICK_IMAGE);

            }
        });
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            File f = new File(getPath(ServiceProviderVerifications.this,selectedImageUri));
            String imageName = f.getName();
            uploadedImageName.setText(imageName);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);
                VerificationModel verificationModel = new VerificationModel(ServiceProviderVerifications.this,imageString);
                verificationModel.UploadValidId();
                verificationModel.setImageUploadListener(new VerificationModel.ImageUploadListener() {
                    @Override
                    public void onUploaded(String link) {
                        uploadIdUrl = link;
                        Glide.with(ServiceProviderVerifications.this)
                                .load(link)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        idLayout.setBackground(resource);
                                        uploadIdImg.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public String BitmapToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        String imgString = Base64.encodeToString(b, Base64.DEFAULT);
        return imgString;
    }

    private boolean isValidForm() {

        boolean valid = true;

        if (TextUtils.isEmpty(mFirstname)) {
            firstname.setError("Required");
            valid = false;
            return valid;
        }
        if (TextUtils.isEmpty(mLastname)) {
            lastname.setError("Required");
            valid = false;
            return valid;
        }
        if (TextUtils.isEmpty(mAddress)) {
            address.setError("Required");
            valid = false;
            return valid;
        }
        if (TextUtils.isEmpty(mPhonenumber)) {
            phonenumber.setError("Required");
            valid = false;
            return valid;
        }
        if (TextUtils.isEmpty(mState)) {
            state.setError("Required");
            valid = false;
            return valid;
        }
        if (TextUtils.isEmpty(mLga)) {
            lga.setError("Required");
            valid = false;
            return valid;
        }
        if(TextUtils.isEmpty(uploadIdUrl)){
            Toast.makeText(this, "Upload Valid Id", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }
        return valid;
    }

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void showSuccessDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Your submission has been received, the review process may take up to 24 hours")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                       finish();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Assist Verification");
        alert.show();
    }

    private void initDialog(){
        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("Lagos");
        cityList.add("Abuja");
        cityList.add("Kano");

        String[] stateList = {"Abia","Adamawa","Akwa Ibom","Anambra","Bauchi","Bayelsa","Benue","Borno","Cross River","Delta","Ebonyi","Edo","Ekiti","Enugu","Gombe","Imo","Jigawa","Kaduna","Kano","Katsina","Kebbi"
                ,"Kogi","Kwara","Lagos","Nasarawa","Niger","Ogun","Ondo","Osun","Oyo","Plateau","Rivers","Sokoto","Taraba","Yobe","Zamfara"};

        for (String city : stateList) {
            cityList.add(city);
        }

        listDialog = new ListDialog(cityList,ServiceProviderVerifications.this);
        listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
            @Override
            public void onItemClicked(String city) {
                state.setText(city);
            }
        });
    }

}
