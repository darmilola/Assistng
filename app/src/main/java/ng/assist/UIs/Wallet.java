package ng.assist.UIs;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
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

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.WalletAdapter;
import ng.assist.Bills;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.RequestWithdrawal;
import ng.assist.SendMoney;
import ng.assist.TopUp;
import ng.assist.UIs.ViewModel.WalletTransactionsModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class Wallet extends Fragment {


    private static  int TOP_UP_REQ = 0;
    private static  int SEND_MONEY_REQ = 3;
    RecyclerView walletRecyclerview;
    ArrayList<WalletTransactionsModel> walletHistoryList = new ArrayList<>();
    WalletAdapter adapter;
    View view;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    AppBarLayout walletAppbar;
    TextView toolbarTitle;
    LinearLayout walletTransfer,topUp,withdrawals,bills;
    TextView walletBalanceText;
    String mWalletBalance;
    public Wallet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_wallet, container, false);

        initView();
        return  view;
    }

    private void  initView(){
        walletBalanceText = view.findViewById(R.id.wallet_balance_text);
        walletRecyclerview = view.findViewById(R.id.wallet_transcations_recyclerview);
        collapsingToolbarLayout = view.findViewById(R.id.wallet_collapsing_toolbar_layout);
        toolbar = view.findViewById(R.id.wallet_toolbar);
        walletAppbar = view.findViewById(R.id.wallet_app_bar);
        toolbarTitle = view.findViewById(R.id.wallet_toolbar_title);
        walletTransfer = view.findViewById(R.id.wallet_transfer_money);
        topUp = view.findViewById(R.id.top_up_layout);
        withdrawals = view.findViewById(R.id.withdrawals);
        bills = view.findViewById(R.id.bills);
        mWalletBalance = getArguments().getString("walletBalance");
        double amount = Double.parseDouble(mWalletBalance);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        String formatted = formatter.format(amount);
        walletBalanceText.setText("NGN "+formatted);

        initTransactionsHistory();
        topUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), TopUp.class),TOP_UP_REQ);
            }
        });

        walletTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String isVerified =  preferences.getString("isVerified","false");

                if(isVerified.equalsIgnoreCase("true")){
                    startActivityForResult(new Intent(getContext(), SendMoney.class),SEND_MONEY_REQ);
                }
                else{
                    Toast.makeText(getContext(), "You Need to be verified to make withdrawals", Toast.LENGTH_LONG).show();
                }


            }
        });

        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Bills.class));
            }
        });
        withdrawals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RequestWithdrawal.class));
            }
        });
        



        walletAppbar.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {

            boolean isShown = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if(scrollRange + verticalOffset == 0){

                    toolbarTitle.setText("Transactions");
                    isShown = true;
                    return;
                }
                else if(isShown){


                    isShown = false;
                    toolbarTitle.setText("");
                    return;
                }
            }
        });
    }

    private void initTransactionsHistory(){

        WalletTransactionsModel walletTransactionsModel = new WalletTransactionsModel(0);
        WalletTransactionsModel walletTransactionsModel2 = new WalletTransactionsModel(1);
        WalletTransactionsModel walletTransactionsModel3 = new WalletTransactionsModel(2);
        WalletTransactionsModel walletTransactionsModel4 = new WalletTransactionsModel(3);
        WalletTransactionsModel walletTransactionsModel5 = new WalletTransactionsModel(4);
        WalletTransactionsModel walletTransactionsModel6 = new WalletTransactionsModel(5);
        WalletTransactionsModel walletTransactionsModel7 = new WalletTransactionsModel(6);
        WalletTransactionsModel walletTransactionsModel8 = new WalletTransactionsModel(7);

        walletHistoryList.add(walletTransactionsModel);
        walletHistoryList.add(walletTransactionsModel2);
        walletHistoryList.add(walletTransactionsModel3);
        walletHistoryList.add(walletTransactionsModel4);
        walletHistoryList.add(walletTransactionsModel5);
        walletHistoryList.add(walletTransactionsModel6);
        walletHistoryList.add(walletTransactionsModel7);
        walletHistoryList.add(walletTransactionsModel8);

      //  adapter = new WalletAdapter(walletHistoryList,getContext());
        //walletRecyclerview.setAdapter(adapter);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        //walletRecyclerview.setLayoutManager(layoutManager);
        //walletRecyclerview.setAdapter(adapter);

    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.special_activity_background));
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if(requestCode == TOP_UP_REQ && resultCode == 0 && data != null){
           String balance = data.getStringExtra("balance");
           double amount = Double.parseDouble(balance);
           DecimalFormat formatter = new DecimalFormat("#,###.00");
           String formatted = formatter.format(amount);
           walletBalanceText.setText("NGN "+formatted);
       }
        if(requestCode == SEND_MONEY_REQ && resultCode == 0 && data != null){
          ///  String amountSent = data.getStringExtra("amountSent");
           // int amountSentInt = Integer.parseInt(amountSent);

            //DecimalFormat formatter = new DecimalFormat("#,###.00");
            //String formatted = formatter.format(trueBalance);
            //walletBalanceText.setText("NGN "+formatted);

        }
    }

}
