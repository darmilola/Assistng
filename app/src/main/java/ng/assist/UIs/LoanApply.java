package ng.assist.UIs;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.tiper.MaterialSpinner;

import java.util.ArrayList;

import ng.assist.R;
import ng.assist.UIs.Utils.ListDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanApply extends Fragment {

    TextView amount,paybackPeriod,monthlyDue,totalRepayment;
    MaterialButton apply;
    View view;
    LoanApplySuccessListener loanApplySuccessListener;
    private ArrayList<String> amountList = new ArrayList<>();
    //StandingOrder standingOrder;

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

        return  view;
    }

    private void initView(){
        amount = view.findViewById(R.id.quick_credit_loan_amount);
        paybackPeriod = view.findViewById(R.id.quick_credit_loan_payback_period);
        monthlyDue = view.findViewById(R.id.quick_credit_monthly_due);
        totalRepayment = view.findViewById(R.id.quick_credit_loan_payback_period);
        apply = view.findViewById(R.id.quick_credit_apply_button);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               loanApplySuccessListener.onSuccess();
            }
        });

    }

}
