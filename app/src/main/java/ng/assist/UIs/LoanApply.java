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
        totalRepayment = view.findViewById(R.id.quick_credit_loan_payback_period);
        paybackDropdown = view.findViewById(R.id.loan_apply_payback_dropdown);
        apply = view.findViewById(R.id.quick_credit_apply_button);
        accountCode = preferences.getString("accountCode","");
        userId = preferences.getString("userEmail","");
        populateRepaymentList();

        paybackDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(repaymentList,getContext());
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {

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
                            LoanModel loanModel = new LoanModel(getContext(), userId, "3", "20000", "8000", accountCode, accountToken, "pendingApproval");
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

    private void populateRepaymentList(){
        if(loanAmount.equalsIgnoreCase("10,000")){
                repaymentList.add("1 month");
            }
        else  if(loanAmount.equalsIgnoreCase("20,000")){
            repaymentList.add("1 month");
            repaymentList.add("2 months");
        }
        else  if(loanAmount.equalsIgnoreCase("30,000")){
            repaymentList.add("1 month");
            repaymentList.add("2 months");
            repaymentList.add("3 months");
        }
        else  if(loanAmount.equalsIgnoreCase("40,000")){
            repaymentList.add("1 month");
            repaymentList.add("2 months");
            repaymentList.add("3 months");
            repaymentList.add("4 Months");
        }

        else  if(loanAmount.equalsIgnoreCase("50,000")){
            repaymentList.add("1 month");
            repaymentList.add("2 months");
            repaymentList.add("3 months");
            repaymentList.add("4 Months");
            repaymentList.add("5 Months");
        }

    }

}
