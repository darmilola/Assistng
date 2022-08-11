package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;
import ng.assist.UIs.Utils.ListDialog;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class Accomodation extends AppCompatActivity {

    MaterialButton checkHomes,showBookings;
    LinearLayout corpmemberLayout,employeeLayout,othersLayout,preferredLocationLayout;
    ImageView corpmemberImage,employeeImage,othersImage;
    TextView corpmemberTextView, employeeTextview, othersTextview,preferredLocationText;
    boolean isCorpmemberSelected = false, isEmployeeSelected = true, isOthersSelected = false;
    RangeSeekBar priceRangebar;
    TextView priceRangeText;
    ImageView navBack;
    String selectedCity,type = "",min_price = "0",max_price = "10000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation);
        initView();
    }

    private void initDialog(){
        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("Lagos");
        cityList.add("Abuja");
        cityList.add("Kano");

        String[] stateList = {"Abia","Adamawa","Akwa Ibom","Anambra","Bauchi","Bayelsa","Benue","Borno","Cross River","Delta","Ebonyi","Edo","Ekiti","Enugu","Gombe","Imo","Jigawa","Kaduna","Kano","Katsina","Kebbi"
                ,"Kogi","Kwara","Lagos","Nasarawa","Niger","Ogun","Ondo","Osun","Oyo","Plateau","Rivers","Sokoto","Taraba","Yobe","Zamfara"};

        for (String city : stateList) {
            cityList.add(city);
        }

        ListDialog listDialog = new ListDialog(cityList,Accomodation.this);
        listDialog.showListDialog();
        listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
            @Override
            public void onItemClicked(String city) {
                selectedCity = city;
                preferredLocationText.setText(city);
            }
        });
    }



    private void initView(){
        priceRangebar = findViewById(R.id.price_range_progress);
        navBack = findViewById(R.id.nav_back);
        showBookings = findViewById(R.id.show_bookings);
        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        priceRangebar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar rangeSeekBar, int i, int i1, boolean b) {
                min_price = Integer.toString(i);
                max_price = Integer.toString(i1);
                priceRangeText.setText("₦"+Integer.toString(i)+" - "+"₦"+Integer.toString(i1));
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
                if(isValidForm()){
                    Intent intent = new Intent(Accomodation.this,AccomodationListings.class);
                    intent.putExtra("city",selectedCity);
                    intent.putExtra("type",type);
                    intent.putExtra("max_price",max_price);
                    intent.putExtra("min_price",min_price);
                    startActivity(intent);
                }
            }
        });

        showBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Accomodation.this, UserAccomodationBooking.class));
            }
        });


        corpmemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCorpmemberSelection();
                unSelEmployee();
                //unSelOthers();
                type = "lodges";
            }
        });

        employeeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authEmployeeSelection();
                unSelCorpmemebers();
                type = "hotels";
            }
        });

        othersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authOthersSelection();
                unSelEmployee();
                unSelCorpmemebers();
                type = "others";
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private boolean isValidForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(preferredLocationText.getText().toString().trim())) {
            preferredLocationText.setError("Required");
            valid = false;
            return valid;
        }
        else if(type.equalsIgnoreCase("")){
            Toast.makeText(this, "Please Select Accommodation Type", Toast.LENGTH_SHORT).show();
            valid = false;
            return valid;
        }
        return valid;
    }

}
