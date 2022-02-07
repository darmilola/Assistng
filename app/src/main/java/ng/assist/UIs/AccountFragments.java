package ng.assist.UIs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.assist.ChatActivity;
import ng.assist.EcommerceDashboard;
import ng.assist.EstateListingDashboard;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.ServiceProviderDashboard;
import ng.assist.ServiceProviderVerifications;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.AccountModel;
import ng.assist.VerificationDashBoard;
import ng.assist.WelcomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragments extends Fragment {


    View view;
    LinearLayout dashboardLayout,switchAccount,userVerification;
    LinearLayout aboutUs,rateUs,logOut,provideAService;
    ArrayList<String> accountList = new ArrayList<>();
    CircleImageView circleImageView;
    TextView usernameField;
    String accountType;
    SharedPreferences preferences;


    public AccountFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account_fragments, container, false);
        initView();
        return view;
    }

    private void initView() {
        userVerification = view.findViewById(R.id.account_users_verification);
        usernameField = view.findViewById(R.id.account_name);
        logOut = view.findViewById(R.id.account_log_out);
        circleImageView = view.findViewById(R.id.account_profile_image);
        dashboardLayout = view.findViewById(R.id.account_users_dashboard);
        aboutUs = view.findViewById(R.id.account_about_us);
        rateUs = view.findViewById(R.id.account_rate_us);
        switchAccount = view.findViewById(R.id.account_switch_account);
        provideAService = view.findViewById(R.id.account_provide_a_service);
        dashboardLayout.setVisibility(View.GONE);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userImage =  preferences.getString("imageUrl","");
        String firstname =  preferences.getString("firstname","");
        String lastname =  preferences.getString("lastname","");
        String role =  preferences.getString("role","");
        accountType =  preferences.getString("accountType","");

        usernameField.setText(firstname+" "+lastname);
        Glide.with(this)
                .load(userImage)
                .placeholder(R.drawable.profileplaceholder)
                .error(R.drawable.profileplaceholder)
                .into(circleImageView);

        initAccount();

        if(role.equalsIgnoreCase("admin")){
           dashboardLayout.setVisibility(View.VISIBLE);
           switchAccount.setVisibility(View.VISIBLE);
        }
        else{
            dashboardLayout.setVisibility(View.GONE);
            switchAccount.setVisibility(View.GONE);
        }

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              logOut();
            }
        });

        userVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),ServiceProviderVerifications.class));
            }
        });


        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getContext(), ChatActivity.class));
            }
        });

        provideAService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),ServiceProviderDashboard.class));
            }
        });

        dashboardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountType.equalsIgnoreCase("Eccommerce"))startActivity(new Intent(getContext(),EcommerceDashboard.class));
                if(accountType.equalsIgnoreCase("House Agent"))startActivity(new Intent(getContext(), EstateListingDashboard.class));

            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        switchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog listDialog = new ListDialog(accountList, getContext());
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String item) {
                        AccountModel accountModel = new AccountModel(getContext(),item);
                        accountModel.SwitchAccount();
                        accountModel.setAccountSwitchListener(new AccountModel.AccountSwitchListener() {
                            @Override
                            public void onAccountSwitched() {
                                accountType = item;
                                Toast.makeText(getContext(), "Account Switched to "+item, Toast.LENGTH_SHORT).show();
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                preferences.edit().putString("accountType",item).apply();
                                if(item.equalsIgnoreCase("Normal User")){

                                    dashboardLayout.setVisibility(View.GONE);
                                }
                                else{

                                    dashboardLayout.setVisibility(View.VISIBLE);


                                }
                            }
                            @Override
                            public void onError(String message) {
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    private void initAccount(){
        accountList = new ArrayList<>();
        accountList.add("House Agent");
        accountList.add("Eccommerce");

        if(accountType.equalsIgnoreCase("Service Provider")){

        }
        else if(accountType.equalsIgnoreCase("House Agent")){

            dashboardLayout.setVisibility(View.VISIBLE);
        }
        else if(accountType.equalsIgnoreCase("Eccommerce")){

            dashboardLayout.setVisibility(View.VISIBLE);


        }
        else if(accountType.equalsIgnoreCase("Normal User")){
            dashboardLayout.setVisibility(View.GONE);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.special_activity_background));
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(),R.color.special_activity_background));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void logOut(){
        GoogleSignInClient mSignInClient;
        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile()
                        .build();
        mSignInClient = GoogleSignIn.getClient(getContext(), options);
        mSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        preferences.edit().remove("userEmail").apply();
                        startActivity(new Intent(getContext(),WelcomeActivity.class));
                        getActivity().finish();
                    }
                });
           }


}

