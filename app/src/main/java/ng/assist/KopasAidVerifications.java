package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

public class KopasAidVerifications extends AppCompatActivity {

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
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.transparent));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
