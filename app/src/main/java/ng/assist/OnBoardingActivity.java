package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

public class OnBoardingActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        fragmentManager = getSupportFragmentManager();

        final PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(getDataForOnboarding());

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, onBoardingFragment);
        fragmentTransaction.commit();

        onBoardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {

                 startActivity(new Intent(OnBoardingActivity.this,WelcomeActivity.class));
                 finish();

            }
        });
    }

    private ArrayList<PaperOnboardingPage> getDataForOnboarding() {
        // prepare data
        PaperOnboardingPage scr1 = new PaperOnboardingPage("Hotels", "All hotels and hostels are sorted by hospitality rating",
                Color.parseColor("#678FB4"), R.drawable.assist_icon, R.drawable.assist_icon);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Banks", "We carefully verify all banks before add them into the app",
                Color.parseColor("#65B0B4"), R.drawable.assist_icon, R.drawable.assist_icon);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Stores", "All local stores are categorized for your convenience",
                Color.parseColor("#9B90BC"), R.drawable.assist_icon, R.drawable.assist_icon);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);
        return elements;
    }
}