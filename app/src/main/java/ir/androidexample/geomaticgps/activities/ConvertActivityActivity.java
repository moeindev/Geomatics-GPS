package ir.androidexample.geomaticgps.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import org.gps.utils.LatLonPoint;
import org.gps.utils.ReferenceEllipsoids;
import org.gps.utils.UTMPoint;
import org.gps.utils.UTMUtils;

import ir.androidexample.geomaticgps.R;
import ir.androidexample.geomaticgps.utils.design.EnableHomeButton;
import ir.androidexample.geomaticgps.utils.font.titleChanger;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConvertActivityActivity extends AppCompatActivity {
    titleChanger changer;
    EnableHomeButton homeButton;
    TextInputLayout lat_l,lon_l,e_l,n_l,zn_l,zl_l;
    TextInputEditText lat_e,lon_e,e_e,n_e,zn_e,zl_e;
    FloatingActionButton LLtoUTM,UTMtoLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_activity);
        changer = new titleChanger(this,getSupportActionBar());
        changer.set(getString(R.string.convert));
        homeButton = new EnableHomeButton(getSupportActionBar());
        homeButton.enable();
        lat_l = (TextInputLayout) findViewById(R.id.con_lat_l);
        lon_l = (TextInputLayout) findViewById(R.id.con_lon_l);
        e_l = (TextInputLayout) findViewById(R.id.con_e_l);
        n_l = (TextInputLayout) findViewById(R.id.con_n_l);
        zn_l = (TextInputLayout) findViewById(R.id.con_zn_l);
        zl_l = (TextInputLayout) findViewById(R.id.con_zl_l);
        lat_e = (TextInputEditText) findViewById(R.id.con_lat_e);
        lon_e = (TextInputEditText) findViewById(R.id.con_lon_e);
        e_e = (TextInputEditText) findViewById(R.id.con_e_e);
        n_e = (TextInputEditText) findViewById(R.id.con_n_e);
        zn_e = (TextInputEditText) findViewById(R.id.con_zn_e);
        zl_e = (TextInputEditText) findViewById(R.id.con_zl_e);
        LLtoUTM = (FloatingActionButton) findViewById(R.id.lat_lon_to_utm);
        UTMtoLL = (FloatingActionButton) findViewById(R.id.utm_to_lat_lon);
        LLtoUTM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LLtoUTMConverter();
            }
        });
        UTMtoLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UTMtoLLConverter();
            }
        });
    }

    private void LLtoUTMConverter(){
        if (lat_e.getText().toString().isEmpty()){
            lon_l.setErrorEnabled(false);
            lat_l.setError(getString(R.string.error_empty));
        } else if (lon_e.getText().toString().isEmpty()){
            lat_l.setErrorEnabled(false);
            lon_l.setError(getString(R.string.error_empty));
        } else {
            lon_l.setErrorEnabled(false);
            lat_l.setErrorEnabled(false);
            Double lati = Double.valueOf(lat_e.getText().toString());
            Double longi = Double.valueOf(lon_e.getText().toString());
            UTMPoint point = UTMUtils.LLtoUTM(ReferenceEllipsoids.WGS_84,lati,longi);
            e_e.setText(String.valueOf(point.getEasting()));
            n_e.setText(String.valueOf(point.getNorthing()));
            zn_e.setText(String.valueOf(point.getZoneNumber()));
            zl_e.setText(String.valueOf(point.getZoneLetter()));
        }
    }

    private void UTMtoLLConverter(){
        if (e_e.getText().toString().isEmpty()){
            e_l.setError(getString(R.string.error_empty));
        } else if (n_e.getText().toString().isEmpty()){
            n_l.setError(getString(R.string.error_empty));
        } else if (zn_e.getText().toString().isEmpty()){
            zn_l.setError(getString(R.string.error_empty));
        }else if (zl_e.getText().toString().isEmpty()){
            zl_l.setError(getString(R.string.error_empty));
        }else {
            n_l.setErrorEnabled(false);
            e_l.setErrorEnabled(false);
            zn_l.setErrorEnabled(false);
            zl_l.setErrorEnabled(false);
            Double easting = Double.valueOf(e_e.getText().toString());
            Double northing = Double.valueOf(n_e.getText().toString());
            int Zone_number = Integer.parseInt(zn_e.getText().toString());
            char Zone_letter = zl_e.getText().charAt(0);
            UTMPoint utmPoint = new UTMPoint(northing,easting,Zone_number,Zone_letter);
            LatLonPoint latLonPoint = UTMUtils.UTMtoLL(ReferenceEllipsoids.WGS_84,utmPoint);
            lat_e.setText(String.valueOf(latLonPoint.getLatitude()));
            lon_e.setText(String.valueOf(latLonPoint.getLongitude()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
