package ng.assist.UIs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

import androidx.room.Room;
import ng.assist.AccomodationBooking;
import ng.assist.R;
import ng.assist.SendMoney;
import ng.assist.UIs.ViewModel.SendMoneyModel;
import ng.assist.UIs.ViewModel.Transactions;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendMoneySuccess extends Fragment {
    View view;
    TextView amountSent, successText;
    MaterialButton backHome;
    public SendMoneySuccess() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_send_money_success, container, false);
        initView();
        return view;
    }
    private void initView(){

        backHome = view.findViewById(R.id.send_money_success_home_button);
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                //intent.putExtra("amountSent",amount);
                getActivity().setResult(0);
                getActivity().finish();
                preferences.edit().remove("sendMoneyFirstname").apply();
                preferences.edit().remove("sendMoneyLastname").apply();
                preferences.edit().remove("sendMoneyEmail").apply();
                preferences.edit().remove("sendMoneyImageUrl").apply();
            }
        });
        amountSent = view.findViewById(R.id.send_money_success_amount);
        successText = view.findViewById(R.id.send_money_success_text);

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String amount = preferences.getString("sendMoneyAmount", "");
        String firstname = preferences.getString("sendMoneyFirstname","");
        String lastname = preferences.getString("sendMoneyLastname","");
         double mAmount = Double.parseDouble(amount);
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            String formatted = formatter.format(mAmount);
            amountSent.setText("NGN " +formatted);
            successText.setText("Has been sent to "+ firstname+" "+lastname);

        String mWalletBalance = preferences.getString("walletBalance","");
        //String amount = preferences.getString("sendMoneyAmount", "");
        int formerBalance = Integer.parseInt(mWalletBalance);
        int trueBalance = formerBalance - Integer.parseInt(amount);
        preferences.edit().remove("walletBalance").apply();
        preferences.edit().putString("walletBalance",Integer.toString(trueBalance)).apply();

    }


}
