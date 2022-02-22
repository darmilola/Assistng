package ng.assist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ng.assist.UIs.Utils.ListDialog;
import ng.assist.UIs.ViewModel.CabHailingModel;
import ng.assist.UIs.ViewModel.LocationModel;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.button.MaterialButton;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BuildLocation extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
{

    MaterialButton next;
    LinearLayout flightBooking;
    LinearLayout chooseLocationFrom,chooseLocationTo;
    TextView chooseLocationFromText, chooseLocationToText;
    ListDialog listDialog;
    LinearLayout rootLayout;
    ProgressBar progressBar;
    ArrayList<String> locationList = new ArrayList<>();
    ImageView navBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_location);
        initView();

    }


    private void initView(){

        populateLocation();
        navBack = findViewById(R.id.nav_back);
        progressBar = findViewById(R.id.build_location_progress);
        progressBar.setVisibility(View.GONE);
        rootLayout = findViewById(R.id.build_location_root);
        rootLayout.setVisibility(View.VISIBLE);
        chooseLocationFromText = findViewById(R.id.choose_location_from_text);
        chooseLocationToText = findViewById(R.id.choose_location_to_text);
        chooseLocationFrom = findViewById(R.id.choose_location_from);
        chooseLocationTo = findViewById(R.id.choose_location_to);

        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chooseLocationTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog listDialog = new ListDialog(locationList,BuildLocation.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {
                        chooseLocationToText.setText(city);
                    }
                });
            }
        });

        chooseLocationFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialog listDialog = new ListDialog(locationList,BuildLocation.this);
                listDialog.showListDialog();
                listDialog.setItemClickedListener(new ListDialog.OnCityClickedListener() {
                    @Override
                    public void onItemClicked(String city) {
                        chooseLocationFromText.setText(city);
                    }
                });

            }
        });


        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //buildGoogleApiClient();
        //buildLocationSettingsRequest();
        //checkLocationSettings();

        next = findViewById(R.id.build_root_next);
        flightBooking = findViewById(R.id.flight_booking);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((!chooseLocationFromText.getText().toString().equalsIgnoreCase("")) && (!chooseLocationToText.getText().toString().equalsIgnoreCase("")) ){
                    Intent intent =  new Intent(BuildLocation.this,CabHailingActivity.class);
                    intent.putExtra("from",chooseLocationFromText.getText().toString());
                    intent.putExtra("to",chooseLocationToText.getText().toString());
                    startActivity(intent);
                }

            }
        });

    /*    CabHailingModel cabHailingModel = new CabHailingModel();
        cabHailingModel.DisplayAssistLocation();
        cabHailingModel.setLocationReadyListener(new CabHailingModel.LocationReadyListener() {
            @Override
            public void onReady(ArrayList<LocationModel> retailerLocation) {
                progressBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                for (LocationModel locationModel: retailerLocation) {
                     locationList.add(locationModel.getCity());
                }
            }
            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                rootLayout.setVisibility(View.GONE);
                Toast.makeText(BuildLocation.this, message, Toast.LENGTH_SHORT).show();
            }
        });*/
    }


    public boolean isNextValid(){
        boolean isValid = true;
        if(TextUtils.isEmpty(chooseLocationFromText.getText().toString())){
            chooseLocationFromText.setError("Required");
            isValid = false;
            return isValid;
        }
        if(TextUtils.isEmpty(chooseLocationToText.getText().toString())){
            chooseLocationToText.setError("Required");
            isValid = false;
            return isValid;
        }
        return isValid;
    }

  /*  private void startLocationUpdates(){


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted

            } else {

                checkLocationPermission();

            }
        }
        else {

        }
    }*/


   /* private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(BuildLocation.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }*/

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                    }

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION );


                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }*/






    private void saveUsersCity(String cityname){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(BuildLocation.this);
        preferences.edit().putString("city",cityname).apply();
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

}
