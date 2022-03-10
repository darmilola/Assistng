package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import ng.assist.UIs.Utils.LoadingDialogUtils;
import ng.assist.UIs.ViewModel.TopUpModel;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.google.android.material.button.MaterialButton;
import com.tenbis.library.consts.CardType;
import com.tenbis.library.listeners.OnCreditCardStateChanged;
import com.tenbis.library.models.CreditCard;
import com.tenbis.library.views.CompactCreditCardInput;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.Date;

public class TopUp extends AppCompatActivity {

    private String mCardNumber = "4084084084084081";
    private int mExpiryMonth = 11;
    private int mExpiryYear = 22;
    private String mCvv = "408";
    private Card card;
    private boolean isCardValid = false;
    MaterialButton topUpButton;
    EditText topUpAmount;
    int topUpAmountValue;
    LoadingDialogUtils loadingDialogUtils;
    String userId = "";
    LinearLayout backNav;
    String firstname, lastname;
    String publicKey = "FLWPUBK_TEST-61b223e8283eee1c9d387f5f9eba8316-X";
    String encryptionKey = "FLWSECK_TESTa32b07c45304";
    String transferRef = generateTransferRef();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaystackSdk.initialize(this);
        setContentView(R.layout.activity_top_up);
        initView();
    }

    private void initView() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TopUp.this);
        userId = preferences.getString("userEmail", "");
        firstname = preferences.getString("firstname","");
        lastname = preferences.getString("lastname","");

        backNav = findViewById(R.id.top_up_back_nav);
        loadingDialogUtils = new LoadingDialogUtils(TopUp.this);
        topUpAmount = findViewById(R.id.top_up_amount_text);
        topUpButton = findViewById(R.id.topup_charge_card_button);
        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeCard();
            }
        });

        backNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void chargeCard(){
        card = new Card(mCardNumber, mExpiryMonth, mExpiryYear, mCvv);
        if(TextUtils.isEmpty(topUpAmount.getText().toString().trim())){
            topUpAmount.setError("Required");
            return;
        }
        topUpAmountValue = Integer.parseInt(topUpAmount.getText().toString().trim());

        new RaveUiManager(TopUp.this).setAmount(topUpAmountValue)
                .setCurrency("NGN")
                .setEmail(userId)
                .setfName(firstname)
                .setlName(lastname)
                .setPublicKey(publicKey)
                .setEncryptionKey(encryptionKey)
                .setTxRef(transferRef)
                .acceptUssdPayments(true)
                .acceptBarterPayments(true)
                .allowSaveCardFeature(true)
                .acceptAccountPayments(true)
                .acceptCardPayments(true)
                .shouldDisplayFee(true)
                .initialize();
    }



    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

    private void insertBooking(int id,int type, String title, String timestamp, String amount, String orderId){
        TransactionDatabase db = Room.databaseBuilder(TopUp.this,
                TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
        Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
        TransactionDao transactionDao = db.transactionDao();
        transactionDao.insert(transactions);
    }

    // function to generate a random string of length n
    static String generateTransferRef()
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {

                TopUpModel topUpModel = new TopUpModel(userId,(int)topUpAmountValue,TopUp.this);
                topUpModel.topUpBalance();
                topUpModel.setTopUpListener(new TopUpModel.TopUpListener() {
                    @Override
                    public void onTopUpSucessful(String balance) {
                        Date date = new Date();
                        Timestamp timestamp = new Timestamp(date.getTime());
                        insertBooking(0,5,"Top Up",timestamp.toString(),Integer.toString((int)topUpAmountValue),"");
                        Toast.makeText(TopUp.this, "TopUp Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("balance",balance);
                        setResult(0,intent);
                        increaseWalletBalanceInSharedPref(TopUp.this,Integer.toString(topUpAmountValue));
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(TopUp.this, message, Toast.LENGTH_SHORT).show();
                        loadingDialogUtils.cancelLoadingDialog();
                        finish();
                    }
                });


            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void increaseWalletBalanceInSharedPref(Context context, String amount){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String walletBalance = preferences.getString("walletBalance","0");
        preferences.edit().putString("walletBalance",Integer.toString(Integer.parseInt(walletBalance) + Integer.parseInt(amount))).apply();
    }
}
