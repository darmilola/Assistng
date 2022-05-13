package ng.assist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import ng.assist.UIs.ViewModel.ForgotPasswordModel;

public class VerificationEmail extends AppCompatActivity {

    TextInputEditText emailText;
    MaterialButton getResetLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_email);
        initView();
    }

    private void initView(){
        emailText = findViewById(R.id.forgot_password_email);
        getResetLink = findViewById(R.id.forgot_password_reset_link);

        getResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(emailText.getText().toString().trim().equalsIgnoreCase("")){
                    emailText.setError("Required");
                }
                else{
                    ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel(emailText.getText().toString().trim(),generateRandomId(),VerificationEmail.this);
                    forgotPasswordModel.getResetLink();
                    forgotPasswordModel.setListener(new ForgotPasswordModel.getLinkListener() {
                        @Override
                        public void isSuccess() {
                            showAlert();
                        }

                        @Override
                        public void isFailure() {
                            Toast.makeText(VerificationEmail.this, "Error Occurred please try again", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    // function to generate a random string of length n
    static String generateRandomId()
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


    private void showAlert(){
        new AlertDialog.Builder(VerificationEmail.this)
                .setTitle("Reset Link Sent")
                .setMessage("A password reset link has been forwarded to the provided email address if it exist in our database")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }
}