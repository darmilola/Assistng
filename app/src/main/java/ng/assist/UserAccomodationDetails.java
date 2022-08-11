package ng.assist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.Utils.AccomodationRefundDialog;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.ProductImageModel;
import ng.assist.UIs.ViewModel.TransactionDao;
import ng.assist.UIs.ViewModel.TransactionDatabase;
import ng.assist.UIs.ViewModel.Transactions;
import ng.assist.UIs.ViewModel.VerificationModel;

public class UserAccomodationDetails extends AppCompatActivity {

    static int PICK_IMAGE = 1;
    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<ProductImageModel> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle, pricePerMonth, adddress, agentName, description,refundProcessingText,beds,bath,perTag;
    ImageView agentPicture;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    LinearLayout imageScrollLayout, call, chat;
    AgentModel agentModel;
    AlertDialog.Builder builder;
    String userId;
    LinearLayout errorRoot;
    MaterialButton errorRetry,applyRefund;
    AccomodationRefundDialog accomodationRefundDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_accomodation_details);
        initView();
    }

    private void initView() {
        refundProcessingText = findViewById(R.id.user_accommodation_refund_text_2);
        applyRefund = findViewById(R.id.user_accommodation_details_apply_refunds);
        accomodationRefundDialog = new AccomodationRefundDialog(UserAccomodationDetails.this);
        errorRoot = findViewById(R.id.error_occurred_layout_root);
        errorRetry = findViewById(R.id.error_occurred_retry);
        call = findViewById(R.id.acc_details_call);
        chat = findViewById(R.id.acc_details_chat);
        beds = findViewById(R.id.accomodation_listing_beds);
        bath = findViewById(R.id.accomodation_listing_baths);
        perTag = findViewById(R.id.accommodation_listing_per_tag_text);
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
        LinearLayoutManager imagesManager = new LinearLayoutManager(UserAccomodationDetails.this, LinearLayoutManager.HORIZONTAL, false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();
        houseTitle.setText(accomodationListModel.getHouseTitle());
        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());
        userId = PreferenceManager.getDefaultSharedPreferences(UserAccomodationDetails.this).getString("userEmail", "null");

        loadPage();

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(UserAccomodationDetails.this, ChatActivity.class);
                    intent.putExtra("receiverId", agentModel.getAgentId());
                    intent.putExtra("receiverFirstname", agentModel.getAgentFirstname());
                    intent.putExtra("receiverLastname", agentModel.getAgentLastName());
                    intent.putExtra("receiverImageUrl", agentModel.getAgentPicUrl());
                    startActivity(intent);
            }
        });

        applyRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accomodationRefundDialog.ShowRefundDialog();
                accomodationRefundDialog.setDialogActionClickListener(new AccomodationRefundDialog.OnDialogActionClickListener() {
                    @Override
                    public void onActionClicked(String message, String evidenceUrl) {
                        EstateDashboardModel estateDashboardModel = new EstateDashboardModel(accomodationListModel.getBookingId(),message,evidenceUrl,UserAccomodationDetails.this,true);
                        estateDashboardModel.requestRefund();
                        estateDashboardModel.setUpdateInfoListener(new EstateDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(UserAccomodationDetails.this, "Successfully Applied for Refund", Toast.LENGTH_SHORT).show();
                                accomodationRefundDialog.CloseRefundDialog();
                            }

                            @Override
                            public void onError(String message) {
                                Log.e("onError: ",message );
                                accomodationRefundDialog.CloseRefundDialog();
                            }
                        });
                    }
                    @Override
                    public void onEvidenceClicked() {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Attach Evidence"), PICK_IMAGE);
                    }
                });
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
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", agentModel.getAgentPhone(), null));
                    startActivity(intent);

            }
        });


    }


    private void loadPage(){
        if(accomodationListModel.getIsRefund().equalsIgnoreCase("true")){
               refundProcessingText.setVisibility(View.VISIBLE);
               applyRefund.setVisibility(View.GONE);
        }
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
                UserAccomodationDetails.this.agentModel = agentModel;
                adapter = new ProductImageScrollAdapter(mImageList, UserAccomodationDetails.this);
                adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());
                agentName.setText(agentModel.getAgentFirstname());
                imagesRecyclerview.setAdapter(adapter);
                imagesRecyclerview.setVisibility(View.VISIBLE);
                imageScrollLayout.setVisibility(View.VISIBLE);
                PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
                pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
                imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);

                beds.setText(accomodationListModel.getBeds());
                bath.setText(accomodationListModel.getBaths());
                if (accomodationListModel.getType().equalsIgnoreCase("lodges")) {
                    pricePerMonth.setText("₦" + accomodationListModel.getPricesPerMonth());
                    perTag.setText("/year");
                } else {
                    pricePerMonth.setText("₦" + accomodationListModel.getPricesPerMonth());
                    perTag.setText("/day");
                }

                Glide.with(UserAccomodationDetails.this)
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

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            File f = new File(getPath(UserAccomodationDetails.this,selectedImageUri));
            String imageName = f.getName();
            accomodationRefundDialog.setEvidenceTitle(imageName);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);
                VerificationModel verificationModel = new VerificationModel(UserAccomodationDetails.this,imageString);
                verificationModel.UploadValidId();
                verificationModel.setImageUploadListener(new VerificationModel.ImageUploadListener() {
                    @Override
                    public void onUploaded(String link) {
                        accomodationRefundDialog.setEvidenceUrl(link);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String BitmapToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        String imgString = Base64.encodeToString(b, Base64.DEFAULT);
        return imgString;
    }

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }


}