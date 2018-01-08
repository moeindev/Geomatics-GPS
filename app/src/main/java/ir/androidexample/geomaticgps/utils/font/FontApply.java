package ir.androidexample.geomaticgps.utils.font;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by moein on 12/12/17.
 */

public class FontApply extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath("orgFont.ttf")
        .setFontAttrId(uk.co.chrisjenx.calligraphy.R.attr.fontPath)
        .build());
    }
}
