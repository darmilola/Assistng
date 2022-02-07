package ng.assist.UIs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import ng.assist.Accomodation;
import ng.assist.BuildLocation;
import ng.assist.GroceryLanding;
import ng.assist.QuickCreditApplication;
import ng.assist.R;
import ng.assist.ServiceProviderVerifications;
import ng.assist.ServicesLanding;
import ng.assist.TopUp;
import ng.assist.Transportation;


public class HomeFragment extends Fragment {


    private static  int TOP_UP_REQ = 1;
    View view;
    LinearLayout transportationLayout;
    LinearLayout groceryLayout;
    LinearLayout accomodationLayout;
    LinearLayout homeServicesLayout;
    LinearLayout applyForQucikCredit;
    TextView userFirstname,userWalletBalance;
    String mFirstname,mWalletBalance;
    LinearLayout topUp;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        return  view;
    }

    private void initView(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String status = preferences.getString("verificationStatus","notVerified");
        topUp = view.findViewById(R.id.home_topup_layout);
        transportationLayout = view.findViewById(R.id.home_transportation_layout);
        groceryLayout = view.findViewById(R.id.home_grocery_layout);
        accomodationLayout = view.findViewById(R.id.home_accomodation_layout);
        homeServicesLayout = view.findViewById(R.id.home_services_layout);
        applyForQucikCredit = view.findViewById(R.id.quick_credit_create_application_layout);
        userFirstname = view.findViewById(R.id.home_user_firstname);
        userWalletBalance = view.findViewById(R.id.home_user_wallet_balance);
        mFirstname = getArguments().getString("firstname");
        mWalletBalance = getArguments().getString("walletBalance");
        userFirstname.setText("Hi "+mFirstname+"!");
        double amount = Double.parseDouble(mWalletBalance);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String formatted = formatter.format(amount);
        userWalletBalance.setText(formatted);

        topUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), TopUp.class),TOP_UP_REQ);
            }
        });

        applyForQucikCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("notVerified")){
                    Toast.makeText(getContext(), "Please Get Verified To Apply", Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivity(new Intent(getContext(), QuickCreditApplication.class));
                }
            }
        });
        transportationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BuildLocation.class));
            }
        });

        homeServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ServicesLanding.class));
            }
        });
        groceryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GroceryLanding.class));
            }
        });
        accomodationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Accomodation.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TOP_UP_REQ && resultCode == 0 && data != null){
            String balance = data.getStringExtra("balance");
            double amount = Double.parseDouble(balance);
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            String formatted = formatter.format(amount);
            userWalletBalance.setText(formatted);
        }
    }


    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.special_activity_background));
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }
    }



}
