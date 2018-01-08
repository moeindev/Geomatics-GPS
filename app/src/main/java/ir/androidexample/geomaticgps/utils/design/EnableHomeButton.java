package ir.androidexample.geomaticgps.utils.design;

import android.support.v7.app.ActionBar;

/**
 * Created by moein on 12/14/17.
 */

public class EnableHomeButton {
    private ActionBar actionBar;

    public EnableHomeButton(ActionBar bar){
        this.actionBar = bar;
    }

    public void enable(){
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }
}
