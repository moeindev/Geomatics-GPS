package ir.androidexample.geomaticgps.utils.prefrence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import ir.androidexample.geomaticgps.R;

/**
 * Created by moein on 12/16/17.
 */

public class GetUserSettings {
    private Context context;
    private SharedPreferences SP;
    public GetUserSettings(Context c){
        this.context = c;
        SP = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public String GetShowCoordinateSystem(){
        String show = SP.getString(context.getString(R.string.list_preference_show_coordinate_system_key),"1");
        Log.d("Show Coordinate id",show);
        return show;
    }
    public String GetSaveCoordinateSystem(){
        String save = SP.getString(context.getString(R.string.list_preference_save_coordinate_system_key),"1");
        Log.d("Show Coordinate id",save);
        return save;
    }
}
