package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import ng.assist.UIs.Utils.LoadingDialogUtils;
import ng.assist.UIs.ViewModel.TopUpModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.tenbis.library.consts.CardType;
import com.tenbis.library.listeners.OnCreditCardStateChanged;
import com.tenbis.library.models.CreditCard;
import com.tenbis.library.views.CompactCreditCardInput;

import org.jetbrains.annotations.NotNull;

public class TopUp extends AppCompatActivity {

    private String mCardNumber = "4084084084084081";
    private int mExpiryMonth = 11;
    private int mExpiryYear = 22;
    private String mCvv = "408";
    private Card card;
    private boolean isCardValid = false;
    MaterialButton topUpButton;
    CompactCreditCardInput compactCreditCardInput;
    EditText topUpAmount;
    int topUpAmountValue;
    LoadingDialogUtils loadingDialogUtils;
    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaystackSdk.initialize(this);
        setContentView(R.layout.activity_top_up);
        initView();
    }

    private void initView(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TopUp.this);
        userId = preferences.getString("userEmail","");

        loadingDialogUtils = new LoadingDialogUtils(TopUp.this);
        compactCreditCardInput = findViewById(R.id.compact_credit_card_input);
        topUpAmount = findViewById(R.id.top_up_amount_text);
        topUpButton = findViewById(R.id.topup_charge_card_button);
        topUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeCard();
            }
        });

        compactCreditCardInput.addOnCreditCardStateChangedListener(new OnCreditCardStateChanged() {
            @Override
            public void onCreditCardValid(@NotNull CreditCard creditCard) {
                isCardValid = true;
                mCardNumber = creditCard.getCardNumber();
                mCvv = creditCard.getCvv();
                mExpiryMonth = creditCard.getExpiryMonth();
                mExpiryYear = creditCard.getExpiryYear();
            }

            @Override
            public void onCreditCardNumberValid(@NotNull String s) {

            }

            @Override
            public void onCreditCardExpirationDateValid(int i, int i1) {

            }

            @Override
            public void onCreditCardCvvValid(@NotNull String s) {

            }

            @Override
            public void onCreditCardTypeFound(@NotNull CardType cardType) {

            }

            @Override
            public void onInvalidCardTyped() {
               isCardValid = false;
                Toast.makeText(TopUp.this, "Card not valid", Toast.LENGTH_SHORT).show();
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
        if (card.isValid() && isCardValid) {
            performCharge();
        } else {
            Toast.makeText(this, "Error occurred please try again", Toast.LENGTH_SHORT).show();
        }
    }

    public void performCharge(){
         //create a Charge object
        loadingDialogUtils.showLoadingDialog("Processing...");
        Charge charge = new Charge();
        charge.setAmount(topUpAmountValue*100);
        charge.setCurrency("NGN");
        charge.setEmail(userId);
        charge.setCard(card); //sets the card to charge

        PaystackSdk.chargeCard(TopUp.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                TopUpModel topUpModel = new TopUpModel(userId,topUpAmountValue,TopUp.this);
                topUpModel.topUpBalance();
                topUpModel.setTopUpListener(new TopUpModel.TopUpListener() {
                    @Override
                    public void onTopUpSucessful(String balance) {
                        Toast.makeText(TopUp.this, "TopUp Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("balance",balance);
                        setResult(0,intent);
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

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
                Toast.makeText(TopUp.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

        });
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
}
