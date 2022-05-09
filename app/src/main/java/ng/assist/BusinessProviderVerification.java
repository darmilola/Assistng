package ng.assist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.VerificationModel;

public class BusinessProviderVerification extends AppCompatActivity {
    static int PICK_IMAGE = 1,PICK_CAC = 2,PICK_MEMO = 3;
    String uploadIdUrl = "",uploadCACUrl = "",uploadMemoUrl = "";
    EditText firstname,lastname,address,phonenumber,businessName,businessRegNumber,businessAddress;
    LinearLayout idLayout,cacLayout,memoLayout;
    ImageView uploadIdImg,uploadCACImg,uploadMemoImg;
    MaterialButton apply;
    String mFirstname,mLastname,mAddress,mPhonenumber,mBusinessName,mBusinessRegNumber,mBusinessAddress;
    String userId;
    ScrollView rootView;
    TextView statusText,uploadedIDName,uploadedCACName,uploadedMemoName;
    ImageView navBack;
    AlertDialog.Builder builder;
    ListDialog listDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_provider_verification);
        initView();
    }

    private void initView(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BusinessProviderVerification.this);
        String status = preferences.getString("verificationStatus","notVerified");

        navBack = findViewById(R.id.nav_back);
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firstname = findViewById(R.id.verification_firstname);
        lastname = findViewById(R.id.verification_lastname);
        address = findViewById(R.id.verification_address);
        phonenumber = findViewById(R.id.verification_phonenumber);
        businessName = findViewById(R.id.verification_business_name);
        businessRegNumber = findViewById(R.id.verification_business_reg_number);
        businessAddress = findViewById(R.id.verification_business_address);
        idLayout = findViewById(R.id.verification_id_layout);
        cacLayout = findViewById(R.id.verification_cac_layout);
        memoLayout = findViewById(R.id.verification_memo_layout);
        uploadIdImg = findViewById(R.id.verification_upload_img);
        uploadCACImg = findViewById(R.id.verification_uploaded_cac_cert_img);
        uploadMemoImg = findViewById(R.id.verification_uploaded_memo_img);
        statusText = findViewById(R.id.verification_status_text);
        uploadedIDName = findViewById(R.id.verification_uploaded_image_name);
        uploadedCACName = findViewById(R.id.verification_uploaded_cac_cert_text);
        uploadedMemoName = findViewById(R.id.verification_uploaded_memo_text);
        apply = findViewById(R.id.verification_apply);
        rootView = findViewById(R.id.verification_form_root);
        navBack = findViewById(R.id.nav_back);


        if(status.equalsIgnoreCase("pending")){
            statusText.setText("Your Business Verification is Pending...");
            statusText.setVisibility(View.VISIBLE);
            rootView.setVisibility(View.GONE);
        }
        else if(status.equalsIgnoreCase("approved")){
            statusText.setText("You have been successfully Verified");
            statusText.setVisibility(View.VISIBLE);
            rootView.setVisibility(View.VISIBLE);
        }
        else{
            statusText.setVisibility(View.GONE);
            rootView.setVisibility(View.VISIBLE);
        }


        idLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Valid Id"), PICK_IMAGE);

            }
        });


        cacLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select CAC Certificate"), PICK_CAC);

            }
        });

        memoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Memorandum And Article of Association"), PICK_MEMO);
            }
        });


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirstname = firstname.getText().toString().trim();
                mLastname = lastname.getText().toString().trim();
                mAddress = address.getText().toString().trim();
                mPhonenumber = phonenumber.getText().toString().trim();
                mBusinessName = businessName.getText().toString().trim();
                mBusinessAddress = businessAddress.getText().toString().trim();
                mBusinessRegNumber = businessRegNumber.getText().toString().trim();
                if(isValidForm()){
                    VerificationModel verificationModel = new VerificationModel(BusinessProviderVerification.this,mFirstname,mLastname,mAddress,mPhonenumber,userId,mBusinessName,mBusinessAddress,mBusinessRegNumber,uploadIdUrl,uploadCACUrl,uploadMemoUrl);
                    verificationModel.CreateBuisnessVerification();
                    verificationModel.setCreateVerifyListener(new VerificationModel.CreateVerifyListener() {
                        @Override
                        public void onSuccess() {
                            showSuccessDialog();
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(BusinessProviderVerification.this, message, Toast.LENGTH_LONG).show();
                        }
                    });
                }


            }
        });


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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            File f = new File(getPath(BusinessProviderVerification.this,selectedImageUri));
            String imageName = f.getName();
            uploadedIDName.setText(imageName);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);
                VerificationModel verificationModel = new VerificationModel(BusinessProviderVerification.this,imageString);
                verificationModel.UploadValidId();
                verificationModel.setImageUploadListener(new VerificationModel.ImageUploadListener() {
                    @Override
                    public void onUploaded(String link) {
                        uploadIdUrl = link;
                        Glide.with(BusinessProviderVerification.this)
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


        if (requestCode == PICK_CAC && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            File f = new File(getPath(BusinessProviderVerification.this,selectedImageUri));
            String imageName = f.getName();
            uploadedCACName.setText(imageName);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);
                VerificationModel verificationModel = new VerificationModel(BusinessProviderVerification.this,imageString);
                verificationModel.UploadValidId();
                verificationModel.setImageUploadListener(new VerificationModel.ImageUploadListener() {
                    @Override
                    public void onUploaded(String link) {
                        uploadCACUrl = link;
                        Glide.with(BusinessProviderVerification.this)
                                .load(link)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        cacLayout.setBackground(resource);
                                        uploadCACImg.setVisibility(View.GONE);
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


        if (requestCode == PICK_MEMO && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            File f = new File(getPath(BusinessProviderVerification.this,selectedImageUri));
            String imageName = f.getName();
            uploadedMemoName.setText(imageName);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);
                VerificationModel verificationModel = new VerificationModel(BusinessProviderVerification.this,imageString);
                verificationModel.UploadValidId();
                verificationModel.setImageUploadListener(new VerificationModel.ImageUploadListener() {
                    @Override
                    public void onUploaded(String link) {
                        uploadMemoUrl = link;
                        Glide.with(BusinessProviderVerification.this)
                                .load(link)
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        memoLayout.setBackground(resource);
                                        uploadMemoImg.setVisibility(View.GONE);
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

    private void showSuccessDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Your submission has been received, the review process may take up to 48 hours")
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        finish();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Assist Business Verification");
        alert.show();
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
        if (TextUtils.isEmpty(mBusinessAddress)) {
            businessAddress.setError("Required");
            valid = false;
            return valid;
        }
        if (TextUtils.isEmpty(mBusinessName)) {
            businessName.setError("Required");
            valid = false;
            return valid;
        }
        if (TextUtils.isEmpty(mBusinessRegNumber)) {
            businessRegNumber.setError("Required");
            valid = false;
            return valid;
        }
        if(TextUtils.isEmpty(uploadIdUrl)){
            Toast.makeText(this, "Upload Valid Id", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }

        if(TextUtils.isEmpty(uploadCACUrl)){
            Toast.makeText(this, "Upload CAC Certificate", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }

        if(TextUtils.isEmpty(uploadMemoUrl)){
            Toast.makeText(this, "Upload Memorandum of Association", Toast.LENGTH_LONG).show();
            valid = false;
            return valid;
        }

        return valid;
    }


    public String BitmapToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        String imgString = Base64.encodeToString(b, Base64.DEFAULT);
        return imgString;
    }
}