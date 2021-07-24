package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.AccomodationBookingHomeDisplayAdapter;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.ItemDecorator;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AccomodationBooking extends AppCompatActivity {
    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle,beds,baths,pricePerMonth,ratings,rateCount,adddress,agentName,description,bookingFee;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    CardView bookNowLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_booking);
        initView();
    }

    private void initView(){

        accomodationListModel = getIntent().getParcelableExtra("accModel");
        loadingBar = findViewById(R.id.acc_details_progress);
        rootLayout = findViewById(R.id.acc_details_root);
        bookNowLayout = findViewById(R.id.acc_booknow_layout);
        houseTitle = findViewById(R.id.acc_details_title);
        beds = findViewById(R.id.acc_details_bed);
        baths = findViewById(R.id.acc_details_bath);
        pricePerMonth = findViewById(R.id.acc_details_price_per_month);
        ratings = findViewById(R.id.acc_details_rating);
        rateCount = findViewById(R.id.acc_details_rate_count);
        adddress = findViewById(R.id.acc_details_address);
        agentName = findViewById(R.id.acc_details_agent_name);
        description = findViewById(R.id.acc_details_desc);
        bookingFee = findViewById(R.id.acc_details_booking_price);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        agentPicture = findViewById(R.id.acc_details_agent_pic);

        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();

        houseTitle.setText(accomodationListModel.getHouseTitle());
        beds.setText(accomodationListModel.getBeds());
        baths.setText(accomodationListModel.getBaths());
        pricePerMonth.setText(accomodationListModel.getPricesPerMonth());
        ratings.setText(accomodationListModel.getTotalRatings());
        rateCount.setText(accomodationListModel.getTotalRaters());
        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());
        bookingFee.setText(accomodationListModel.getBookingFee());

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(AccomodationBooking.this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);


        AccomodationListModel accomodationListModel1 = new AccomodationListModel(houseId,agentId);
        accomodationListModel1.getAccomodationDetails();
        accomodationListModel1.setAccomodationDetailsListener(new AccomodationListModel.AccomodationDetailsListener() {
            @Override
            public void onDetailsReady(ArrayList<String> mImageList, AgentModel agentModel) {

                loadingBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                bookNowLayout.setVisibility(View.VISIBLE);

                adapter = new ProductImageScrollAdapter(mImageList,AccomodationBooking.this);
                agentName.setText(agentModel.getAgentFirstname()+" "+agentModel.getAgentLastName());
                imagesRecyclerview.setAdapter(adapter);
                pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
                imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
                adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());


                Glide.with(AccomodationBooking.this)
                        .load(agentModel.getAgentPicUrl())
                        .placeholder(R.drawable.background_image)
                        .error(R.drawable.background_image)
                        .into(agentPicture);
            }

            @Override
            public void onError(String message) {
                loadingBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                bookNowLayout.setVisibility(View.VISIBLE);
                Toast.makeText(AccomodationBooking.this, message, Toast.LENGTH_SHORT).show();
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
