package ir.androidexample.geomaticgps.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.gps.utils.LatLonPoint;
import org.gps.utils.LatLonUtils;
import org.gps.utils.ReferenceEllipsoids;
import org.gps.utils.UTMPoint;
import org.gps.utils.UTMUtils;

import ir.androidexample.geomaticgps.R;
import ir.androidexample.geomaticgps.utils.database.userDatabaseHelper;
import ir.androidexample.geomaticgps.utils.design.EnableHomeButton;
import ir.androidexample.geomaticgps.utils.font.titleChanger;
import ir.androidexample.geomaticgps.utils.permission.CheckLocationPermission;
import ir.androidexample.geomaticgps.utils.prefrence.GetUserSettings;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    SeekBar getIdleTime;
    TextView lat_show, lon_show, loc_distance, loc_status, tv_time;
    Button btn_start, btn_stop;
    GoogleApiClient apiClient;
    int idleTime = 5;
    LocationRequest locationRequest;
    userDatabaseHelper helper;
    GetUserSettings userSettings;
    public static String PROJECT_NAME;
    titleChanger changer;
    EnableHomeButton homeButton;
    int SHoW;
    CheckLocationPermission locationPermission;
    FusedLocationProviderClient fusedLocationProviderClient;
    mLocationCallBack callBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getIdleTime = (SeekBar) findViewById(R.id.time_idle_seek);
        tv_time = (TextView) findViewById(R.id.tv_time_show);
        lat_show = (TextView) findViewById(R.id.tv_lat_show);
        lon_show = (TextView) findViewById(R.id.tv_lon_show);
        loc_distance = (TextView) findViewById(R.id.tv_dis_show);
        loc_status = (TextView) findViewById(R.id.loc_status_tv);
        btn_start = (Button) findViewById(R.id.btn_start_listening);
        btn_stop = (Button) findViewById(R.id.btn_stop_listening);
        userSettings = new GetUserSettings(this);
        SHoW = Integer.parseInt(userSettings.GetShowCoordinateSystem());
        helper = new userDatabaseHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        btn_stop.setEnabled(false);
        btn_start.setEnabled(false);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartLocationRequest();
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopLocationRequest();
            }
        });
        callBack = new mLocationCallBack();
        getIdleTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int po = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_time.setText(getString(R.string.time_idle_tx) + String.valueOf(po) + "ثانیه");
                po = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_time.setText(getString(R.string.time_idle_tx) + String.valueOf(po) + "ثانیه");
                idleTime = po;
                Log.d("idle Time", String.valueOf(idleTime));
            }
        });
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (!apiClient.isConnected()) {
            apiClient.connect();
        }
        changer = new titleChanger(this, getSupportActionBar());
        changer.set(getString(R.string.title_loc) + PROJECT_NAME);
        homeButton = new EnableHomeButton(getSupportActionBar());
        homeButton.enable();
        locationPermission = new CheckLocationPermission(this, this);
        locationPermission.CheckItOut();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
        checkIt();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIt();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopLocationRequest();
    }

    private void checkIt() {
        if (isLocationEnabled(this)) {
            btn_start.setEnabled(true);
            btn_stop.setEnabled(true);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_gps_title)
                    .setMessage(R.string.dialog_gps_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_gps_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(settings, 0);
                        }
                    }).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void StartLocationRequest() {
        loc_status.setText(R.string.lbl_started);
        loc_status.setTextColor(Color.GREEN);
        getIdleTime.setEnabled(false);
        btn_start.setEnabled(false);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(idleTime * 1000);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,callBack,null);
    }

    private void StopLocationRequest(){
        fusedLocationProviderClient.removeLocationUpdates(callBack);
        finish();
    }
    boolean checkFirst = false;
    LatLonPoint StartLatLon;
    LatLonPoint NextLatLon;
    Double Distance = 0.0;
    String TAG = "google API client";

    private class mLocationCallBack extends LocationCallback{
        @Override
        public void onLocationResult(LocationResult locationResult) {
            android.location.Location location = locationResult.getLastLocation();
            if (SHoW == 1){
                lat_show.setText("Latitude:"+String.valueOf(location.getLatitude()));
                lon_show.setText("Longitude:"+String.valueOf(location.getLongitude()));
            }else if (SHoW == 2){
                LatLonPoint point = new LatLonPoint(Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()));
                UTMPoint utm = UTMUtils.LLtoUTM(ReferenceEllipsoids.WGS_84, point.getLatitude(), point.getLongitude());
                lat_show.setText(String.valueOf(utm.getEasting()));
                lon_show.setText(String.valueOf(utm.getNorthing()));
            }
            if (!checkFirst){
                StartLatLon = new LatLonPoint(location.getLatitude(),location.getLongitude());
                checkFirst = true;
            }
            NextLatLon = new LatLonPoint(location.getLatitude(),location.getLongitude());
            if (Distance == 0.0){
                Distance = LatLonUtils.getHaversineDistance(StartLatLon, NextLatLon);
            }else {
                Distance = Distance + LatLonUtils.getHaversineDistance(StartLatLon, NextLatLon);
            }
            loc_distance.setText("فاصله"+String.valueOf(Distance)+"متر");
            Log.d("location ac",String.valueOf(location.getAccuracy()));
            helper.AddRow(PROJECT_NAME,String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),"Description");
            super.onLocationResult(locationResult);
        }
    }
    private static boolean isLocationEnabled(Context context){
        int LocationMode = 0;
        String LocationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                LocationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return LocationMode != Settings.Secure.LOCATION_MODE_OFF;
        }else {
            LocationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(LocationProviders);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        btn_start.setEnabled(true);
        btn_stop.setEnabled(true);
        Log.d(TAG,"Connected!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"Failed");
    }

}
