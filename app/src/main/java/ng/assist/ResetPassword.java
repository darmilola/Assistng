package ng.assist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import ng.assist.UIs.ViewModel.ForgotPasswordModel;

public class ResetPassword extends AppCompatActivity {

    TextInputEditText newPassword, retypePassword;
    MaterialButton reset;
    String userIdHash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
    }

    private void initView(){
        newPassword = findViewById(R.id.reset_password_password);
        retypePassword = findViewById(R.id.reset_password_password_retype);
        reset = findViewById(R.id.reset_password_reset_button);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        userIdHash = data.getQueryParameter("hash");

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidForm()) {
                    ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel(newPassword.getText().toString(),userIdHash,ResetPassword.this,1 );
                    forgotPasswordModel.ResetPassword();
                    forgotPasswordModel.setListener(new ForgotPasswordModel.getLinkListener() {
                        @Override
                        public void isSuccess() {
                            showAlert();
                        }

                        @Override
                        public void isFailure() {
                            Toast.makeText(ResetPassword.this, "Error Occurred please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void showAlert(){
        new AlertDialog.Builder(ResetPassword.this)
                .setTitle("Password Reset")
                .setMessage("Your Password has been reset successfully, you can now login")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ResetPassword.this,ConnectWithEmail.class));
                        finish();
                    }
                })
                .show();
    }

    private boolean isValidForm(){
        boolean isValid = true;

        if(TextUtils.isEmpty(newPassword.getText().toString().trim())){
            newPassword.setError("Required");
            isValid = false;
            return isValid;
        }
        if(TextUtils.isEmpty(retypePassword.getText().toString().trim())){
            retypePassword.setError("Required");
            isValid = false;
            return isValid;
        }

        if(!newPassword.getText().toString().trim().equalsIgnoreCase(retypePassword.getText().toString().trim())){
            Toast.makeText(ResetPassword.this, "Password does not match", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        return isValid;
    }
}
