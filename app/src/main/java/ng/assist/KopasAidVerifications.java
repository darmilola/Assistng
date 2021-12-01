package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.VerificationModel;

public class KopasAidVerifications extends AppCompatActivity {

    static int PICK_IMAGE = 1;
    EditText firstname,lastname,address,phonenumber;
    LinearLayout idLayout;
    ImageView uploadIdImg;
    MaterialButton apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kopas_aid_verifications);
        initView();
    }

    private void initView(){
        firstname = findViewById(R.id.verification_firstname);
        lastname = findViewById(R.id.verification_lastname);
        address = findViewById(R.id.verification_address);
        phonenumber = findViewById(R.id.verification_phonenumber);
        idLayout = findViewById(R.id.verification_id_layout);
        uploadIdImg = findViewById(R.id.verification_upload_img);
        apply = findViewById(R.id.verification_apply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



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

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.transparent));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);
                VerificationModel verificationModel = new VerificationModel(KopasAidVerifications.this,imageString);
                verificationModel.UploadValidId();
                verificationModel.setImageUploadListener(new VerificationModel.ImageUploadListener() {
                    @Override
                    public void onUploaded(String link) {

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

}
