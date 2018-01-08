package ir.androidexample.geomaticgps.utils.font;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by moein on 12/13/17.
 */

public class titleChanger {
    private Context context;
    private ActionBar actionBar;

    public titleChanger(Context c, ActionBar bar){
        this.context = c;
        this.actionBar = bar;
    }
    @SuppressLint("ResourceType")
    public void set(String Title){
        TextView appn = new TextView(context);
        appn.setText("   "+Title);
        appn.setGravity(Gravity.VERTICAL_GRAVITY_MASK);
        appn.setTextSize(18);
        appn.setTextColor(context.getResources().getColor(android.support.v7.appcompat.R.color.abc_secondary_text_material_dark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            appn.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.RIGHT );
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(appn,params);
    }
}
