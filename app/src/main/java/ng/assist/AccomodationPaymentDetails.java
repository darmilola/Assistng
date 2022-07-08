package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.ProductImageModel;

public class AccomodationPaymentDetails extends AppCompatActivity {

    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle, pricePerMonth, adddress, description;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    MaterialButton approve;
    LinearLayout imageScrollLayout;
    TextView remainingDaysToPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_payment_details);
        initView();
    }


    private void initView() {
        remainingDaysToPay = findViewById(R.id.remaining_days);
        imageScrollLayout = findViewById(R.id.scroll_image_layout);
        loadingBar = findViewById(R.id.estate_dashboard_details_progress);
        rootLayout = findViewById(R.id.estate_dashboard_details_nested_scroll);
        accomodationListModel = getIntent().getParcelableExtra("accModel");
        houseTitle = findViewById(R.id.estate_dashboard_details_title);
        pricePerMonth = findViewById(R.id.estate_dashboard_details_price);
        adddress = findViewById(R.id.estate_dashboard_details_address);
        description = findViewById(R.id.estate_dashboard_details_description);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        approve = findViewById(R.id.acc_approve_payment);


        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();

        houseTitle.setText(accomodationListModel.getHouseTitle());
        pricePerMonth.setText(accomodationListModel.getPricesPerMonth());
        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());


        Locale NigerianLocale = new Locale("en", "ng");
        String unFormattedPrice = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(accomodationListModel.getBookingFee()));
        String formattedPrice = unFormattedPrice.replaceAll("\\.00", "");

        String unFormattedPrice2 = NumberFormat.getCurrencyInstance(NigerianLocale).format(Integer.parseInt(accomodationListModel.getPricesPerMonth()));
        String formattedPrice2 = unFormattedPrice2.replaceAll("\\.00", "");

        if (accomodationListModel.getType().equalsIgnoreCase("lodges")) {
            pricePerMonth.setText(formattedPrice2 + "/year");
        } else {
            pricePerMonth.setText(formattedPrice2 + "/day");
        }


        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(AccomodationPaymentDetails.this, LinearLayoutManager.HORIZONTAL, false);
        imagesRecyclerview.setLayoutManager(imagesManager);

        EstateDashboardModel estateDashboardModel = new EstateDashboardModel(houseId, agentId);
        estateDashboardModel.getAccomodationDetails();
        estateDashboardModel.setAccomodationDetailsListener(new EstateDashboardModel.AccomodationDetailsListener() {
            @Override
            public void onDetailsReady(ArrayList<ProductImageModel> imageList, AgentModel agentModel) {
                rootLayout.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
                imageScrollLayout.setVisibility(View.VISIBLE);
                adapter = new ProductImageScrollAdapter(imageList, AccomodationPaymentDetails.this);
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
                Toast.makeText(AccomodationPaymentDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String agentId = accomodationListModel.getAgentId();
                String amount = accomodationListModel.getPricesPerMonth();
                int bookingId = accomodationListModel.getBookingId();
                AccomodationListModel accomodationListModel = new AccomodationListModel(agentId, amount, bookingId, AccomodationPaymentDetails.this);
                accomodationListModel.ReleaseFund();
                accomodationListModel.setUpdateListener(new AccomodationListModel.UpdateListener() {
                    @Override
                    public void onUpdateSuccess() {
                        Toast.makeText(AccomodationPaymentDetails.this, "Accommodation was paid Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(AccomodationPaymentDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        String remainingDays = CalcRemainingDate(accomodationListModel.getBookingDate());

        if (Integer.parseInt(remainingDays) > 7) {
            remainingDaysToPay.setText("Ready To Pay");
        } else {
            int diff = 7 - Integer.parseInt(remainingDays);
            remainingDaysToPay.setText(Integer.toString(diff) + " Days Remaining for Refund");
        }

    }

    public String CalcRemainingDate(String date) {
        String bookDate = date;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(bookDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date todaysDate = new Date();
        Date houseBookDate = c.getTime();


        String today = sdf.format(todaysDate);
        String bookDateStr = sdf.format(houseBookDate);

        String rem = findDifference(houseBookDate, todaysDate);

        return rem;
    }

    String
    findDifference(Date start_date,
                   Date end_date) {
        long difference_In_Days = 0;
        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");




            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = end_date.getTime() - start_date.getTime();


             difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;


        return Long.toString(difference_In_Days);
    }


}