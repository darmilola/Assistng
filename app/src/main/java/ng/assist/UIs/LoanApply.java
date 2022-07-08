package ng.assist.UIs;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.tiper.MaterialSpinner;

import java.util.ArrayList;

import mono.connect.kit.ConnectKit;
import mono.connect.kit.Mono;
import mono.connect.kit.MonoConfiguration;
import ng.assist.LoanApplySuccessListener;
import ng.assist.QuickCreditApplication;
import ng.assist.R;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.LoanHistoryModel;
import ng.assist.UIs.ViewModel.LoanModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanApply extends Fragment implements QuickCreditApplication.AmountReadyListener {

    TextView amount,paybackPeriod,monthlyDue,totalRepayment,serviceFeeText;
    RelativeLayout paybackDropdown,amountDropDown;
    MaterialButton apply;
    View view;
    LoanApplySuccessListener loanApplySuccessListener;
    private ArrayList<String> repaymentList = new ArrayList<>();
    private ArrayList<String> amountList = new ArrayList<>();
    LoanModel loanModel;
    String accountCode = "",userId;
    CheckBox termsAndConditions;
    String loanAmount;
    ListDialog listDialog;
    MaterialButton authenticate;
    ConnectKit widget;
    boolean isAuthSuccess = false;

    @Override
    public void onAmountReady(String Amount) {
         loanAmount = Amount;
         amount.setText(loanAmount);
         populateRepaymentList();
         populateAmountList();
    }



    public LoanApply() {

        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        loanApplySuccessListener = (LoanApplySuccessListener)context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_loan_apply, container, false);


        initView();


        return  view;
    }

    private void initView(){
        QuickCreditApplication activityCompat = (QuickCreditApplication)getActivity();
        activityCompat.setAmountReadyListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        termsAndConditions = view.findViewById(R.id.loan_apply_terms_and_condition);
        amount = view.findViewById(R.id.quick_credit_amount);
        authenticate = view.findViewById(R.id.quick_credit_auth);
        paybackPeriod = view.findViewById(R.id.quick_credit_loan_payback_period);
        monthlyDue = view.findViewById(R.id.quick_credit_monthly_due);
        totalRepayment = view.findViewById(R.id.quick_credit_total_repayment);
        paybackDropdown = view.findViewById(R.id.loan_apply_payback_dropdown);
        apply = view.findViewById(R.id.quick_credit_apply_button);
        userId = preferences.getString("userEmail","");
        serviceFeeText = view.findViewById(R.id.loan_service_fee_text);
        amountDropDown = view.findViewById(R.id.loan_amount_dropdown);
        String reference = generateProductId();


        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (paybackPeriod.getText().toString().trim().equalsIgnoreCase("Select Month")) {
                    Toast.makeText(getContext(), "Please Select Payback Month", Toast.LENGTH_SHORT).show();
                } else if (!termsAndConditions.isChecked()) {
                    Toast.makeText(getContext(), "Please Accept Loan Terms and Conditions", Toast.LENGTH_SHORT).show();
                } else if (!accountCode.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "You are Already Authenticated please Apply", Toast.LENGTH_SHORT).show();
                } else {

                    MonoConfiguration config = new MonoConfiguration.Builder(getContext(),
                            "test_pk_EAY6dsTsaS0u3T1WQDz3", // your publicKey
                            (account) -> {
                                accountCode = account.getCode();
                            }) // onSuccess function
                            .addReference(reference)
                            .addOnEvent((event) -> {
                                if (event.getEventName().equalsIgnoreCase("SUCCESS")) {
                                    isAuthSuccess = true;
                                }
                            }) // onEvent function
                            .addOnClose(() -> {
                                System.out.println("Widget closed.");
                            }) // onClose function
                            .build();
                    widget = Mono.create(config);
                    widget.show();
                }
            }
        });

        paybackDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(repaymentList,getContext());
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String month) {
                         String mMonthlyDue = calcMonthlyDue(month);
                         paybackPeriod.setText(month);
                         monthlyDue.setText(mMonthlyDue);
                         String mTotalRepayment = calcTotalRepayment(month,mMonthlyDue);
                         totalRepayment.setText(mTotalRepayment);
                         serviceFeeText.setText("The Service fee of "+calcServiceFee(Integer.parseInt(getPaybackMonth(paybackPeriod.getText().toString())))+" will be deducted before crediting your wallet");

                    }
                });
            }
        });


        amountDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(amountList,getContext());
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String mAmount) {
                        if(loanAmount == mAmount){

                        }
                        else {
                            loanAmount = mAmount;
                            populateRepaymentList();
                            amount.setText(mAmount);
                            paybackPeriod.setText("Select Month");
                            monthlyDue.setText("");
                            totalRepayment.setText("");
                            serviceFeeText.setText("");
                        }
                    }
                });
            }
        });


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(accountCode.equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "Please Authenticate before Applying", Toast.LENGTH_SHORT).show();
                }
                else if(paybackPeriod.getText().toString().trim().equalsIgnoreCase("Select Month")){
                    Toast.makeText(getContext(), "Please Select Payback Month", Toast.LENGTH_SHORT).show();
                }
                else  if (!termsAndConditions.isChecked()) {
                    Toast.makeText(getContext(), "Please Accept Loan Terms and Conditions", Toast.LENGTH_SHORT).show();
                }
                else {
                    loanModel = new LoanModel(getContext());
                    loanModel.ExchangeToken(accountCode);
                    loanModel.setExchangeTokenListener(new LoanModel.ExchangeTokenListener() {
                        @Override
                        public void onSuccess(String accountToken) {
                            LoanModel loanModel = new LoanModel(getContext(), userId, getPaybackMonth(paybackPeriod.getText().toString()), totalRepayment.getText().toString().trim(), monthlyDue.getText().toString(), accountCode, accountToken, "pendingApproval");
                            loanModel.Apply();
                            loanModel.setLoanApplyListener(new LoanModel.LoanApplyListener() {
                                @Override
                                public void onSuccess() {

                                    loanApplySuccessListener.onSuccess();
                                    LoanHistoryModel loanHistoryModel = new LoanHistoryModel(userId,loanAmount);
                                    loanHistoryModel.createLoan();
                                    loanHistoryModel.setCreateLoanListener(new LoanHistoryModel.createLoanListener() {
                                        @Override
                                        public void onSuccessful() {

                                        }

                                        @Override
                                        public void onFailure(String message) {

                                        }
                                    });
                                }

                                @Override
                                public void onError(String message) {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    // function to generate a random string of length n
    static String generateProductId()
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


    private String calcTotalRepayment(String paybackMonth,String monthlyDue){
        if(paybackMonth.equalsIgnoreCase("1 month")){
            int total = Integer.parseInt(monthlyDue) * 1;
            return Integer.toString(total);
        }
        else if(paybackMonth.equalsIgnoreCase("2 months")){
            int total = Integer.parseInt(monthlyDue) * 2;
            return Integer.toString(total);
        }
        else if(paybackMonth.equalsIgnoreCase("3 months")){
            int total = Integer.parseInt(monthlyDue) * 3;
            return Integer.toString(total);
        }
        else if(paybackMonth.equalsIgnoreCase("4 months")){
            int total = Integer.parseInt(monthlyDue) * 4;
            return Integer.toString(total);
        }
        else if(paybackMonth.equalsIgnoreCase("5 months")){
            int total = Integer.parseInt(monthlyDue) * 5;
            return Integer.toString(total);
        }
        else{
            int total = Integer.parseInt(monthlyDue) * 1;
            return Integer.toString(total);
        }
    }

    private String calcMonthlyDue(String paybackMonth){
        if(paybackMonth.equalsIgnoreCase("1 month")){
            int due = Integer.parseInt(loanAmount);
            return Integer.toString(due);
        }
        else if(paybackMonth.equalsIgnoreCase("2 months")){
            int due = ((int)(Integer.parseInt(loanAmount)/2));
            return Integer.toString(due);
        }
        else if(paybackMonth.equalsIgnoreCase("3 months")){
            int due = ((int)(Integer.parseInt(loanAmount)/3));
            return Integer.toString(due);
        }
        else if(paybackMonth.equalsIgnoreCase("4 months")){
            int due = ((int)(Integer.parseInt(loanAmount)/4));
            return Integer.toString(due);
        }
        else if(paybackMonth.equalsIgnoreCase("5 months")){
            int due = ((int)(Integer.parseInt(loanAmount)/5));
            return Integer.toString(due);
        }
        else{
            int due = Integer.parseInt(loanAmount);
            return Integer.toString(due);
        }
    }

    private String getPaybackMonth(String paybackMonth){
        if(paybackMonth.equalsIgnoreCase("1 month")){
            return "1";
        }
        else if(paybackMonth.equalsIgnoreCase("2 months")){
            return "2";
        }
        else if(paybackMonth.equalsIgnoreCase("3 months")){
            return "3";
        }
        else if(paybackMonth.equalsIgnoreCase("4 months")){
            return "4";
        }
        else if(paybackMonth.equalsIgnoreCase("5 months")){
            return "5";
        }
        else{
            return "1";
        }
    }

    private String calcServiceFee(int paybackMonths){
        int serviceFee = (700 * paybackMonths) + 250;
        return Integer.toString(serviceFee);
    }

    private void populateRepaymentList(){
        repaymentList = new ArrayList<>();
        if(loanAmount.equalsIgnoreCase("10000")){
                repaymentList.add("1 month");
            }
        else  if(loanAmount.equalsIgnoreCase("20000")){
            repaymentList.add("1 month");
            repaymentList.add("2 months");
        }
        else  if(loanAmount.equalsIgnoreCase("30000")){
            repaymentList.add("1 month");
            repaymentList.add("2 months");
            repaymentList.add("3 months");
        }
        else  if(loanAmount.equalsIgnoreCase("40000")){
            repaymentList.add("1 month");
            repaymentList.add("2 months");
            repaymentList.add("3 months");
            repaymentList.add("4 Months");
        }

        else  if(loanAmount.equalsIgnoreCase("50000")){
            repaymentList.add("1 month");
            repaymentList.add("2 months");
            repaymentList.add("3 months");
            repaymentList.add("4 Months");
            repaymentList.add("5 Months");
        }

    }

    private void populateAmountList(){
        amountList.add("10000");
        amountList.add("20000");
        amountList.add("30000");
        amountList.add("40000");
        amountList.add("50000");
    }


}
