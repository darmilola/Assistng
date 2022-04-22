package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.Utils.AccomodationRefundDialog;
import ng.assist.UIs.ViewModel.AccomodationListModel;
import ng.assist.UIs.ViewModel.AgentModel;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.ProductImageModel;
import ng.assist.UIs.ViewModel.VerificationModel;

public class AgentAccomodationDetails extends AppCompatActivity {

    static int PICK_IMAGE = 1;
    RecyclerView imagesRecyclerview;
    ProductImageScrollAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView houseTitle,pricePerMonth,adddress,description,bookingFee;
    ProgressBar loadingBar;
    NestedScrollView rootLayout;
    AccomodationListModel accomodationListModel;
    String houseId, agentId;
    MaterialButton deleteListing,applyRefund;
    LinearLayout imageScrollLayout;
    AccomodationRefundDialog accomodationRefundDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_accomodation_details);
        initView();
    }

    private void initView(){
        applyRefund = findViewById(R.id.agent_acc_apply_refund);
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
        accomodationRefundDialog = new AccomodationRefundDialog(this);
        agentId = accomodationListModel.getAgentId();
        houseId = accomodationListModel.getHouseId();

        houseTitle.setText(accomodationListModel.getHouseTitle());
        pricePerMonth.setText(accomodationListModel.getPricesPerMonth());
        adddress.setText(accomodationListModel.getAddress());
        description.setText(accomodationListModel.getHouseDesc());

        if(!accomodationListModel.getAgentReason().equalsIgnoreCase("null")){
            applyRefund.setVisibility(View.GONE);
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

        bookingFee.setText(formattedPrice);





        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        LinearLayoutManager imagesManager = new LinearLayoutManager(AgentAccomodationDetails.this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);

        EstateDashboardModel estateDashboardModel = new EstateDashboardModel(houseId,agentId);
        estateDashboardModel.getAccomodationDetails();
        estateDashboardModel.setAccomodationDetailsListener(new EstateDashboardModel.AccomodationDetailsListener() {
            @Override
            public void onDetailsReady(ArrayList<ProductImageModel> imageList, AgentModel agentModel) {

                rootLayout.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
                imageScrollLayout.setVisibility(View.VISIBLE);
                adapter = new ProductImageScrollAdapter(imageList,AgentAccomodationDetails.this);
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
                Toast.makeText(AgentAccomodationDetails.this, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });

        applyRefund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accomodationRefundDialog.ShowRefundDialog();
                accomodationRefundDialog.setDialogActionClickListener(new AccomodationRefundDialog.OnDialogActionClickListener() {
                    @Override
                    public void onActionClicked(String message, String evidenceUrl) {
                        EstateDashboardModel estateDashboardModel = new EstateDashboardModel(accomodationListModel.getBookingId(),message,evidenceUrl,AgentAccomodationDetails.this);
                        estateDashboardModel.agentRequestRefund();
                        estateDashboardModel.setUpdateInfoListener(new EstateDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(AgentAccomodationDetails.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                                accomodationRefundDialog.CloseRefundDialog();
                            }

                            @Override
                            public void onError(String message) {
                                Log.e("onError: ",message);
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            File f = new File(getPath(this,selectedImageUri));
            String imageName = f.getName();
            accomodationRefundDialog.setEvidenceTitle(imageName);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);
                VerificationModel verificationModel = new VerificationModel(this,imageString);
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