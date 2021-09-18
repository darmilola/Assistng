package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class EstateDashboardListingDetails extends AppCompatActivity {

    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle,beds,baths,pricePerMonth,ratings,rateCount,adddress,agentName,description,bookingFee;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    SwitchMaterial availabilitySwitch;
    MaterialButton deleteListing;
    LinearLayout imageScrollLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate_dashboard_listing_details);
        initView();
    }

    private void initView(){

        imageScrollLayout = findViewById(R.id.scroll_image_layout);
        availabilitySwitch = findViewById(R.id.estate_dashboard_details_availability_switch);
        deleteListing = findViewById(R.id.estate_dashboard_details_delete_listing);
        loadingBar = findViewById(R.id.estate_dashboard_details_progress);
        rootLayout = findViewById(R.id.estate_dashboard_details_nested_scroll);
        accomodationListModel = getIntent().getParcelableExtra("accModel");
        houseTitle = findViewById(R.id.estate_dashboard_details_title);
        beds = findViewById(R.id.estate_dashboard_details_bed);
        baths = findViewById(R.id.estate_dashboard_details_bath);
        pricePerMonth = findViewById(R.id.estate_dashboard_details_price);
        adddress = findViewById(R.id.estate_dashboard_details_address);
        description = findViewById(R.id.estate_dashboard_details_description);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        bookingFee = findViewById(R.id.estate_dashboard_details_booking_fee);


        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();

        houseTitle.setText(accomodationListModel.getHouseTitle());
        beds.setText(accomodationListModel.getBeds());
        baths.setText(accomodationListModel.getBaths());
        pricePerMonth.setText(accomodationListModel.getPricesPerMonth());
        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());
        bookingFee.setText(accomodationListModel.getBookingFee());

        Toast.makeText(this, accomodationListModel.getIsAvailable(), Toast.LENGTH_SHORT).show();

        if(accomodationListModel.getIsAvailable().equalsIgnoreCase("true")){
            availabilitySwitch.setChecked(true);
        }
        else{
            availabilitySwitch.setChecked(false);
        }


        availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isValidateAgent() && isChecked){
                    EstateDashboardModel estateDashboardModel = new EstateDashboardModel(accomodationListModel.getHouseId(),"true",1);
                    estateDashboardModel.updateAgentListingInfo();
                    estateDashboardModel.setUpdateInfoListener(new EstateDashboardModel.UpdateInfoListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(EstateDashboardListingDetails.this, "House is now Available for inspection", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(EstateDashboardListingDetails.this, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(!isValidateAgent()){
                    availabilitySwitch.setChecked(false);
                    Toast.makeText(EstateDashboardListingDetails.this, "Please provide your phonenumber", Toast.LENGTH_SHORT).show();
                }
                else if(!isChecked){
                    EstateDashboardModel estateDashboardModel = new EstateDashboardModel(accomodationListModel.getHouseId(),"false",0);
                    estateDashboardModel.updateAgentListingInfo();
                    estateDashboardModel.setUpdateInfoListener(new EstateDashboardModel.UpdateInfoListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(EstateDashboardListingDetails.this, "House is no more available for inspection", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String message) {

                        }
                    });
                }
            }
        });


        deleteListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              EstateDashboardModel estateDashboardModel = new EstateDashboardModel(EstateDashboardListingDetails.this,accomodationListModel.getHouseId());
              estateDashboardModel.deleteAgentListing();
              estateDashboardModel.setUpdateInfoListener(new EstateDashboardModel.UpdateInfoListener() {
                  @Override
                  public void onSuccess() {
                      Toast.makeText(EstateDashboardListingDetails.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                      finish();
                  }

                  @Override
                  public void onError(String message) {
                      Toast.makeText(EstateDashboardListingDetails.this, message, Toast.LENGTH_SHORT).show();
                  }
              });
            }
        });

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(EstateDashboardListingDetails.this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);

        EstateDashboardModel estateDashboardModel = new EstateDashboardModel(houseId,agentId);
        estateDashboardModel.getAccomodationDetails();
        estateDashboardModel.setAccomodationDetailsListener(new EstateDashboardModel.AccomodationDetailsListener() {
            @Override
            public void onDetailsReady(ArrayList<String> imageList, AgentModel agentModel) {

                rootLayout.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
                imageScrollLayout.setVisibility(View.VISIBLE);
                adapter = new ProductImageScrollAdapter(imageList,EstateDashboardListingDetails.this);
                imagesRecyclerview.setAdapter(adapter);
                pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
                imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
                adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());
            }

            @Override
            public void onError(String message) {
                rootLayout.setVisibility(View.GONE);
                loadingBar.setVisibility(View.GONE);
                imageScrollLayout.setVisibility(View.GONE);
                Toast.makeText(EstateDashboardListingDetails.this, message, Toast.LENGTH_SHORT).show();
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

    private boolean isValidateAgent(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EstateDashboardListingDetails.this);
        String phone = preferences.getString("phone","");
        if(phone.equalsIgnoreCase("")){
            return false;
        }
        else{
            return  true;
        }
    }
}
