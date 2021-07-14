package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }
}
