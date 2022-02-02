package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.AccomodationBookingHomeDisplayAdapter;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.ItemDecorator;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.CreatBill;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class AccomodationBooking extends AppCompatActivity {
    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle,pricePerMonth,adddress,agentName,description,bookingFee;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    CardView bookNowLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    LinearLayout imageScrollLayout,call,chat;
    MaterialButton bookInspection;
    AgentModel agentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_booking);
        initView();
    }

    private void initView(){
        call = findViewById(R.id.acc_details_call);
        chat = findViewById(R.id.acc_details_chat);
        bookInspection = findViewById(R.id.acc_details_book_inspection);
        imageScrollLayout = findViewById(R.id.scroll_image_layout);
        accomodationListModel = getIntent().getParcelableExtra("accModel");
        loadingBar = findViewById(R.id.acc_details_progress);
        rootLayout = findViewById(R.id.acc_details_root);
        bookNowLayout = findViewById(R.id.acc_booknow_layout);
        houseTitle = findViewById(R.id.acc_details_title);
        pricePerMonth = findViewById(R.id.acc_details_price_per_month);
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

        if(accomodationListModel.getType().equalsIgnoreCase("lodges")){
            pricePerMonth.setText("₦"+accomodationListModel.getPricesPerMonth()+" per month");
        }
        else{
            pricePerMonth.setText("₦"+accomodationListModel.getPricesPerMonth()+" per day");
        }


        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());
        bookingFee.setText("₦"+accomodationListModel.getBookingFee());
        String userId = PreferenceManager.getDefaultSharedPreferences(AccomodationBooking.this).getString("userEmail","null");


        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(AccomodationBooking.this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccomodationBooking.this,ChatActivity.class);
                intent.putExtra("receiverId",agentModel.getAgentId());
                intent.putExtra("receiverFirstname",agentModel.getAgentFirstname());
                intent.putExtra("receiverLastname",agentModel.getAgentLastName());
                intent.putExtra("receiverImageUrl",agentModel.getAgentPicUrl());
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

        AccomodationListModel accomodationListModel1 = new AccomodationListModel(houseId,agentId);
        accomodationListModel1.getAccomodationDetails();
        accomodationListModel1.setAccomodationDetailsListener(new AccomodationListModel.AccomodationDetailsListener() {
            @Override
            public void onDetailsReady(ArrayList<String> mImageList, AgentModel agentModel) {

                loadingBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                bookNowLayout.setVisibility(View.VISIBLE);
                AccomodationBooking.this.agentModel = agentModel;
                adapter = new ProductImageScrollAdapter(mImageList,AccomodationBooking.this);
                agentName.setText(agentModel.getAgentFirstname());
                imagesRecyclerview.setAdapter(adapter);
                imagesRecyclerview.setVisibility(View.VISIBLE);
                imageScrollLayout.setVisibility(View.VISIBLE);
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
                rootLayout.setVisibility(View.GONE);
                bookNowLayout.setVisibility(View.GONE);
                Toast.makeText(AccomodationBooking.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        bookInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAuthorized(accomodationListModel.getBookingFee())) {

                    String fullName = agentModel.getAgentFirstname() + " - " + agentModel.getAgentLastName();
                    CreatBill creatBill = new CreatBill(userId, agentModel.getAgentId(), Integer.parseInt(accomodationListModel.getBookingFee()), "3", fullName, AccomodationBooking.this, "");
                    creatBill.CreateBill();
                    creatBill.setCreateBillListener(new CreatBill.CreateBillListener() {
                        @Override
                        public void onSuccess() {
                            Date date = new Date();
                            Timestamp timestamp = new Timestamp(date.getTime());
                            insertBooking(0, 3, "Inspection", timestamp.toString(), accomodationListModel.getBookingFee(), "");
                            Toast.makeText(AccomodationBooking.this, "You have booked Inspection Successfully", Toast.LENGTH_SHORT).show();

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccomodationBooking.this);
                            String walletBalance = preferences.getString("walletBalance","0");
                            preferences.edit().putString("walletBalance",Integer.toString(Integer.parseInt(walletBalance) - Integer.parseInt(accomodationListModel.getBookingFee()))).apply();

                            finish();

                        }

                        @Override
                        public void onError() {
                            Toast.makeText(AccomodationBooking.this, "Error Occurred please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(AccomodationBooking.this,"Insufficient Balance", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isAuthorized(String inspectionFee){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccomodationBooking.this);
        String walletBalance = preferences.getString("walletBalance","0");
        if(Integer.parseInt(walletBalance) < Integer.parseInt(inspectionFee)){
            return false;
        }
        else{
            return true;
        }
    }

    private void insertBooking(int id,int type, String title, String timestamp, String amount, String orderId){
        TransactionDatabase db = Room.databaseBuilder(AccomodationBooking.this,
                TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
        Transactions transactions = new Transactions(id,type,title,timestamp,amount,orderId);
        TransactionDao transactionDao = db.transactionDao();
        transactionDao.insert(transactions);
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
