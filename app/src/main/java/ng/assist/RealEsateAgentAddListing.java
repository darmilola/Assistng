package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.Utils.ListDialog;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

public class RealEsateAgentAddListing extends AppCompatActivity {

    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    ArrayList<String> accommodationTypeList = new ArrayList<>();
    ArrayList<String> cityList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    EditText title,pricePerMonth,address,bed,bath,bookingFee,description;
    TextView type,city;
    String mTitle = "",mPricePerMonth = "",mAddress = "",mCity = "",mBed = "",mBath = "",mBookingFee = "",mDescription = "",mType = "";
    String houseId = "";
    String displayImage = "";
    MaterialCheckBox availability;
    LinearLayout cancel,saveListing;
    LinearLayout scrollImageLayout;
    ListDialog listDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_esate_agent_add_listing);
        initView();
    }

    private void initView(){
        populateList();
        scrollImageLayout = findViewById(R.id.scroll_image_layout);
        cancel = findViewById(R.id.real_estate_add_listing_cancel);
        saveListing = findViewById(R.id.real_estate_add_listing_save);
        title = findViewById(R.id.real_estate_add_listing_title);
        pricePerMonth = findViewById(R.id.real_estate_add_listing_price);
        address = findViewById(R.id.real_estate_add_listing_addess);
        city = findViewById(R.id.real_estate_add_listing_city);
        bed = findViewById(R.id.real_estate_add_listing_bed);
        bath = findViewById(R.id.real_estate_add_listing_bath);
        bookingFee = findViewById(R.id.real_estate_add_listing_booking_fee);
        description = findViewById(R.id.real_estate_add_listing_desc);
        type = findViewById(R.id.real_estate_add_listing_type);
        availability = findViewById(R.id.real_estate_add_listing_availability);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        for(int i = 0; i < 5; i++){
            imagesList.add("");
        }
        adapter = new ProductImageScrollAdapter(imagesList,this);
        LinearLayoutManager imagesManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        imagesRecyclerview.setAdapter(adapter);
        pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
        imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
        adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());

        saveListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = title.getText().toString().trim();
                mPricePerMonth = pricePerMonth.getText().toString().trim();
                mAddress = address.getText().toString().trim();
                mCity = city.getText().toString().trim();
                mType = type.getText().toString().trim();
                mBath = bath.getText().toString().trim();
                mBed = bed.getText().toString().trim();
                mBookingFee = bookingFee.getText().toString().trim();
                mDescription = description.getText().toString().trim();
            }
        });

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(cityList,RealEsateAgentAddListing.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {

                    }
                });
            }
        });

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(accommodationTypeList,RealEsateAgentAddListing.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {

                    }
                });
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void populateList(){
        cityList.add("Kano");
        cityList.add("Lagos");
        cityList.add("Abuja");
        accommodationTypeList.add("Employee");
        accommodationTypeList.add("Corpers");
        accommodationTypeList.add("others");
    }
    private boolean isValidInput(){
        boolean isValid = true;



        return isValid;
    }

    // function to generate a random string of length n
    static String generateHouseId()
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
}
