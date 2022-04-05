package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.ServicesLandingAdapter;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ServicesLanding extends AppCompatActivity {


    FrameLayout homeServicesLayout;
    FrameLayout personalServicesLayout;
    FrameLayout educationalServicesLayout;
    FrameLayout autoServicesLayout;
    FrameLayout repairServicesLayout;
    FrameLayout generalServicesLayout;
    ImageView navBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_landing);
        initView();
    }



    private void initView(){
        homeServicesLayout = findViewById(R.id.home_services_layout);
        personalServicesLayout = findViewById(R.id.personal_services_layout);
        educationalServicesLayout = findViewById(R.id.educational_services_layout);
        autoServicesLayout = findViewById(R.id.auto_services_layout);
        repairServicesLayout = findViewById(R.id.repair_services_layout);
        generalServicesLayout = findViewById(R.id.general_services_layout);
        navBack = findViewById(R.id.nav_back);
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        homeServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServicesLanding.this,HomeServicesDetails.class));
            }
        });

        personalServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServicesLanding.this,PersonalServicesDetails.class));
            }
        });

        educationalServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServicesLanding.this,EducationalServicesDetails.class));
            }
        });

        generalServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServicesLanding.this,GeneralService.class));
            }
        });

        autoServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServicesLanding.this,AutoServicesDetails.class));
            }
        });

        repairServicesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServicesLanding.this,RepairServicesDetails.class));
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}
