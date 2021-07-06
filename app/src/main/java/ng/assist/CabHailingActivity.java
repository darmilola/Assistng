package ng.assist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import ng.assist.Adapters.RideDisplayAdapter;
import ng.assist.UIs.model.DirectionsJSONParser;
import ng.assist.UIs.model.ZoomCenterCardLayoutManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CabHailingActivity extends AppCompatActivity {


    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationSettingsRequest mLocationSettingsRequest;
    LocationSettingsRequest.Builder builder;
    FusedLocationProviderClient mFusedLocationClient;
    static final int REQUEST_CHECK_SETTINGS = 0x1;
    static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    RecyclerView recyclerView;
    TextView currentLocationTextView;
    ArrayList<String> rideList = new ArrayList<>();
    RideDisplayAdapter rideDisplayAdapter;
    LinearLayout rideLayout;
    LinearLayout locationLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_hailing);
        initView();

    }

    private void initView(){


        rideLayout = findViewById(R.id.ride_display_layout);
        locationLayout = findViewById(R.id.cab_hailing_location_selection_layout);
        currentLocationTextView = findViewById(R.id.current_location_name);
        currentLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             rideLayout.setVisibility(View.GONE);
             locationLayout.setVisibility(View.VISIBLE);
            }
        });
        recyclerView = findViewById(R.id.ride_display_recyclerview);
        rideList = new ArrayList<>();
        for(int i = 0; i < 20; i++){
            rideList.add("");
        }

        rideDisplayAdapter = new RideDisplayAdapter(rideList,CabHailingActivity.this);
        recyclerView.setAdapter(rideDisplayAdapter);
        ZoomCenterCardLayoutManager zoomCenterCardLayoutManager = new ZoomCenterCardLayoutManager(CabHailingActivity.this,LinearLayout.HORIZONTAL,false);
        recyclerView.setLayoutManager(zoomCenterCardLayoutManager);

    }








    @Override
    public void onResume() {

        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          //  getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.transparent));
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }
}
