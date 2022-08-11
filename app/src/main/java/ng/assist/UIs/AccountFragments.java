package ng.assist.UIs;


import static android.app.Activity.RESULT_OK;

import android.accounts.Account;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
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


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.assist.AccomodationAdmin;
import ng.assist.BusinessProviderVerification;
import ng.assist.EcommerceDashboard;
import ng.assist.EstateListingDashboard;
import ng.assist.ProfileImageCrop;
import ng.assist.R;
import ng.assist.ServiceProviderDashboard;
import ng.assist.ServiceProviderVerifications;
import ng.assist.SignUpWithEmail;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.AccountModel;
import ng.assist.UIs.ViewModel.SignupModel;
import ng.assist.WelcomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragments extends Fragment {

    static int PICK_IMAGE = 1;
    static int CROP_IMAGE = 2;
    View view;
    LinearLayout dashboardLayout,switchAccount,userVerification,accomodationAdmin;
    LinearLayout aboutUs,rateUs,logOut,provideAService;
    ArrayList<String> accountList = new ArrayList<>();
    ArrayList<String> accountTypeList = new ArrayList<>();
    CircleImageView circleImageView;
    TextView usernameField, termsAndCondition, privacyPolicy, quickCreditTandC,currentDashboard;
    String accountType,verificationType,accountRole;
    SharedPreferences preferences;
    ImageView isVerifiedBadge;
    LinearLayout editProfilePicture;
    String userEmail;
    String mProfileImage;


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
        termsAndCondition = view.findViewById(R.id.termsAndCondition);
        privacyPolicy = view.findViewById(R.id.privacyPolicy);
        quickCreditTandC = view.findViewById(R.id.quickCreditTandC);
        accomodationAdmin = view.findViewById(R.id.account_accomodation_admin);
        isVerifiedBadge = view.findViewById(R.id.verification_badge);
        userVerification = view.findViewById(R.id.account_users_verification);
        usernameField = view.findViewById(R.id.account_name);
        logOut = view.findViewById(R.id.account_log_out);
        circleImageView = view.findViewById(R.id.account_profile_image);
        dashboardLayout = view.findViewById(R.id.account_users_dashboard);
        aboutUs = view.findViewById(R.id.account_about_us);
        rateUs = view.findViewById(R.id.account_rate_us);
        currentDashboard = view.findViewById(R.id.current_dashboard);
        editProfilePicture = view.findViewById(R.id.edit_profile_picture);
        switchAccount = view.findViewById(R.id.account_switch_account);
        provideAService = view.findViewById(R.id.account_provide_a_service);
        dashboardLayout.setVisibility(View.GONE);



        termsAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://assist.ng/terms.php"));
                startActivity(i);
            }
        });

        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE);
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://assist.ng/privacypolicy.php"));
                startActivity(i);

            }
        });

        quickCreditTandC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://assist.ng/credit.php"));
                startActivity(i);
            }
        });


        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userImage =  preferences.getString("imageUrl","");
        String firstname =  preferences.getString("firstname","");
        String lastname =  preferences.getString("lastname","");
        accountType =  preferences.getString("accountType","");
        accountRole = preferences.getString("role","none");
        userEmail = preferences.getString("userEmail", "");
        verificationType = preferences.getString("verificationType","none");

        currentDashboard.setText(accountType);

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
                if(accountType.equalsIgnoreCase("Product Dashboard"))startActivity(new Intent(getContext(),EcommerceDashboard.class));
                if(accountType.equalsIgnoreCase("House List Dashboard"))startActivity(new Intent(getContext(), EstateListingDashboard.class));
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
                                currentDashboard.setText(item);
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
        accountList.add("House List Dashboard");
        accountList.add("Product Dashboard");

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
        //accomodationAdmin.setVisibility(View.GONE);
        //accomodationAdmin.setVisibility(View.GONE);
        dashboardLayout.setVisibility(View.VISIBLE);
        switchAccount.setVisibility(View.VISIBLE);
    }

    private void authPersonalUser(){
        userVerification.setVisibility(View.GONE);
        switchAccount.setVisibility(View.GONE);
        dashboardLayout.setVisibility(View.GONE);
        //accomodationAdmin.setVisibility(View.GONE);
        //accomodationAdmin.setVisibility(View.GONE);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            String selectedImageString = data.getData().toString();
            Intent cropIntent = new Intent(getContext(), ProfileImageCrop.class);
            cropIntent.putExtra("image",selectedImageString);
            startActivityForResult(cropIntent,CROP_IMAGE);
        }

        if (requestCode == CROP_IMAGE && resultCode == 2) {
            byte[] byteArray = data.getByteArrayExtra("croppedImage");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            mProfileImage = getEncodedImage(bmp);
           // circleImageView.setImageBitmap(bmp);

            SignupModel signupModel = new SignupModel(mProfileImage,userEmail,getContext());
            signupModel.UpdateProfileImage();
            signupModel.setSignupListener(new SignupModel.SignupListener() {
                @Override
                public void isSuccessful(String message) {
                    circleImageView.setImageBitmap(bmp);
                }

                @Override
                public void isFailed(String message) {
                    Toast.makeText(getContext(), "Error Occurred Please try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    public static String getEncodedImage(Bitmap bitmap){
        final int MAX_IMAGE_SIZE = 500 * 1024; // max final file size in kilobytes
        byte[] bmpPicByteArray;

        //Bitmap scBitmap  = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do{
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
            Log.d("compressBitmap", "Size: " + streamLength/1024+" kb");
        }while (streamLength >= MAX_IMAGE_SIZE);

        String encodedImage = Base64.encodeToString(bmpPicByteArray, Base64.DEFAULT);
        return encodedImage;

    }

}

