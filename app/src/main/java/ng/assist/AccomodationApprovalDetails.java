package ng.assist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.ProductImageModel;

public class AccomodationApprovalDetails extends AppCompatActivity {
    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<ProductImageModel> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle, pricePerMonth, adddress, agentName, description;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    LinearLayout imageScrollLayout, call, chat;
    MaterialButton bookInspection;
    AgentModel agentModel;
    AlertDialog.Builder builder;
    String userId;
    LinearLayout errorRoot;
    MaterialButton errorRetry,rejectAcc,acceptAcc;
    RelativeLayout approveLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_approval_details);
        initView();
    }

    private void initView() {
        approveLayout = findViewById(R.id.approve_layout);
        rejectAcc = findViewById(R.id.acc_approval_reject);
        acceptAcc = findViewById(R.id.acc_approval_approve);
        errorRoot = findViewById(R.id.error_occurred_layout_root);
        errorRetry = findViewById(R.id.error_occurred_retry);
        call = findViewById(R.id.acc_details_call);
        chat = findViewById(R.id.acc_details_chat);
        bookInspection = findViewById(R.id.acc_details_book_inspection);
        imageScrollLayout = findViewById(R.id.scroll_image_layout);
        accomodationListModel = getIntent().getParcelableExtra("accModel");
        loadingBar = findViewById(R.id.acc_details_progress);
        rootLayout = findViewById(R.id.acc_details_root);
        houseTitle = findViewById(R.id.acc_details_title);
        pricePerMonth = findViewById(R.id.acc_details_price_per_month);
        adddress = findViewById(R.id.acc_details_address);
        agentName = findViewById(R.id.acc_details_agent_name);
        description = findViewById(R.id.acc_details_desc);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        agentPicture = findViewById(R.id.acc_details_agent_pic);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(AccomodationApprovalDetails.this, LinearLayoutManager.HORIZONTAL, false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
        imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);

        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();

        houseTitle.setText(accomodationListModel.getHouseTitle());


        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());
        userId = PreferenceManager.getDefaultSharedPreferences(AccomodationApprovalDetails.this).getString("userEmail", "null");

        loadPage();

        errorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(AccomodationApprovalDetails.this, ChatActivity.class);
                    intent.putExtra("receiverId", agentModel.getAgentId());
                    intent.putExtra("receiverFirstname", agentModel.getAgentFirstname());
                    intent.putExtra("receiverLastname", agentModel.getAgentLastName());
                    intent.putExtra("receiverImageUrl", agentModel.getAgentPicUrl());
                    startActivity(intent);

            }
        });



        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", agentModel.getAgentPhone(), null));
                    startActivity(intent);
            }
        });

        acceptAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              accomodationListModel = new AccomodationListModel(houseId,AccomodationApprovalDetails.this);
              accomodationListModel.approveHouse();
              accomodationListModel.setUpdateListener(new AccomodationListModel.UpdateListener() {
                  @Override
                  public void onUpdateSuccess() {
                      finish();
                      Toast.makeText(AccomodationApprovalDetails.this, "Approve Success", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onError() {
                      Toast.makeText(AccomodationApprovalDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                  }
              });

            }
        });

        rejectAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                accomodationListModel = new AccomodationListModel(houseId,AccomodationApprovalDetails.this);
                accomodationListModel.rejectHouse();
                accomodationListModel.setUpdateListener(new AccomodationListModel.UpdateListener() {
                    @Override
                    public void onUpdateSuccess() {
                        finish();
                        Toast.makeText(AccomodationApprovalDetails.this, "Reject Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        Toast.makeText(AccomodationApprovalDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void loadPage(){

        loadingBar.setVisibility(View.VISIBLE);
        rootLayout.setVisibility(View.GONE);
        errorRoot.setVisibility(View.GONE);
        AccomodationListModel accomodationListModel1 = new AccomodationListModel(houseId, agentId);
        accomodationListModel1.getAccomodationDetails();
        accomodationListModel1.setAccomodationDetailsListener(new AccomodationListModel.AccomodationDetailsListener() {
            @Override
            public void onDetailsReady(ArrayList<ProductImageModel> mImageList, AgentModel agentModel) {

                loadingBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                errorRoot.setVisibility(View.GONE);
                AccomodationApprovalDetails.this.agentModel = agentModel;
                adapter = new ProductImageScrollAdapter(mImageList, AccomodationApprovalDetails.this);
                adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());
                agentName.setText(agentModel.getAgentFirstname());
                imagesRecyclerview.setAdapter(adapter);
                imagesRecyclerview.setVisibility(View.VISIBLE);
                imageScrollLayout.setVisibility(View.VISIBLE);
                approveLayout.setVisibility(View.VISIBLE);

                if (accomodationListModel.getType().equalsIgnoreCase("lodges")) {
                    pricePerMonth.setText("₦" + accomodationListModel.getPricesPerMonth() + " per month");
                } else {
                    pricePerMonth.setText("₦" + accomodationListModel.getPricesPerMonth() + " per day");
                }

                Glide.with(AccomodationApprovalDetails.this)
                        .load(agentModel.getAgentPicUrl())
                        .placeholder(R.drawable.background_image)
                        .error(R.drawable.background_image)
                        .into(agentPicture);

            }

            @Override
            public void onError(String message) {
                Log.e("onError: ", message);
                loadingBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.GONE);
                errorRoot.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}