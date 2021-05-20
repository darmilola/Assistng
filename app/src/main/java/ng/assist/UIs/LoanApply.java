package ng.assist.UIs;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.tiper.MaterialSpinner;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ng.assist.R;
import ng.assist.UIs.model.StandingOrder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanApply extends Fragment {


    View view;
    MaterialSpinner materialSpinner;
    ArrayAdapter<CharSequence> adapter;
    MaterialButton apply;
    StandingOrder standingOrder;


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

    private void initView(){
        apply = view.findViewById(R.id.loan_apply_button);
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.loan_payback_period, android.R.layout.simple_spinner_item);
        materialSpinner = view.findViewById(R.id.loan_apply_payback_period);
        materialSpinner.setAdapter(adapter);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                standingOrder = new StandingOrder(getContext(),"Damilola Akinterinwa","damilolaakinterinwa@gmail.com","08102853533","058","0178871016","2000","19/04/2021","19/06/2021");
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
                        Toast.makeText(getContext(),"ready  "+ message, Toast.LENGTH_LONG).show();
                        Log.e("onMandatePrintReady: ",message);
                    }
                });
                standingOrder.ProcessStandingOrder();

            }
        });
    }
}
