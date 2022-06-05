package ng.assist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import me.relex.circleindicator.CircleIndicator2;
import ng.assist.Adapters.PortfolioAdapter;
import ng.assist.Adapters.ProductImageScrollAdapter;
import ng.assist.UIs.Utils.InputDialog;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.ServiceProviderDashboardModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServiceProviderDashboard extends AppCompatActivity {

    private static int PICK_IMAGE = 1;
    RecyclerView imagesRecyclerview;
    PortfolioAdapter adapter;
    ArrayList<String> imagesList = new ArrayList<>();
    CircleIndicator2 imagesIndicator;
    TextView serviceType,jobTitle,phone,rate,providerName,cityName;
    ImageView serviceTypeSelect,jobTitleSelect,phoneSelect,rateSelect,citySelect;
    LinearLayout selectPortfolio,noPortfolio,scrollImageLayout;
    SwitchMaterial availabilitySwitch;
    InputDialog inputDialog;
    ListDialog listDialog;
    ScrollView rootScroll;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    MaterialButton retry;
    ArrayList<String> serviceTypeList = new ArrayList<>();
    ArrayList<String> rateList = new ArrayList<>();
    ServiceProviderDashboardModel serviceProviderDashboardModel;
    ServiceProviderDashboardModel mServiceProviderModel;
    ArrayList<String> locationList = new ArrayList<>();
    ArrayList<String> edList = new ArrayList<>();
    ArrayList<String> repairList = new ArrayList<>();
    ArrayList<String> autoList = new ArrayList<>();
    ArrayList<String> personalServicesList = new ArrayList<>();
    ArrayList<String> homeServiceList = new ArrayList<>();
    ArrayList<String> generalServiceList = new ArrayList<>();
    ListDialog cityListDialog;
    MaterialButton showBookings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_dashboard);
        initProductImageView();
    }
    private void initProductImageView(){

        populateList();
        populateLocation();

        showBookings = findViewById(R.id.service_provider_dashboard_view_bookings);
        errorLayout = findViewById(R.id.error_occurred_layout_root);
        retry = findViewById(R.id.error_occurred_retry);
        cityName = findViewById(R.id.service_provider_dashboard_city);
        citySelect = findViewById(R.id.service_provider_dashboard_city_select);
        serviceType = findViewById(R.id.service_provider_dashboard_service_type_text);
        jobTitle  = findViewById(R.id.service_provider_dashboard_job_type_text);
        phone = findViewById(R.id.service_provider_dashboard_phone_text);
        rate = findViewById(R.id.service_provider_dashboard_rate_text);
        availabilitySwitch = findViewById(R.id.service_provider_dashboard_availability_switch);
        serviceTypeSelect = findViewById(R.id.service_provider_dashboard_service_type_select);
        jobTitleSelect = findViewById(R.id.service_provider_dashboard_job_type_select);
        phoneSelect = findViewById(R.id.service_provider_dashboard_phone_select);
        imagesRecyclerview = findViewById(R.id.product_image_recyclerview);
        rateSelect = findViewById(R.id.service_provider_dashboard_rate_select);
        imagesIndicator = findViewById(R.id.product_image_indicator);
        selectPortfolio = findViewById(R.id.service_provider_dashboard_select_porfolio);
        noPortfolio = findViewById(R.id.no_portfolio_available_layout);
        scrollImageLayout = findViewById(R.id.scroll_image_layout);
        rootScroll = findViewById(R.id.service_provider_scrollview);
        progressBar = findViewById(R.id.service_provider_dashboard_progress);
        providerName = findViewById(R.id.service_provider_dashboard_provider_name);


        showBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServiceProviderDashboard.this,ServiceBookingActivity.class));
            }
        });

        showProvider();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ServiceProviderDashboard.this);
        String firstname =  preferences.getString("firstname","");
        String lastname =  preferences.getString("lastname","");
        providerName.setText(firstname+" "+lastname);

        citySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityListDialog = new ListDialog(locationList,ServiceProviderDashboard.this);
                cityListDialog.showListDialog();
                cityListDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {
                        ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("city",city,ServiceProviderDashboard.this);
                        mServiceProviderDashboardModel.updateProviderInfo();
                        mServiceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                serviceProviderDashboardModel.setCity(city);
                                cityName.setText(city);
                            }
                        });
                    }
                });
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProvider();
            }
        });


        jobTitleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog listDialog = null;
                if(serviceType.getText().toString().equalsIgnoreCase("Not Available")){
                    Toast.makeText(ServiceProviderDashboard.this, "Please select service type", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(serviceType.getText().toString().equalsIgnoreCase("Home Services")){
                        listDialog = new ListDialog(homeServiceList,ServiceProviderDashboard.this);
                    }
                    else if(serviceType.getText().toString().equalsIgnoreCase("Personal Services")){
                        listDialog = new ListDialog(personalServicesList,ServiceProviderDashboard.this);
                    }
                    else if(serviceType.getText().toString().equalsIgnoreCase("Educational Services")){
                        listDialog = new ListDialog(edList,ServiceProviderDashboard.this);
                    }
                    else if(serviceType.getText().toString().equalsIgnoreCase("Repair Services")){
                        listDialog = new ListDialog(repairList,ServiceProviderDashboard.this);
                    }
                    else if(serviceType.getText().toString().equalsIgnoreCase("Auto Services")){
                        listDialog = new ListDialog(autoList,ServiceProviderDashboard.this);
                    }
                    else if(serviceType.getText().toString().equalsIgnoreCase("General Services")){
                        listDialog = new ListDialog(generalServiceList,ServiceProviderDashboard.this);
                    }
                    else{
                        listDialog = new ListDialog(homeServiceList,ServiceProviderDashboard.this);
                    }
                    listDialog.showListDialog();
                    listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                        @Override
                        public void onItemClicked(String city) {
                            ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("jobTitle",city,ServiceProviderDashboard.this);
                            mServiceProviderDashboardModel.updateProviderInfo();
                            mServiceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                                @Override
                                public void onSuccess() {
                                    serviceProviderDashboardModel.setJobTitle(city);
                                    jobTitle.setText(city);
                                }
                            });

                        }
                    });

                }
            }
        });


        phoneSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new InputDialog(ServiceProviderDashboard.this,"Phone");
                inputDialog.showInputDialog();

                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {
                        ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("phonenumber",text,ServiceProviderDashboard.this);
                        mServiceProviderDashboardModel.updateProviderInfo();
                        mServiceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                serviceProviderDashboardModel.setPhonenumber(text);
                                phone.setText(text);
                            }
                        });

                    }

                    @Override
                    public void cancelClicked() {

                    }
                });
            }
        });

        serviceTypeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(serviceTypeList,ServiceProviderDashboard.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {

                        ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("type",city,ServiceProviderDashboard.this);
                        mServiceProviderDashboardModel.updateProviderInfo();
                        mServiceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                serviceProviderDashboardModel.setServiceType(city);
                                serviceType.setText(city);
                            }
                        });


                    }
                });
            }
        });

        rateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog = new ListDialog(rateList,ServiceProviderDashboard.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {
                        ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("ratePerHour",city,ServiceProviderDashboard.this);
                        mServiceProviderDashboardModel.updateProviderInfo();
                        mServiceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                            @Override
                            public void onSuccess() {
                                serviceProviderDashboardModel.setRatePerHour(city);
                                rate.setText(city);
                            }
                        });
                    }
                });
            }
        });

        selectPortfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Portfolio"), PICK_IMAGE);
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
                ServiceProviderDashboardModel serviceProviderDashboardModel = new ServiceProviderDashboardModel(imageString,ServiceProviderDashboard.this);
                serviceProviderDashboardModel.uploadImage();
                serviceProviderDashboardModel.setPortfolioUploadListener(new ServiceProviderDashboardModel.PortfolioUploadListener() {
                    @Override
                    public void onImageUpload(ServiceProviderDashboardModel.ProviderPortfolio providerPortfolio) {
                        adapter.addItem(providerPortfolio);
                        imagesRecyclerview.scrollToPosition(adapter.getItemCount()-1);
                        scrollImageLayout.setVisibility(View.VISIBLE);
                        noPortfolio.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String message) {

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void populateList(){
        serviceTypeList.add("Home Services");
        serviceTypeList.add("Personal Services");
        serviceTypeList.add("Educational Services");
        serviceTypeList.add("Repair Services");
        serviceTypeList.add("Auto Services");
        serviceTypeList.add("General Services");

        rateList.add("Min 500");
        rateList.add("Min 1000");
        rateList.add("Min 5000");
        rateList.add("Min 10000");
        rateList.add("Min 20000");

        edList.add("Home Lesson ");
        edList.add("STEM Tutor");
        edList.add("Training Centre");

        personalServicesList.add("Hair Salon");
        personalServicesList.add("Barber Shop");
        personalServicesList.add("Makeup artist");
        personalServicesList.add("Massage Therapy");

        homeServiceList.add("Laundry Service");
        homeServiceList.add("Cooking-Gas Refill");
        homeServiceList.add("Electrical Repair");
        homeServiceList.add("Furniture Repair");

        generalServiceList.add("General Services");

        repairList.add("AC repair");
        repairList.add("Electronics repair");
        repairList.add("Fridge repair");
        repairList.add("Plumbing ");

        autoList.add("Benz Autotech");
        autoList.add("Toyota Autotech");
        autoList.add("General Auto mechanic");
        autoList.add("Spare parts dealer");
        autoList.add("Driving School");


    }

    private boolean isValidProvider(){
        boolean isValid = true;
        if(mServiceProviderModel.getServiceType().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        if(mServiceProviderModel.getJobTitle().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        if(mServiceProviderModel.getPhonenumber().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        if(mServiceProviderModel.getRatePerHour().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        if(mServiceProviderModel.getCity().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        return  isValid;
    }


    private void showProvider(){

        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        serviceProviderDashboardModel = new ServiceProviderDashboardModel(ServiceProviderDashboard.this);
        serviceProviderDashboardModel.ShowProvider();
        serviceProviderDashboardModel.setProviderListener(new ServiceProviderDashboardModel.ProviderListener() {
            @Override
            public void onInfoReady(ArrayList<ServiceProviderDashboardModel> serviceProviderDashboardModels, ArrayList<ServiceProviderDashboardModel.ProviderPortfolio> providerPortfolios) {
                mServiceProviderModel = serviceProviderDashboardModels.get(0);
                rootScroll.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                if(serviceProviderDashboardModels.get(0).getServiceType().equalsIgnoreCase("null")){
                    serviceType.setText("Not Available");
                }
                else{
                    serviceType.setText(serviceProviderDashboardModels.get(0).getServiceType());
                }
                if(serviceProviderDashboardModels.get(0).getJobTitle().equalsIgnoreCase("null")){
                    jobTitle.setText("Not Available");
                }
                else{
                    jobTitle.setText(serviceProviderDashboardModels.get(0).getJobTitle());
                }

                if(serviceProviderDashboardModels.get(0).getCity().equalsIgnoreCase("null")){
                    cityName.setText("Not Available");
                }
                else{
                    cityName.setText(serviceProviderDashboardModels.get(0).getCity());
                }

                if(serviceProviderDashboardModels.get(0).getPhonenumber().equalsIgnoreCase("null")){
                    phone.setText("Not Available");
                }
                else{
                    phone.setText(serviceProviderDashboardModels.get(0).getPhonenumber());
                }

                if(serviceProviderDashboardModels.get(0).getRatePerHour().equalsIgnoreCase("null")){
                    rate.setText("Not Available");
                }
                else{
                    rate.setText(serviceProviderDashboardModels.get(0).getRatePerHour());
                }

                if(serviceProviderDashboardModels.get(0).getIsAvailable().equalsIgnoreCase("false")){
                    availabilitySwitch.setChecked(false);
                }
                else{
                    availabilitySwitch.setChecked(true);
                }

                if(providerPortfolios.size() == 0){
                    scrollImageLayout.setVisibility(View.GONE);
                    noPortfolio.setVisibility(View.VISIBLE);
                    adapter = new PortfolioAdapter(providerPortfolios,ServiceProviderDashboard.this);
                }
                else{
                    scrollImageLayout.setVisibility(View.VISIBLE);
                    noPortfolio.setVisibility(View.GONE);
                    PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
                    adapter = new PortfolioAdapter(providerPortfolios,ServiceProviderDashboard.this);
                    LinearLayoutManager imagesManager = new LinearLayoutManager(ServiceProviderDashboard.this, LinearLayoutManager.HORIZONTAL,false);
                    imagesRecyclerview.setLayoutManager(imagesManager);
                    imagesRecyclerview.setAdapter(adapter);
                    pagerSnapHelper.attachToRecyclerView(imagesRecyclerview);
                    imagesIndicator.attachToRecyclerView(imagesRecyclerview, pagerSnapHelper);
                    adapter.registerAdapterDataObserver(imagesIndicator.getAdapterDataObserver());
                }

                availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isValidProvider() && isVerified() && isChecked){
                            Toast.makeText(ServiceProviderDashboard.this, "You are now Available for hiring", Toast.LENGTH_SHORT).show();
                            ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("isAvailable","true",ServiceProviderDashboard.this);
                            mServiceProviderDashboardModel.updateProviderInfo();
                            mServiceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else if(!isVerified()){
                            Toast.makeText(ServiceProviderDashboard.this, "Please Get Verified To Be Available", Toast.LENGTH_SHORT).show();
                            availabilitySwitch.setChecked(false);
                        }
                        else if(!isValidProvider()){
                            Toast.makeText(ServiceProviderDashboard.this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                            availabilitySwitch.setChecked(false);
                        }
                        else if(!isChecked){
                            ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("isAvailable","false",ServiceProviderDashboard.this);
                            mServiceProviderDashboardModel.updateProviderInfo();
                            mServiceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(ServiceProviderDashboard.this, "You are no more available for hiring", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onErrorOccurred(String message) {
                progressBar.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void populateLocation(){
        locationList.add("Lagos");
        locationList.add("Abuja");
        locationList.add("Kano");

        String[] stateList = {"Abia","Adamawa","Akwa Ibom","Anambra","Bauchi","Bayelsa","Benue","Borno","Cross River","Delta","Ebonyi","Edo","Ekiti","Enugu","Gombe","Imo","Jigawa","Kaduna","Kano","Katsina","Kebbi"
                ,"Kogi","Kwara","Lagos","Nasarawa","Niger","Ogun","Ondo","Osun","Oyo","Plateau","Rivers","Sokoto","Taraba","Yobe","Zamfara"};

        for (String city : stateList) {
            locationList.add(city);
        }
    }

    private boolean isVerified(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ServiceProviderDashboard.this);
        String status = preferences.getString("verificationStatus","notVerified");

        if(status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("notVerified")){
            return false;
        }
        else{
            return true;
        }

    }

}
