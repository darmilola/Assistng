package ng.assist.UIs;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import mono.connect.kit.ConnectKit;
import mono.connect.kit.Mono;
import mono.connect.kit.MonoConfiguration;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.UIs.Utils.ListDialog;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;


import java.util.ArrayList;


public class QuickCreditsAmount extends Fragment {


    View view;
    TextView selectAmount;
    private ArrayList<String> amountList = new ArrayList<>();
    MaterialButton authenticateWithMono;
    MonoAuthenticationListener authenticationListener;
    ConnectKit widget;
    boolean isAuthSuccess = false;
    public QuickCreditsAmount() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        authenticationListener = (MonoAuthenticationListener) context;
    }


    public interface MonoAuthenticationListener{
          void onAuthSuccessful();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_quick_credits_amount, container, false);
        initView();
        return  view;
    }


    private void initView() {
        initAmount();
        authenticateWithMono = view.findViewById(R.id.loan_authenticate_button);
        selectAmount = view.findViewById(R.id.quick_credits_select_amount);
        selectAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListDialog listDialog = new ListDialog(amountList,getContext());
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String amount) {
                          selectAmount.setText(amount);
                    }
                });

            }
        });

        authenticateWithMono.setOnClickListener(new View.OnClickListener() {
            String reference = generateProductId();
            @Override
            public void onClick(View view) {

                if(selectAmount.getText().toString().trim().equalsIgnoreCase("")){
                    selectAmount.setError("Required");
                }

               else if(authenticateWithMono.getText().toString().equalsIgnoreCase("Next") && !selectAmount.getText().toString().trim().equalsIgnoreCase("")){
                    authenticationListener.onAuthSuccessful();
                }

                else{

                    MonoConfiguration config = new MonoConfiguration.Builder(getContext(),
                            "test_pk_EAY6dsTsaS0u3T1WQDz3", // your publicKey
                            (account) -> {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                preferences.edit().putString("loanAmount",selectAmount.getText().toString()).apply();
                                preferences.edit().putString("accountCode",account.getCode()).apply();
                                Toast.makeText(getContext(), account.getCode(), Toast.LENGTH_SHORT).show();
                                System.out.println("Successfully linked account. Code: " + account.getCode());
                                authenticateWithMono.setText("Next");
                            }) // onSuccess function
                            .addReference(reference)
                            .addOnEvent((event) -> {
                                if(event.getEventName().equalsIgnoreCase("SUCCESS")){
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


    }

    private void initAmount(){
        amountList = new ArrayList<>();
        amountList.add("10,000");
        amountList.add("20,000");
        amountList.add("30,000");
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

}
