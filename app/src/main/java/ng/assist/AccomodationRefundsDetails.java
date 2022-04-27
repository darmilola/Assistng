package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.ProductImageModel;

public class AccomodationRefundsDetails extends AppCompatActivity {
    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle,pricePerMonth,adddress,description;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    MaterialButton approve,reject;
    LinearLayout imageScrollLayout;
    TextView agentReason,useReason;
    ImageView userEvidence,agentEvidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_refunds_details);
        initView();
    }

    private void initView(){
        agentEvidence = findViewById(R.id.agent_evidence);
        userEvidence = findViewById(R.id.user_evidence);
        agentReason = findViewById(R.id.refund_agent_reason);
        useReason = findViewById(R.id.refund_user_reason);
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
        approve = findViewById(R.id.refund_approve_refund);
        reject = findViewById(R.id.refund_release_fund);

        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();

        houseTitle.setText(accomodationListModel.getHouseTitle());
        pricePerMonth.setText(accomodationListModel.getPricesPerMonth());
        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());

        agentReason.setText(accomodationListModel.getAgentReason());
        useReason.setText(accomodationListModel.getUserReason());

        if(accomodationListModel.getAgentEvidence().equalsIgnoreCase("")){
            agentEvidence.setVisibility(View.GONE);
        }
        else{
            Glide.with(this)
                    .load(accomodationListModel.getAgentEvidence())
                    .placeholder(R.drawable.background_image)
                    .error(R.drawable.background_image)
                    .into(agentEvidence);
        }
        if(accomodationListModel.getUserEvidence().equalsIgnoreCase("")){
            userEvidence.setVisibility(View.GONE);
        }
        else{
            Glide.with(this)
                    .load(accomodationListModel.getUserEvidence())
                    .placeholder(R.drawable.background_image)
                    .error(R.drawable.background_image)
                    .into(userEvidence);
        }

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


        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(AccomodationRefundsDetails.this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);

        EstateDashboardModel estateDashboardModel = new EstateDashboardModel(houseId,agentId);
        estateDashboardModel.getAccomodationDetails();
        estateDashboardModel.setAccomodationDetailsListener(new EstateDashboardModel.AccomodationDetailsListener() {
            @Override
            public void onDetailsReady(ArrayList<ProductImageModel> imageList, AgentModel agentModel) {
                rootLayout.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
                imageScrollLayout.setVisibility(View.VISIBLE);
                adapter = new ProductImageScrollAdapter(imageList,AccomodationRefundsDetails.this);
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
                Toast.makeText(AccomodationRefundsDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String userId = accomodationListModel.getUserId();
                 String amount = accomodationListModel.getPricesPerMonth();
                 String houseId = accomodationListModel.getHouseId();
                 int bookingId = accomodationListModel.getBookingId();
                 AccomodationListModel accomodationListModel = new AccomodationListModel(userId,amount,bookingId,houseId,AccomodationRefundsDetails.this);
                 accomodationListModel.ApproveRefund();
                 accomodationListModel.setUpdateListener(new AccomodationListModel.UpdateListener() {
                     @Override
                     public void onUpdateSuccess() {
                         Toast.makeText(AccomodationRefundsDetails.this, "Refund was Successful", Toast.LENGTH_SHORT).show();
                         finish();
                     }

                     @Override
                     public void onError() {
                         Toast.makeText(AccomodationRefundsDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                     }
                 });
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String agentId = accomodationListModel.getAgentId();
                String amount = accomodationListModel.getPricesPerMonth();
                int bookingId = accomodationListModel.getBookingId();
                AccomodationListModel accomodationListModel = new AccomodationListModel(agentId,amount,bookingId,AccomodationRefundsDetails.this);
                accomodationListModel.ReleaseFund();
                accomodationListModel.setUpdateListener(new AccomodationListModel.UpdateListener() {
                    @Override
                    public void onUpdateSuccess() {
                        Toast.makeText(AccomodationRefundsDetails.this, "Refund was Rejected Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(AccomodationRefundsDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}