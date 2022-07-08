package ng.assist.UIs;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import ng.assist.AccomodationAdmin;
import ng.assist.AccomodationBooking;
import ng.assist.BusinessProviderVerification;
import ng.assist.ChatActivity;
import ng.assist.EcommerceDashboard;
import ng.assist.EstateListingDashboard;
import ng.assist.MainActivity;
import ng.assist.R;
import ng.assist.SendMoney;
import ng.assist.ServiceProviderDashboard;
import ng.assist.ServiceProviderVerifications;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.AccountModel;
import ng.assist.UserAccomodationBooking;
import ng.assist.VerificationDashBoard;
import ng.assist.WelcomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragments extends Fragment {


    View view;
    LinearLayout dashboardLayout,switchAccount,userVerification,accomodationAdmin;
    LinearLayout aboutUs,rateUs,logOut,provideAService;
    ArrayList<String> accountList = new ArrayList<>();
    ArrayList<String> accountTypeList = new ArrayList<>();
    CircleImageView circleImageView;
    TextView usernameField,faq,whyAssist,contactUs;
    String accountType,verificationType,accountRole;
    SharedPreferences preferences;
    ImageView isVerifiedBadge;


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
        faq = view.findViewById(R.id.faqs);
        whyAssist = view.findViewById(R.id.why_assist);
        contactUs = view.findViewById(R.id.contact_us);
        accomodationAdmin = view.findViewById(R.id.account_accomodation_admin);
        isVerifiedBadge = view.findViewById(R.id.verification_badge);
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


        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://assist.ng/faqs.html"));
                startActivity(i);
            }
        });

        whyAssist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://assist.ng"));
                startActivity(i);

            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://assist.ng/faqs.html"));
                startActivity(i);
            }
        });


        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userImage =  preferences.getString("imageUrl","");
        String firstname =  preferences.getString("firstname","");
        String lastname =  preferences.getString("lastname","");
        accountType =  preferences.getString("accountType","");
        accountRole = preferences.getString("role","none");
        verificationType = preferences.getString("verificationType","none");

        usernameField.setText(firstname+" "+lastname);
        Glide.with(this)
                .load(userImage)
                .placeholder(R.drawable.profileplaceholder)
                .error(R.drawable.profileplaceholder)
                .into(circleImageView);

        initAccount();


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              logOut();
            }
        });


        accomodationAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AccomodationAdmin.class));
            }
        });

        userVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog listDialog = new ListDialog(accountTypeList, getContext());
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String item) {
                        if(item.equalsIgnoreCase("Personal Account")){
                             showPersonalVerificationAlert();
                        }
                        else{
                             showBusinessVerificationAlert();
                        }
                    }
                });

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
                accountType =  preferences.getString("accountType","");
                if(accountType.equalsIgnoreCase("Physical Products"))startActivity(new Intent(getContext(),EcommerceDashboard.class));
                if(accountType.equalsIgnoreCase("Home Listing"))startActivity(new Intent(getContext(), EstateListingDashboard.class));
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
                                preferences.edit().remove("accountType").apply();
                                preferences.edit().putString("accountType",item).apply();
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
        accountList.add("Home Listing");
        accountList.add("Physical Products");

        accountTypeList.add("Personal Account");
        accountTypeList.add("Business Account");

       if(!isVerified()){
            dashboardLayout.setVisibility(View.GONE);
            switchAccount.setVisibility(View.GONE);
            userVerification.setVisibility(View.VISIBLE);
            provideAService.setVisibility(View.GONE);
            accomodationAdmin.setVisibility(View.GONE);
            isVerifiedBadge.setVisibility(View.VISIBLE);
        }

        if(accountRole.equalsIgnoreCase("admin")){
            authAdminUser();
        }
        if(verificationType.equalsIgnoreCase("personal")){
            authPersonalUser();
        }
        if(verificationType.equalsIgnoreCase("business")){
            authBusinessUser();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.white));
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

    private boolean isVerified(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String isVerified =  preferences.getString("isVerified","false");
        if(isVerified.equalsIgnoreCase("true")){
            return true;
        }
        else{
            return false;
        }
    }

    private void authAdminUser(){
        dashboardLayout.setVisibility(View.GONE);
        switchAccount.setVisibility(View.GONE);
        userVerification.setVisibility(View.GONE);
        provideAService.setVisibility(View.GONE);
        accomodationAdmin.setVisibility(View.VISIBLE);
    }

    private void authBusinessUser(){
        userVerification.setVisibility(View.GONE);
        provideAService.setVisibility(View.VISIBLE);
        accomodationAdmin.setVisibility(View.GONE);
        accomodationAdmin.setVisibility(View.GONE);
        dashboardLayout.setVisibility(View.VISIBLE);
        switchAccount.setVisibility(View.VISIBLE);
    }

    private void authPersonalUser(){
        userVerification.setVisibility(View.GONE);
        switchAccount.setVisibility(View.GONE);
        dashboardLayout.setVisibility(View.GONE);
        accomodationAdmin.setVisibility(View.GONE);
        accomodationAdmin.setVisibility(View.GONE);
        dashboardLayout.setVisibility(View.GONE);
    }


    private void showPersonalVerificationAlert(){
        new AlertDialog.Builder(getContext())
                .setTitle("Personal Verification")
                .setMessage("Personal Verification gives you access to provide a service feature of the App")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(getContext(),ServiceProviderVerifications.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    private void showBusinessVerificationAlert(){
        new AlertDialog.Builder(getContext())
                .setTitle("Business Verification")
                .setMessage("Business Verification gives you access to all business feature of the App")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(getContext(),BusinessProviderVerification.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

}

