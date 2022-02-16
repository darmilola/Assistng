package ng.assist.UIs;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

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

import ng.assist.R;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.LoanModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanApply extends Fragment {

    TextView amount,paybackPeriod,monthlyDue,totalRepayment;
    RelativeLayout paybackDropdown;
    MaterialButton apply;
    View view;
    LoanApplySuccessListener loanApplySuccessListener;
    private ArrayList<String> repaymentList = new ArrayList<>();
    LoanModel loanModel;
    String accountCode,userId;
    CheckBox termsAndConditions;
    String loanAmount;
    ListDialog listDialog;

    public interface LoanApplySuccessListener{
           void onSuccess();
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        loanAmount = preferences.getString("loanAmount","");
        termsAndConditions = view.findViewById(R.id.loan_apply_terms_and_condition);
        amount = view.findViewById(R.id.quick_credit_loan_amount);
        paybackPeriod = view.findViewById(R.id.quick_credit_loan_payback_period);
        monthlyDue = view.findViewById(R.id.quick_credit_monthly_due);
        totalRepayment = view.findViewById(R.id.quick_credit_total_repayment);
        paybackDropdown = view.findViewById(R.id.loan_apply_payback_dropdown);
        apply = view.findViewById(R.id.quick_credit_apply_button);
        accountCode = preferences.getString("accountCode","");
        userId = preferences.getString("userEmail","");
        amount.setText(loanAmount);
        populateRepaymentList();

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
                    }
                });
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!termsAndConditions.isChecked()) {
                    Toast.makeText(getContext(), "Please Accept Loan Terms and Conditions", Toast.LENGTH_SHORT).show();
                }
                else if(paybackPeriod.getText().toString().trim().equalsIgnoreCase("Select Month")){
                    Toast.makeText(getContext(), "Please Select Payback Month", Toast.LENGTH_SHORT).show();
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
            int due = Integer.parseInt(loanAmount) + 1000;
            return Integer.toString(due);
        }
        else if(paybackMonth.equalsIgnoreCase("2 months")){
            int due = ((int)(Integer.parseInt(loanAmount)/2)) + 1000;
            return Integer.toString(due);
        }
        else if(paybackMonth.equalsIgnoreCase("3 months")){
            int due = ((int)(Integer.parseInt(loanAmount)/3)) + 1000;
            return Integer.toString(due);
        }
        else if(paybackMonth.equalsIgnoreCase("4 months")){
            int due = ((int)(Integer.parseInt(loanAmount)/4)) + 1000;
            return Integer.toString(due);
        }
        else if(paybackMonth.equalsIgnoreCase("5 months")){
            int due = ((int)(Integer.parseInt(loanAmount)/5)) + 1000;
            return Integer.toString(due);
        }
        else{
            int due = Integer.parseInt(loanAmount) + 1000;
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

    private void populateRepaymentList(){
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

    @Override
    public void onDestroy() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.edit().remove("loanAmount").apply();
        super.onDestroy();
    }

}
