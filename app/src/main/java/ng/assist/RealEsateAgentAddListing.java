package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.EstateDashboardImageAdapter;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.EstateDashboardModel;
import ng.assist.UIs.ViewModel.ServiceProviderDashboardModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RealEsateAgentAddListing extends AppCompatActivity {

    private static int PICK_IMAGE = 1;
    RecyclerView imagesRecyclerview;
    ArrayList<String> imagesList = new ArrayList<>();
    ArrayList<String> accommodationTypeList = new ArrayList<>();
    ArrayList<String> cityList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    EditText title,pricePerMonth,address,bed,bath,bookingFee,description;
    TextView type,city;
    String mTitle = "",mPricePerMonth = "",mAddress = "",mCity = "",mBed = "",mBath = "",mBookingFee = "",mDescription = "",mType = "";
    String houseId = "";
    String displayImage = "";
    String mAvailability = "false";
    MaterialCheckBox availability;
    LinearLayout cancel,saveListing;
    LinearLayout scrollImageLayout;
    ListDialog listDialog;
    MaterialButton uploadImageButton;
    ArrayList<EstateDashboardModel.HouseImage> houseImageArrayList = new ArrayList<>();
    String userEmail;
    EstateDashboardImageAdapter estateDashboardImageAdapter = new EstateDashboardImageAdapter(houseImageArrayList,RealEsateAgentAddListing.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_esate_agent_add_listing);
        initView();
    }

    private void initView(){
        populateList();
        uploadImageButton = findViewById(R.id.real_estate_add_image);
        scrollImageLayout = findViewById(R.id.scroll_image_layout);
        cancel = findViewById(R.id.real_estate_add_listing_cancel);
        saveListing = findViewById(R.id.real_estate_add_listing_save);
        title = findViewById(R.id.real_estate_add_listing_title);
        pricePerMonth = findViewById(R.id.real_estate_add_listing_price);
        address = findViewById(R.id.real_estate_add_listing_addess);
        city = findViewById(R.id.real_estate_add_listing_city);
        bed = findViewById(R.id.real_estate_add_listing_bed);
        bath = findViewById(R.id.real_estate_add_listing_bath);
        bookingFee = findViewById(R.id.real_estate_add_listing_booking_fee);
        description = findViewById(R.id.real_estate_add_listing_desc);
        type = findViewById(R.id.real_estate_add_listing_type);
        availability = findViewById(R.id.real_estate_add_listing_availability);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userEmail =  preferences.getString("userEmail","");

        LinearLayoutManager imagesManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        imagesRecyclerview.setLayoutManager(imagesManager);
        imagesRecyclerview.setAdapter(estateDashboardImageAdapter);
        pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
        imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
        estateDashboardImageAdapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());

        saveListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitle = title.getText().toString().trim();
                mPricePerMonth = pricePerMonth.getText().toString().trim();
                mAddress = address.getText().toString().trim();
                mCity = city.getText().toString().trim();
                mType = type.getText().toString().trim();
                mBath = bath.getText().toString().trim();
                mBed = bed.getText().toString().trim();
                mBookingFee = bookingFee.getText().toString().trim();
                mDescription = description.getText().toString().trim();
                if(availability.isChecked()){
                    mAvailability = "true";
                }
                else{
                    mAvailability = "false";
                }

                if(isValidInput()){
                    EstateDashboardModel estateDashboardModel = new EstateDashboardModel(RealEsateAgentAddListing.this,houseId,mTitle,mPricePerMonth,mCity,mBookingFee,Integer.parseInt(mBath),Integer.parseInt(mBed),mAddress,displayImage,mDescription,mType,mAvailability,userEmail);
                    estateDashboardModel.createAgentListing();
                    estateDashboardModel.setCreateListingListener(new EstateDashboardModel.CreateListingListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(RealEsateAgentAddListing.this, "Listing added successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(RealEsateAgentAddListing.this, "Error Occurred please try again", Toast.LENGTH_SHORT).show();

                        }
                    });
                }




            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(cityList,RealEsateAgentAddListing.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String item) {
                         city.setText(item);
                    }
                });
            }
        });

        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(accommodationTypeList,RealEsateAgentAddListing.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String item) {
                          type.setText(item);
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

    private void populateList(){
        cityList.add("Kano");
        cityList.add("Lagos");
        cityList.add("Abuja");
        accommodationTypeList.add("Employee");
        accommodationTypeList.add("Corpers");
        accommodationTypeList.add("others");
    }
    private boolean isValidInput(){
        boolean isValid = true;

        if(TextUtils.isEmpty(mTitle)){
            isValid = false;
            title.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mPricePerMonth)){
            isValid = false;
            pricePerMonth.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mAddress)){
            isValid = false;
            address.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mCity)){
            isValid = false;
            city.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mBed)){
            isValid = false;
            bed.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mBath)){
            isValid = false;
            bath.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mBookingFee)){
            isValid = false;
            bookingFee.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mDescription)){
            isValid = false;
            description.setError("Required");
            return isValid;
        }
        if(TextUtils.isEmpty(mType)){
            isValid = false;
            type.setError("Required");
            return isValid;
        }
        if(displayImage.equalsIgnoreCase("")){
            isValid = false;
            Toast.makeText(this, "Upload at least one image", Toast.LENGTH_SHORT).show();
            return isValid;
        }
        return isValid;
    }

    // function to generate a random string of length n
    static String generateHouseId()
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

    public String BitmapToString(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        String imgString = Base64.encodeToString(b, Base64.DEFAULT);
        return imgString;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                String imageString = BitmapToString(bitmap);

                if(houseId.equalsIgnoreCase("")){
                    houseId = generateHouseId();
                }

                EstateDashboardModel estateDashboardModel = new EstateDashboardModel(imageString,houseId,RealEsateAgentAddListing.this, 0);
                estateDashboardModel.uploadImage();
                estateDashboardModel.setHouseUploadListener(new EstateDashboardModel.HouseUploadListener() {
                    @Override
                    public void onUploadSuccessful(EstateDashboardModel.HouseImage houseImage) {
                        if(displayImage.equalsIgnoreCase("")){
                            displayImage = houseImage.getImageUrl();
                        }
                        estateDashboardImageAdapter.addItem(houseImage);
                        estateDashboardImageAdapter.notifyDataSetChanged();
                        scrollImageLayout.setVisibility(View.VISIBLE);
                        imagesRecyclerview.scrollToPosition(estateDashboardImageAdapter.getItemCount()-1);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(RealEsateAgentAddListing.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
