package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.WithdrawalModel;

public class RequestWithdrawal extends AppCompatActivity {

    LinearLayout historyImage;
    ArrayList<String> bankList = new ArrayList<>();
    ListDialog listDialog;
    EditText amountEdit,accountNameEdit,accountNumberEdit;
    TextView bankNameText;
    MaterialButton requestWithdrawal;
    String walletBalance,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_withdrawal);
        initView();
    }

    private void initView(){
        populateBanks();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        walletBalance = preferences.getString("walletBalance","0");
        userId = preferences.getString("userEmail","");
        amountEdit = findViewById(R.id.withdraw_amount);
        accountNameEdit = findViewById(R.id.withdraw_account_name);
        accountNumberEdit = findViewById(R.id.withdraw_account_number);
        bankNameText = findViewById(R.id.withdraw_bank_name);
        requestWithdrawal = findViewById(R.id.withdraw_request_withdraw_button);

        bankNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(bankList,RequestWithdrawal.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String text) {
                        bankNameText.setText(text);
                    }
                });
            }
        });

        historyImage = findViewById(R.id.withdrawl_history_image);
        historyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RequestWithdrawal.this,WithdrawalHistory.class));
            }
        });

        requestWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountEdit.getText().toString().trim();
                String accountName = accountNameEdit.getText().toString().trim();
                String accountNumber = accountNumberEdit.getText().toString().trim();
                String bankname = bankNameText.getText().toString().trim();

                if(isValidForm()){

                    WithdrawalModel withdrawalModel = new WithdrawalModel(Integer.parseInt(amount),accountName,bankname,accountNumber,userId,RequestWithdrawal.this);
                    withdrawalModel.makeWithdrawal();
                    withdrawalModel.setWithdrawListener(new WithdrawalModel.WithdrawListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(RequestWithdrawal.this, "Your withdraw is been processed", Toast.LENGTH_SHORT).show();
                             finish();
                        }

                        @Override
                        public void onFailure() {

                            Toast.makeText(RequestWithdrawal.this, "Error Occurred please try again", Toast.LENGTH_SHORT).show();
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
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

    private void populateBanks(){
        bankList.add("Access Bank Plc");
        bankList.add("Citibank Nigeria Limited");
        bankList.add("Ecobank Nigeria Plc");
        bankList.add("Fidelity Bank Plc");
        bankList.add("FIRST BANK NIGERIA LIMITED");
        bankList.add("First City Monument Bank Plc");
        bankList.add("Globus Bank Limited");
        bankList.add("Guaranty Trust Bank Plc");
        bankList.add("Heritage Banking Company Ltd");
        bankList.add("Key Stone Bank");
        bankList.add("Polaris Bank");
        bankList.add("Providus Bank");
        bankList.add("Stanbic IBTC Bank Ltd");
        bankList.add("Standard Chartered Bank Nigeria Ltd");
        bankList.add("Sterling Bank Plc");
        bankList.add("SunTrust Bank Nigeria Limited");
        bankList.add("Titan Trust Bank Ltd");
        bankList.add("Union Bank of Nigeria Plc");
        bankList.add("United Bank For Africa Plc");
        bankList.add("Unity Bank Plc");
        bankList.add("Wema Bank Plc");
        bankList.add("Zenith Bank Plc");

    }

    private boolean isValidForm(){
        boolean isValid = true;

        if(TextUtils.isEmpty(amountEdit.getText().toString().trim())){
            amountEdit.setError("Required");
            isValid = false;
            return isValid;
        }
        else if(TextUtils.isEmpty(accountNameEdit.getText().toString().trim())){
            accountNumberEdit.setError("Required");
            isValid = false;
            return isValid;
        }
        if(TextUtils.isEmpty(bankNameText.getText().toString().trim())){
            bankNameText.setError("Required");
            isValid = false;
            return isValid;
        }
        if(TextUtils.isEmpty(accountNumberEdit.getText().toString().trim())){
            accountNumberEdit.setError("Required");
            isValid = false;
            return isValid;
        }
        if (Integer.parseInt(walletBalance) < Integer.parseInt(amountEdit.getText().toString().trim())) {
            Toast.makeText(RequestWithdrawal.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
            isValid = false;
            amountEdit.setError("");
            return isValid;
        }
        return  isValid;


    }
}
