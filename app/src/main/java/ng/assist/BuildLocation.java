package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.thefinestartist.finestwebview.FinestWebView;

public class BuildLocation extends AppCompatActivity {

    MaterialButton next;
    LinearLayout flightBooking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_location);
        next = findViewById(R.id.build_root_next);
        flightBooking = findViewById(R.id.flight_booking);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BuildLocation.this,BusTransport.class));
            }
        });

        flightBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(BuildLocation.this,CabHailingActivity.class));
            }
        });

    }

    private void buildFlightActivity(){
        new FinestWebView.Builder(BuildLocation.this)
                .showIconForward(false)
                .showMenuRefresh(false)
                .showUrl(false)
                .showMenuFind(false)
                .showIconMenu(false)
                .titleFont("clannews.ttf")
                .iconDefaultColor(ContextCompat.getColor(BuildLocation.this,R.color.white))
                .toolbarColor(ContextCompat.getColor(BuildLocation.this,R.color.colorPrimary))
                .statusBarColor(ContextCompat.getColor(BuildLocation.this,R.color.colorPrimary))
                .show("https://www.assisthq.bubbleapps.io/version-test/travelstart");
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        }


    }

}
