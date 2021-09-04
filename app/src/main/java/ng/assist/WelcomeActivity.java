package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import ng.assist.UIs.Utils.NetworkUtils;
import ng.assist.UIs.ViewModel.SignupModel;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class WelcomeActivity extends AppCompatActivity {

    LinearLayout googleSignInLayout,emailSigninLayout;
    private static final int GC_SIGN_IN = 1;
    TextView privacyTerms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
    }

    private void initView(){
        privacyTerms = findViewById(R.id.assist_privacy_terms);
        googleSignInLayout = findViewById(R.id.signin_with_google_layout);
        emailSigninLayout = findViewById(R.id.signin_with_email_layout);

        googleSignInLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGoogleSignIn();
            }
        });


        emailSigninLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,ConnectWithEmail.class));
            }
        });

        // set up spanned string with url
        SpannableString termsOfUseString = new SpannableString("By Creating your Assist account you agree to our Terms of Use and Privacy Policy");
        String privacyUrl = "https://developer.android.com";
        termsOfUseString.setSpan(new URLSpan(privacyUrl), 49, 61, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsOfUseString.setSpan(new URLSpan(privacyUrl), 66, 80, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsOfUseString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.green)), 49, 61, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsOfUseString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.green)), 66, 80, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        privacyTerms.setText(termsOfUseString);
        privacyTerms.setMovementMethod(LinkMovementMethod.getInstance());

    }


    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    private void startGoogleSignIn(){

        GoogleSignInClient mSignInClient;
        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile()
                        .build();
        mSignInClient = GoogleSignIn.getClient(this, options);
        Intent intent = mSignInClient.getSignInIntent();
        startActivityForResult(intent, GC_SIGN_IN);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GC_SIGN_IN){
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                GoogleSignInAccount acct = task.getResult();
                handleSignInResult(acct);
            } else {

                Toast.makeText(this, "Error Signing in Please try again", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void handleSignInResult(GoogleSignInAccount account){
        String firstname = account.getGivenName();
        String lastname = account.getFamilyName();
        String userEmail = account.getEmail();
        String userPhotoUrl = String.valueOf(account.getPhotoUrl());

        SignupModel signupModel = new SignupModel(firstname,lastname,userEmail,userPhotoUrl,WelcomeActivity.this);
        if(!new NetworkUtils(WelcomeActivity.this).isNetworkAvailable()) {
            Toast.makeText(WelcomeActivity.this, "No Network", Toast.LENGTH_SHORT).show();
        }
        else {
            signupModel.SignupUsingGmail();
        }
        signupModel.setSignupListener(new SignupModel.SignupListener() {
            @Override
            public void isSuccessful(String message) {
                Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                intent.putExtra("email",userEmail);
                startActivity(intent);
            }

            @Override
            public void isFailed(String message) {
                 Toast.makeText(WelcomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
