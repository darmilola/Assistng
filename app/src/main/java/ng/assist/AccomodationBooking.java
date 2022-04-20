package ng.assist;

import androidx.appcompat.app.AlertDialog;
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
import ng.assist.UIs.ViewModel.ProductImageModel;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;

import android.content.Context;
import android.content.DialogInterface;
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
    ArrayList<ProductImageModel> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle, pricePerMonth, adddress, agentName, description, bookingFee;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    CardView bookNowLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    LinearLayout imageScrollLayout, call, chat;
    MaterialButton bookInspection;
    AgentModel agentModel;
    AlertDialog.Builder builder;
    String userId;
    LinearLayout errorRoot;
    MaterialButton errorRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accomodation_booking);
        initView();
    }

    private void initView() {
        errorRoot = findViewById(R.id.error_occurred_layout_root);
        errorRetry = findViewById(R.id.error_occurred_retry);
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
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(AccomodationBooking.this, LinearLayoutManager.HORIZONTAL, false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
        imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);


        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();

        houseTitle.setText(accomodationListModel.getHouseTitle());




        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());
        bookingFee.setText("₦" + accomodationListModel.getBookingFee());
        userId = PreferenceManager.getDefaultSharedPreferences(AccomodationBooking.this).getString("userEmail", "null");

        loadPage();

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValidBalance()) {
                    Toast.makeText(AccomodationBooking.this, "You do not have sufficient balance to complete this action", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent = new Intent(AccomodationBooking.this, ChatActivity.class);
                    intent.putExtra("receiverId", agentModel.getAgentId());
                    intent.putExtra("receiverFirstname", agentModel.getAgentFirstname());
                    intent.putExtra("receiverLastname", agentModel.getAgentLastName());
                    intent.putExtra("receiverImageUrl", agentModel.getAgentPicUrl());
                    startActivity(intent);
            }
            }
        });

        errorRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isValidBalance()){
                    Toast.makeText(AccomodationBooking.this, "You do not have sufficient balance to complete this action", Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", agentModel.getAgentPhone(), null));
                    startActivity(intent);
                }
            }
        });



        bookInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showBookingDialog();
            }
        });
    }

    public boolean isAuthorized(String inspectionFee) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccomodationBooking.this);
        String walletBalance = preferences.getString("walletBalance", "0");
        if (Integer.parseInt(walletBalance) < Integer.parseInt(inspectionFee)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidBalance() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccomodationBooking.this);
        String walletBalance = preferences.getString("walletBalance", "0");
        if (Integer.parseInt(walletBalance) < 1000) {
            return false;
        } else {
            return true;
        }
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
                AccomodationBooking.this.agentModel = agentModel;
                adapter = new ProductImageScrollAdapter(mImageList, AccomodationBooking.this);
                adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());
                agentName.setText(agentModel.getAgentFirstname());
                imagesRecyclerview.setAdapter(adapter);
                imagesRecyclerview.setVisibility(View.VISIBLE);
                imageScrollLayout.setVisibility(View.VISIBLE);

                if (accomodationListModel.getType().equalsIgnoreCase("lodges")) {
                    pricePerMonth.setText("₦" + accomodationListModel.getPricesPerMonth() + " per month");
                    bookNowLayout.setVisibility(View.VISIBLE);
                } else {
                    pricePerMonth.setText("₦" + accomodationListModel.getPricesPerMonth() + " per day");
                    bookNowLayout.setVisibility(View.GONE);
                }


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
                errorRoot.setVisibility(View.VISIBLE);
            }
        });


    }

    private void insertBooking(int id, int type, String title, String timestamp, String amount, String orderId) {
        TransactionDatabase db = Room.databaseBuilder(AccomodationBooking.this,
                TransactionDatabase.class, "transactions").allowMainThreadQueries().build();
        Transactions transactions = new Transactions(id, type, title, timestamp, amount, orderId);
        TransactionDao transactionDao = db.transactionDao();
        transactionDao.insert(transactions);
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

    private void showBookingDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("You are about to pay booking fee for this listing")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (isAuthorized(accomodationListModel.getBookingFee())) {

                            String fullName = agentModel.getAgentFirstname() + " - " + agentModel.getAgentLastName();
                            String billId = generateBillId();
                            CreatBill creatBill = new CreatBill(userId,accomodationListModel.getBookingFee(), accomodationListModel.getAgentId(), accomodationListModel.getHouseId(), AccomodationBooking.this);
                            creatBill.BookHouse();
                            creatBill.setCreateBillListener(new CreatBill.CreateBillListener() {
                                @Override
                                public void onSuccess() {

                                    Toast.makeText(AccomodationBooking.this, "Booking Successful", Toast.LENGTH_SHORT).show();

                                 /* Date date = new Date();
                                    Timestamp timestamp = new Timestamp(date.getTime());
                                    insertBooking(0, 3, "Inspection", timestamp.toString(), accomodationListModel.getBookingFee(), "");
                                    Toast.makeText(AccomodationBooking.this, "You have booked Inspection Successfully", Toast.LENGTH_SHORT).show();
                                    addPending();
                                    reduceWalletBalanceInSharedPref(AccomodationBooking.this,accomodationListModel.getBookingFee());
                                    finish();*/
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(AccomodationBooking.this, "Error Occurred please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(AccomodationBooking.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Confirm Booking");
        alert.show();
    }

    private void reduceWalletBalanceInSharedPref(Context context, String amount){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String walletBalance = preferences.getString("walletBalance","0");
        preferences.edit().putString("walletBalance",Integer.toString(Integer.parseInt(walletBalance) - Integer.parseInt(amount))).apply();
    }


    // function to generate a random string of length n
    static String generateBillId()
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


    private void addPending(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AccomodationBooking.this);
        preferences.edit().putBoolean("isPending",true).apply();
    }


}


