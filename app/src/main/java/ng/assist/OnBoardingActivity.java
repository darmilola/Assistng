package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.limerse.onboard.OnboardAdvanced;
import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class OnBoardingActivity extends OnboardAdvanced {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

          addSlide(new OnboardTransport());
          addSlide(new OnBoardMarketplace());
          addSlide(new onBoardQuickCredit());
          addSlide(new onBoardAccomodation());
          addSlide(new onBoardServices());

    }

    @Override
    public void onSkipPressed(Fragment fragment){
       startActivity(new Intent(OnBoardingActivity.this,WelcomeActivity.class));
       finish();
    }

    @Override
    public void onDonePressed(Fragment fragment){
        startActivity(new Intent(OnBoardingActivity.this,WelcomeActivity.class));
        finish();
    }



}