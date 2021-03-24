package ng.assist.UIs;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.WalletAdapter;
import ng.assist.R;
import ng.assist.SendMoney;

/**
 * A simple {@link Fragment} subclass.
 */
public class Wallet extends Fragment {


    RecyclerView walletRecyclerview;
    ArrayList<String> walletHistoryList = new ArrayList<>();
    WalletAdapter adapter;
    View view;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    AppBarLayout walletAppbar;
    TextView toolbarTitle;
    LinearLayout walletTransfer;

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
        walletRecyclerview = view.findViewById(R.id.wallet_transcations_recyclerview);
        collapsingToolbarLayout = view.findViewById(R.id.wallet_collapsing_toolbar_layout);
        toolbar = view.findViewById(R.id.wallet_toolbar);
        walletAppbar = view.findViewById(R.id.wallet_app_bar);
        toolbarTitle = view.findViewById(R.id.wallet_toolbar_title);
        walletTransfer = view.findViewById(R.id.wallet_transfer_money);

        walletTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SendMoney.class));
            }
        });
        for(int  i = 0; i < 15; i++){
            walletHistoryList.add("");
        }

        adapter = new WalletAdapter(walletHistoryList,getContext());
        walletRecyclerview.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        walletRecyclerview.setLayoutManager(layoutManager);
        walletRecyclerview.setAdapter(adapter);


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

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.transparent));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

}
