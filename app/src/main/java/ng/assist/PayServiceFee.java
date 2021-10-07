package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import ng.assist.UIs.ViewModel.CreatBill;
import ng.assist.UIs.ViewModel.ServicesModel;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

public class PayServiceFee extends AppCompatActivity {

    TextView providerName,providerEmail;
    EditText serviceFee;
    MaterialButton serviceFeeSend,successBack;
    LinearLayout payLayout,successLayout;
    TextView successAmount,successText;
    ServicesModel servicesModel;
    ImageView profileImage;
    int walletBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_service_fee);
        initView();
    }

    private void initView(){

        profileImage = findViewById(R.id.pay_service_fee_profile_image);
        servicesModel = getIntent().getParcelableExtra("provider_info");
        providerName = findViewById(R.id.pay_service_fee_name);
        providerEmail = findViewById(R.id.pay_service_fee_email);
        serviceFee = findViewById(R.id.pay_service_fee_amount);
        serviceFeeSend = findViewById(R.id.pay_service_fee_send);
        successAmount = findViewById(R.id.pay_service_fee_success_amount);
        successText = findViewById(R.id.pay_service_fee_success_text);
        successBack = findViewById(R.id.pay_service_fee_success_back);
        payLayout = findViewById(R.id.pay_service_fee_pay_layout);
        successLayout = findViewById(R.id.pay_service_fee_success_layout);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayServiceFee.this);
        walletBalance = Integer.parseInt(preferences.getString("walletBalance",""));
        String userId = PreferenceManager.getDefaultSharedPreferences(this).getString("userEmail","null");
        String fullname = servicesModel.getHandymanFirstname()+" "+servicesModel.getHandymanLastname();
        successText.setText("You have successfully booked for the service of "+fullname);

        providerName.setText(servicesModel.getHandymanFirstname()+" "+servicesModel.getHandymanLastname());
        providerEmail.setText(servicesModel.getHandymanId());
        Glide.with(PayServiceFee.this)
                .load(servicesModel.getHandymanImage())
                .placeholder(R.drawable.background_image)
                .error(R.drawable.background_image)
                .into(profileImage);


        serviceFeeSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidAmount()){
                    String fullname = servicesModel.getHandymanFirstname()+" "+servicesModel.getHandymanLastname();
                    int cost = Integer.parseInt(serviceFee.getText().toString().trim());
                    CreatBill creatBill = new CreatBill(userId,servicesModel.getHandymanId(),cost,"4",fullname,PayServiceFee.this,1);
                    creatBill.CreateBill();
                    creatBill.setCreateBillListener(new CreatBill.CreateBillListener() {
                        @Override
                        public void onSuccess() {
                            successAmount.setText(serviceFee.getText().toString().trim());
                            payLayout.setVisibility(View.GONE);
                            successLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(PayServiceFee.this, "Error occurred please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        successBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean isValidAmount(){
        boolean isValid = true;
        if(TextUtils.isEmpty(serviceFee.getText().toString().trim())){
            serviceFee.setError("Required");
            isValid = false;
        }
        else if(walletBalance < Long.parseLong(serviceFee.getText().toString())){
            Toast.makeText(PayServiceFee.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
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
}
