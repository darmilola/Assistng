package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class SignUpWithEmail extends AppCompatActivity {

    static int PICK_IMAGE = 1;
    static int CROP_IMAGE = 2;
    MaterialButton signupWithEmail;
    ImageView selectProfileImageButton;
    CircleImageView circleImageView;

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
                startActivity(new Intent(SignUpWithEmail.this,MainActivity.class));
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
            circleImageView.setImageBitmap(bmp);
            Toast.makeText(this, "am here", Toast.LENGTH_SHORT).show();
        }

    }
}
