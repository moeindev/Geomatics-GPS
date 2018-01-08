package ir.androidexample.geomaticgps.utils.prefrence;

import android.content.Context;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by moein on 12/13/17.
 */

public class FirstTimeChecker {

    private Context context;
    private String TAG = "PREFERENCES";

    public FirstTimeChecker(Context c){
        this.context = c;
    }

    public void ChangeFirstTime(Boolean Change){
        context.getSharedPreferences(TAG,MODE_PRIVATE).edit().putBoolean("FirstTime",Change).apply();
        Log.d("First time Status","First time status updated to : "+String.valueOf(Change));
    }

    public Boolean IsFirstTime(){
        boolean l = context.getSharedPreferences(TAG,MODE_PRIVATE).getBoolean("FirstTime",true);
        Log.d("First time Status",String.valueOf(l));
        return l;
    }
}
