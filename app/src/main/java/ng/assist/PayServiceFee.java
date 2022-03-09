package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import ng.assist.UIs.ViewModel.CreatBill;
import ng.assist.UIs.ViewModel.ProviderBookingsModel;
import ng.assist.UIs.ViewModel.ServicesModel;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;

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

import java.sql.Timestamp;
import java.util.Date;

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
                String billId = generateBillId();
                if(isValidAmount()){
                    String fullname = servicesModel.getHandymanFirstname()+" "+servicesModel.getHandymanLastname();
                    int cost = Integer.parseInt(serviceFee.getText().toString().trim());
                    CreatBill creatBill = new CreatBill(billId,userId,servicesModel.getHandymanId(),cost,"4",fullname,PayServiceFee.this,1);
                    creatBill.CreateBill();
                    creatBill.setCreateBillListener(new CreatBill.CreateBillListener() {
                        @Override
                        public void onSuccess() {
                            successAmount.setText(serviceFee.getText().toString().trim());
                            payLayout.setVisibility(View.GONE);
                            successLayout.setVisibility(View.VISIBLE);
                            addPending();
                            createBookings(billId,userId,servicesModel.getHandymanId(),Integer.toString(cost));
                            Date date = new Date();
                            Timestamp timestamp = new Timestamp(date.getTime());
                            insertBooking(0,4,"Service",timestamp.toString(),Integer.toString(cost),"");
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

    private void createBookings(String bookingId, String userId, String providerId, String amount){
        ProviderBookingsModel providerBookingsModel = new ProviderBookingsModel(providerId,bookingId,amount,userId);
        providerBookingsModel.createBookings();
        providerBookingsModel.setUpdateListener(new ProviderBookingsModel.UpdateListener() {
            @Override
            public void onUpdateSuccess() {

            }

            @Override
            public void onError(String message) {
                Toast.makeText(PayServiceFee.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertBooking(int id,int type, String title, String timestamp, String amount, String orderId){
        TransactionDatabase db = Room.databaseBuilder(PayServiceFee.this,
                TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
        Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
        TransactionDao transactionDao = db.transactionDao();
        transactionDao.insert(transactions);
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

    private void addPending(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(PayServiceFee.this);
        preferences.edit().putBoolean("isPending",true).apply();
    }


    // function to generate a random string of length n
    static String generateBillId()
    {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(50);

        for (int i = 0; i < 50; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
