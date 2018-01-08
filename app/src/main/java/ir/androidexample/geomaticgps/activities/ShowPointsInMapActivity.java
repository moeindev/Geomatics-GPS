package ir.androidexample.geomaticgps.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ir.androidexample.geomaticgps.R;
import ir.androidexample.geomaticgps.objects.PointObject;
import ir.androidexample.geomaticgps.utils.database.userDatabaseHelper;


public class ShowPointsInMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static String PROJECT_NAME;
    userDatabaseHelper helper;
    Cursor cursor;
    ArrayList<PointObject> Pobjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_points_in_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        helper = new userDatabaseHelper(this);
        cursor = helper.getRows(PROJECT_NAME);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GetPoints getPoints = new GetPoints();
        getPoints.execute();
    }

    private class GetPoints extends AsyncTask<Object,Object,ArrayList<PointObject>>{
        @Override
        protected ArrayList<PointObject> doInBackground(Object... objects) {
            ArrayList<PointObject> myObjects = new ArrayList<PointObject>();
            if (cursor.moveToFirst()){
                do {
                    PointObject object = new PointObject();
                    object.setId(cursor.getString(0));
                    object.setLatitude(cursor.getString(1));
                    object.setLongitude(cursor.getString(2));
                    object.setDescription(cursor.getString(3));
                    myObjects.add(object);
                }while (cursor.moveToNext());
            }
            cursor.close();
            return myObjects;
        }

        @Override
        protected void onPostExecute(ArrayList<PointObject> pointObjects) {
            super.onPostExecute(pointObjects);
            Pobjects = pointObjects;
            ShowPoints points = new ShowPoints();
            points.execute();
        }
    }
    private class ShowPoints extends AsyncTask<Object,Object,List<MarkerOptions>>{

        @Override
        protected List doInBackground(Object... objects) {
            List<MarkerOptions> options = new ArrayList<>();
            for (int i = 0;i<Pobjects.size();i++){
                PointObject object = (PointObject) Pobjects.get(i);
                LatLng po = new LatLng(Double.valueOf(object.getLatitude()),Double.valueOf(object.getLongitude()));
                MarkerOptions option = new MarkerOptions()
                        .title(object.getId())
                        .position(po)
                        .snippet("Latitude : "+object.getLatitude()+"\n"+"Longitude : "+object.getLongitude());
                options.add(option);
            }
            return options;
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> markerOptions) {
            super.onPostExecute(markerOptions);
            for (int i = 0;i<markerOptions.size();i++){
                mMap.addMarker(markerOptions.get(i));
            }
        }
    }
}
