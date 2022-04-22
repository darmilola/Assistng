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
import ng.assist.UIs.ViewModel.EcommerceDashboardModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.ProductImageModel;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EstateDashboardListingDetails extends AppCompatActivity {

    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle,pricePerMonth,adddress,description,bookingFee;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    MaterialButton deleteListing;
    LinearLayout imageScrollLayout;
    CheckBox isAvailable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estate_dashboard_listing_details);
        initView();
    }

    private void initView(){

        imageScrollLayout = findViewById(R.id.scroll_image_layout);
        deleteListing = findViewById(R.id.estate_dashboard_details_delete_listing);
        loadingBar = findViewById(R.id.estate_dashboard_details_progress);
        rootLayout = findViewById(R.id.estate_dashboard_details_nested_scroll);
        accomodationListModel = getIntent().getParcelableExtra("accModel");
        houseTitle = findViewById(R.id.estate_dashboard_details_title);
        pricePerMonth = findViewById(R.id.estate_dashboard_details_price);
        adddress = findViewById(R.id.estate_dashboard_details_address);
        description = findViewById(R.id.estate_dashboard_details_description);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        bookingFee = findViewById(R.id.estate_dashboard_details_booking_fee);
        isAvailable = findViewById(R.id.estate_dashboard_details_is_available);

        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();

        houseTitle.setText(accomodationListModel.getHouseTitle());
        pricePerMonth.setText(accomodationListModel.getPricesPerMonth());
        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());


        Locale NigerianLocale = new Locale("en","ng");
        String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(accomodationListModel.getBookingFee()));
        String formattedPrice = unFormattedPrice.replaceAll("\\.00","");

        String unFormattedPrice2 = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(accomodationListModel.getPricesPerMonth()));
        String formattedPrice2 = unFormattedPrice2.replaceAll("\\.00","");

        if(accomodationListModel.getType().equalsIgnoreCase("lodges")){
            pricePerMonth.setText(formattedPrice2+"/month");
        }
        else{
            pricePerMonth.setText(formattedPrice2+"/day");
        }

        if(accomodationListModel.getIsAvailable().equalsIgnoreCase("false")){
            isAvailable.setChecked(false);
        }
        else{
            isAvailable.setChecked(true);
        }

        if(accomodationListModel.getStatus().equalsIgnoreCase("Rejected")){
            isAvailable.setVisibility(View.GONE);
        }

        if(accomodationListModel.getStatus().equalsIgnoreCase("Pending")){
            isAvailable.setVisibility(View.GONE);
        }


        bookingFee.setText(formattedPrice);


        isAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AccomodationListModel mAccomodationListModel = null;
                if(isChecked){
                    mAccomodationListModel = new AccomodationListModel(accomodationListModel.getHouseId(),"true",null);
                }
                else{
                    mAccomodationListModel = new AccomodationListModel(accomodationListModel.getHouseId(),"false",null);

                }
                mAccomodationListModel.updateHouseInfo();
                mAccomodationListModel.setUpdateListener(new AccomodationListModel.UpdateListener() {
                    @Override
                    public void onUpdateSuccess() {
                        Toast.makeText(EstateDashboardListingDetails.this, "Update Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(EstateDashboardListingDetails.this, "Update Error", Toast.LENGTH_SHORT).show();
                    }
                });
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
                      setResult(300);
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
            public void onDetailsReady(ArrayList<ProductImageModel> imageList, AgentModel agentModel) {

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
                Toast.makeText(EstateDashboardListingDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
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
