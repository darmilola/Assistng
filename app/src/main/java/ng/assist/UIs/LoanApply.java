package ng.assist.UIs;


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
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.tiper.MaterialSpinner;

import ng.assist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanApply extends Fragment {


    View view;
    MaterialSpinner materialSpinner;
    ArrayAdapter<CharSequence> adapter;
    MaterialButton apply;
    //StandingOrder standingOrder;


    public LoanApply() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_loan_apply, container, false);
        initView();
        return  view;
    }

    private void initView() {
        apply = view.findViewById(R.id.loan_apply_button);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.loan_payback_period, android.R.layout.simple_spinner_item);
        materialSpinner = view.findViewById(R.id.loan_apply_payback_period);
        materialSpinner.setAdapter(adapter);

     /*   apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                standingOrder = new StandingOrder(getContext(),"Damilola Akinterinwa","damilolaakinterinwa@gmail.com","08102853533","058","0178871016","2000","19/07/2021","19/09/2021");
                standingOrder.setStandingOrderGenerationListener(new StandingOrder.StandingOrderGenerationListener() {
                    @Override
                    public void onInitialGenerationCompleted(String statusCode, String requestId, String mandateId, String status) {

                    }

                    @Override
                    public void onGenerationFailure(String errorMessage) {

                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRequestAuthorizationCompleted(String message) {

                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthorizationValidated(String message) {

                    }

                    @Override
                    public void onAuthorizationValidationError(String errorMessage) {

                    }

                    @Override
                    public void onMandatePrintReady(String message) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://jamaicanmuscle.000webhostapp.com/print_mandate.php?html_string =" + Html.escapeHtml(message)));
                     try {
                         Log.e("onMandatePrintReady: ", Html.escapeHtml("http://jamaicanmuscle.000webhostapp.com/print_mandate.php?html_string =" + message).replaceAll(" \" "," \' "));
                     }catch (Exception e){

                     }
                        startActivity(browserIntent);
                    }
                });
                standingOrder.ProcessStandingOrder();

            }
        });
    }*/
    }
}
