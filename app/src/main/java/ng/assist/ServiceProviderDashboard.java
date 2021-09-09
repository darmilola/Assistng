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
    TextView serviceType,jobTitle,phone,rate,ratings,providerName;
    ImageView serviceTypeSelect,jobTitleSelect,phoneSelect,rateSelect;
    LinearLayout selectPortfolio,noPortfolio,scrollImageLayout;
    SwitchMaterial availabilitySwitch;
    InputDialog inputDialog;
    ListDialog listDialog;
    ScrollView rootScroll;
    ProgressBar progressBar;
    ArrayList<String> serviceTypeList = new ArrayList<>();
    ArrayList<String> rateList = new ArrayList<>();
    ServiceProviderDashboardModel serviceProviderDashboardModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_dashboard);
        initProductImageView();
    }
    private void initProductImageView(){
        populateList();
        serviceType = findViewById(R.id.service_provider_dashboard_service_type_text);
        jobTitle  = findViewById(R.id.service_provider_dashboard_job_type_text);
        phone = findViewById(R.id.service_provider_dashboard_phone_text);
        rate = findViewById(R.id.service_provider_dashboard_rate_text);
        ratings = findViewById(R.id.service_provider_dashboard_rate);
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


        availabilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isValidProvider() && !isChecked){
                    availabilitySwitch.setChecked(true);
                    ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("isAvailable","true",ServiceProviderDashboard.this);
                    mServiceProviderDashboardModel.updateProviderInfo();

                }
                else{
                    Toast.makeText(ServiceProviderDashboard.this, "Please complete your profile", Toast.LENGTH_SHORT).show();
                    ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("isAvailable","false",ServiceProviderDashboard.this);
                    mServiceProviderDashboardModel.updateProviderInfo();
                    availabilitySwitch.setChecked(false);
                }
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ServiceProviderDashboard.this);
        String firstname =  preferences.getString("firstname","");
        String lastname =  preferences.getString("lastname","");
        providerName.setText(firstname+" "+lastname);

        serviceProviderDashboardModel = new ServiceProviderDashboardModel(ServiceProviderDashboard.this);
        serviceProviderDashboardModel.ShowProvider();
        serviceProviderDashboardModel.setProviderListener(new ServiceProviderDashboardModel.ProviderListener() {
            @Override
            public void onInfoReady(ArrayList<ServiceProviderDashboardModel> serviceProviderDashboardModels, ArrayList<ServiceProviderDashboardModel.ProviderPortfolio> providerPortfolios) {
                rootScroll.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
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


            }

            @Override
            public void onErrorOccurred() {

            }
        });

        jobTitleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new InputDialog(ServiceProviderDashboard.this,"Job Title");
                inputDialog.showInputDialog();
                inputDialog.setDialogActionClickListener(new InputDialog.OnDialogActionClickListener() {
                    @Override
                    public void saveClicked(String text) {
                      ServiceProviderDashboardModel mServiceProviderDashboardModel = new ServiceProviderDashboardModel("jobTitle",text,ServiceProviderDashboard.this);
                      mServiceProviderDashboardModel.updateProviderInfo();
                      mServiceProviderDashboardModel.setUpdateInfoListener(new ServiceProviderDashboardModel.UpdateInfoListener() {
                          @Override
                          public void onSuccess() {
                              serviceProviderDashboardModel.setJobTitle(text);
                          }
                      });

                    }

                    @Override
                    public void cancelClicked() {

                    }
                });
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
                startActivityForResult(Intent.createChooser(intent, "Select Attachment"), PICK_IMAGE);
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.special_activity_background));
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.special_activity_background));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
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
        serviceTypeList.add("Children Services");
        serviceTypeList.add("Repair Services");
        rateList.add("$");
        rateList.add("$$");
        rateList.add("$$$");
    }

    private boolean isValidProvider(){
        boolean isValid = true;
        if(serviceProviderDashboardModel.getServiceType().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        if(serviceProviderDashboardModel.getJobTitle().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        if(serviceProviderDashboardModel.getPhonenumber().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        if(serviceProviderDashboardModel.getRatePerHour().equalsIgnoreCase("null")){
            isValid = false;
            return  isValid;
        }

        return  isValid;
    }

}
