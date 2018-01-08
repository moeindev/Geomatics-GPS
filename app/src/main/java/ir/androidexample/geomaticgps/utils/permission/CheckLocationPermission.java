package ir.androidexample.geomaticgps.utils.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import ir.androidexample.geomaticgps.R;

/**
 * Created by moein on 12/13/17.
 */

public class CheckLocationPermission implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Context context;
    private Activity activity;

    public CheckLocationPermission(Context c, Activity activity){
        this.context = c;
        this.activity = activity;
    }

    public void CheckItOut(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 12235);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, 12235);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 12235: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Location access","Permission Granted");
                } else {
                    new AlertDialog.Builder(context)
                            .setMessage(context.getString(R.string.dialog_permission_message))
                            .setCancelable(false)
                            .setNegativeButton(context.getString(R.string.dialog_permission_button), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    CheckItOut();
                                }
                            })
                            .show();
                }
                return;
            }
        }
    }
}
