package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;
import ng.assist.UIs.Utils.LoadingDialogUtils;
import ng.assist.UIs.Utils.NetworkUtils;
import ng.assist.UIs.ViewModel.SignupModel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;

public class SignUpWithEmail extends AppCompatActivity {

    static int PICK_IMAGE = 1;
    static int CROP_IMAGE = 2;
    MaterialButton signupWithEmail;
    ImageView selectProfileImageButton;
    CircleImageView circleImageView;
    LoadingDialogUtils loadingDialogUtils;
    TextInputEditText firstname,lastname,emailAddress,password;
    TextInputLayout firstnameLayout,lastnameLayout,emailLayout,passwordLayout;
    String mFirstname, mLastname, mEmailaddress, mPassword,mProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_email);
        initView();
    }

    private void initView(){
        signupWithEmail = findViewById(R.id.signup_with_email_button);
        selectProfileImageButton = findViewById(R.id.select_profile_image_button);
        circleImageView = findViewById(R.id.signup_profile_image);
        loadingDialogUtils = new LoadingDialogUtils(SignUpWithEmail.this);

        firstnameLayout = findViewById(R.id.sign_up_firstname_layout);
        lastnameLayout = findViewById(R.id.sign_up_lastname_layout);
        emailLayout = findViewById(R.id.sign_up_email_layout);
        passwordLayout = findViewById(R.id.sign_up_password_layout);

        firstname = findViewById(R.id.sign_up_firstname);
        lastname = findViewById(R.id.sign_up_lastname);
        emailAddress = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_password);

        selectProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE);
            }
        });

        signupWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             mFirstname = firstname.getText().toString().trim();
             mLastname = lastname.getText().toString().trim();
             mEmailaddress = emailAddress.getText().toString().trim();
             mPassword = password.getText().toString().trim();
             if(isValidForm()){
                 SignupModel signupModel = new SignupModel(mFirstname,mLastname,mEmailaddress,mPassword,mProfileImage,SignUpWithEmail.this);
                 if(!new NetworkUtils(SignUpWithEmail.this).isNetworkAvailable()) {
                     Toast.makeText(SignUpWithEmail.this, "No Network", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     signupModel.SignupUser();
                 }
                 signupModel.setSignupListener(new SignupModel.SignupListener() {
                     @Override
                     public void isSuccessful(String message) {
                         Intent intent = new Intent(SignUpWithEmail.this,MainActivity.class);
                         intent.putExtra("email",mEmailaddress);
                         startActivity(intent);
                     }
                     @Override
                     public void isFailed(String message) {
                         Toast.makeText(SignUpWithEmail.this, message, Toast.LENGTH_SHORT).show();
                     }
                 });
             }

            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            String selectedImageString = data.getData().toString();
            Intent cropIntent = new Intent(SignUpWithEmail.this,ProfileImageCrop.class);
            cropIntent.putExtra("image",selectedImageString);
            startActivityForResult(cropIntent,CROP_IMAGE);
        }

        if (requestCode == CROP_IMAGE && resultCode == 2) {
            byte[] byteArray = data.getByteArrayExtra("croppedImage");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            mProfileImage = getEncodedImage(bmp);
            circleImageView.setImageBitmap(bmp);
        }

    }

    public static String getEncodedImage(Bitmap bitmap){
        final int MAX_IMAGE_SIZE = 500 * 1024; // max final file size in kilobytes
        byte[] bmpPicByteArray;

        //Bitmap scBitmap  = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do{
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
            Log.d("compressBitmap", "Size: " + streamLength/1024+" kb");
        }while (streamLength >= MAX_IMAGE_SIZE);

        String encodedImage = Base64.encodeToString(bmpPicByteArray, Base64.DEFAULT);
        return encodedImage;

    }

   private boolean isValidForm() {

        boolean valid = true;

        if (TextUtils.isEmpty(mFirstname)) {
            firstnameLayout.setError("Required");
            valid = false;
            return valid;
        }
       if (TextUtils.isEmpty(mLastname)) {
           lastnameLayout.setError("Required");
           valid = false;
           return valid;
       }
       if (TextUtils.isEmpty(mEmailaddress)) {
           emailLayout.setError("Required");
           valid = false;
           return valid;
       }
       if (TextUtils.isEmpty(mPassword)) {
           passwordLayout.setError("Required");
           valid = false;
           return valid;
       }
       if(TextUtils.isEmpty(mProfileImage)){
           Toast.makeText(this, "Upload profile image", Toast.LENGTH_LONG).show();
           valid = false;
           return valid;
       }
       return valid;
   }

}
