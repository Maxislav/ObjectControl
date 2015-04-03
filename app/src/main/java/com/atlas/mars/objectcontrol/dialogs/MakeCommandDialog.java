package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.view.View;

import com.atlas.mars.objectcontrol.MakeCommandActivity;

import java.util.HashMap;

/**
 * Created by mars on 4/3/15.
 */
public class MakeCommandDialog extends MyDialog {

    public MakeCommandDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void doChange(View v, HashMap<String,String> map) {
        MakeCommandActivity activity1 = (MakeCommandActivity)activity;
        activity1.setCheckbox(v, map);

    }

    public interface GiftCheckbox {
        public void setCheckbox(View v, HashMap<String, String> map);
    }
}
