package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    CountDownTimer countDownTimer;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        countDownTimer = new CountDownTimer(4000,1000);
        countDownTimer.start();
    }

    public class CountDownTimer extends android.os.CountDownTimer {


        public CountDownTimer(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {


        }

        @Override
        public void onFinish() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
            String userEmail = preferences.getString("userEmail","");
            boolean isFirstTimeUser = preferences.getBoolean("first_time",true);

            if(isFirstTimeUser){
                startActivity(new Intent(SplashScreen.this,OnBoardingActivity.class));
                finish();
            }

           else if(userEmail.equalsIgnoreCase("")){
                intent = new Intent(SplashScreen.this,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                intent = new Intent(SplashScreen.this,MainActivity.class);
                intent.putExtra("email",userEmail);
                startActivity(intent);
                finish();
            }



        }

    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
               }
    }

}
