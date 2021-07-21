package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;
import ng.assist.UIs.Utils.ListDialog;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class Accomodation extends AppCompatActivity {

    MaterialButton checkHomes;
    LinearLayout corpmemberLayout,employeeLayout,othersLayout,preferredLocationLayout;
    ImageView corpmemberImage,employeeImage,othersImage;
    TextView corpmemberTextView, employeeTextview, othersTextview,preferredLocationText;
    boolean isCorpmemberSelected = false, isEmployeeSelected = true, isOthersSelected = false;
    RangeSeekBar priceRangebar;
    TextView priceRangeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation);
        initView();
    }

    private void initDialog(){
        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("Lagos");
        cityList.add("Dallas");
        cityList.add("Waco");
        cityList.add("Akure");
        cityList.add("Boston");
        cityList.add("Palo Alto");
        cityList.add("Lagos");
        cityList.add("Dallas");
        cityList.add("Waco");
        cityList.add("Akure");
        cityList.add("Boston");
        cityList.add("Palo Alto");
        cityList.add("Lagos");
        cityList.add("Dallas");
        cityList.add("Waco");
        cityList.add("Akure");
        cityList.add("Boston");
        cityList.add("Palo Alto");
        cityList.add("Lagos");
        cityList.add("Dallas");
        cityList.add("Waco");
        cityList.add("Akure");
        cityList.add("Boston");
        cityList.add("Palo Alto");


        ListDialog listDialog = new ListDialog(cityList,Accomodation.this);
        listDialog.showListDialog();
        listDialog.setCityClickedListener(new ListDialog.OnCityClickedListener() {
            @Override
            public void onCityClicked(String city) {
                preferredLocationText.setText(city);
            }
        });
    }



    private void initView(){
        priceRangebar = findViewById(R.id.price_range_progress);
        priceRangebar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar rangeSeekBar, int i, int i1, boolean b) {
                priceRangeText.setText("$"+Integer.toString(i)+" - "+"$"+Integer.toString(i1));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar rangeSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar rangeSeekBar) {

            }
        });
        priceRangeText = findViewById(R.id.price_range_text);
        preferredLocationLayout = findViewById(R.id.preferred_location_layout);
        preferredLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog();
            }
        });
        preferredLocationText = findViewById(R.id.preferred_location_text);
        corpmemberLayout = findViewById(R.id.corp_member_layout);
        employeeLayout = findViewById(R.id.employee_layout);
        othersLayout = findViewById(R.id.others_layout);
        corpmemberImage = findViewById(R.id.corp_member_image);
        employeeImage = findViewById(R.id.employee_image);
        othersImage   = findViewById(R.id.others_image);
        corpmemberTextView = findViewById(R.id.corp_member_text);
        employeeTextview = findViewById(R.id.employee_text);
        othersTextview = findViewById(R.id.others_text);

        checkHomes = findViewById(R.id.check_homes);
        checkHomes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Accomodation.this,AccomodationListings.class));
            }
        });

        corpmemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCorpmemberSelection();
                unSelEmployee();
                unSelOthers();
            }
        });

        employeeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authEmployeeSelection();
                unSelCorpmemebers();
                unSelOthers();
            }
        });

        othersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authOthersSelection();
                unSelEmployee();
                unSelCorpmemebers();
            }
        });

    }

    private void authCorpmemberSelection(){
            isCorpmemberSelected = true;
            corpmemberLayout.setBackgroundResource(R.drawable.bus_type_selected_background);
            corpmemberImage.setColorFilter(ContextCompat.getColor(Accomodation.this,R.color.white));
            corpmemberTextView.setTextColor(ContextCompat.getColor(Accomodation.this,R.color.white));
    }

    private void authEmployeeSelection(){
            isEmployeeSelected = true;
            employeeLayout.setBackgroundResource(R.drawable.bus_type_selected_background);
            employeeImage.setColorFilter(ContextCompat.getColor(Accomodation.this,R.color.white));
            employeeTextview.setTextColor(ContextCompat.getColor(Accomodation.this,R.color.white));
    }


    private void authOthersSelection(){
        isOthersSelected = true;
        othersLayout.setBackgroundResource(R.drawable.bus_type_selected_background);
        othersImage.setColorFilter(ContextCompat.getColor(Accomodation.this,R.color.white));
        othersTextview.setTextColor(ContextCompat.getColor(Accomodation.this,R.color.white));
    }


    private void unSelEmployee(){
        isEmployeeSelected = false;
        employeeLayout.setBackgroundResource(R.drawable.accomodation_background_unselected);
        employeeImage.setColorFilter(ContextCompat.getColor(Accomodation.this,R.color.colorPrimary));
        employeeTextview.setTextColor(ContextCompat.getColor(Accomodation.this,R.color.colorPrimary));
    }

    private void unSelOthers(){
        isOthersSelected = false;
         othersLayout.setBackgroundResource(R.drawable.accomodation_background_unselected);
         othersImage.setColorFilter(ContextCompat.getColor(Accomodation.this,R.color.colorPrimary));
        othersTextview.setTextColor(ContextCompat.getColor(Accomodation.this,R.color.colorPrimary));
    }

    private void unSelCorpmemebers(){
        isCorpmemberSelected = false;
        corpmemberLayout.setBackgroundResource(R.drawable.accomodation_background_unselected);
        corpmemberImage.setColorFilter(ContextCompat.getColor(Accomodation.this,R.color.colorPrimary));
        corpmemberTextView.setTextColor(ContextCompat.getColor(Accomodation.this,R.color.colorPrimary));
    }


    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.transparent));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }
}
