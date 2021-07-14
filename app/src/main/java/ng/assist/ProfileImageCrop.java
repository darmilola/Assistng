package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.takusemba.cropme.CropImageView;
import com.takusemba.cropme.CropLayout;
import com.takusemba.cropme.OnCropListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;

public class ProfileImageCrop extends AppCompatActivity {

    CropLayout profileImageCropView;
    String imageString;
    LinearLayout cancelCrop,cropImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_crop);
        initView();
    }

    private void initView(){
        profileImageCropView = findViewById(R.id.profile_image_crop_view);
        cropImage = findViewById(R.id.crop_image_icon);
        cancelCrop = findViewById(R.id.cancel_crop_icon);
        imageString = getIntent().getStringExtra("image");
        profileImageCropView.setUri(Uri.parse(imageString));
        cropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileImageCropView.crop();
            }
        });

        cancelCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(100);
                finish();
            }
        });


        profileImageCropView.addOnCropListener(new OnCropListener() {
            @Override
            public void onSuccess(@NotNull Bitmap bitmap) {
                byte[] bitmapArray = convertBitmap(bitmap);
                Intent intent = new Intent();
                intent.putExtra("croppedImage",bitmapArray);
                setResult(2,intent);
                finish();
            }

            @Override
            public void onFailure(@NotNull Exception e) {
                 Toast.makeText(ProfileImageCrop.this,"Error occured please try again",Toast.LENGTH_LONG).show();
                 finish();
            }
        });
    }


    private byte[] convertBitmap(Bitmap bitmap){

        //Convert to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }
}
