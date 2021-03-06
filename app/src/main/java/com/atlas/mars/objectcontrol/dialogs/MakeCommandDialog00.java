package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.view.View;

import com.atlas.mars.objectcontrol.MakeCommandActivity;

import java.util.HashMap;

/**
 * Created by mars on 4/3/15.
 */
public class MakeCommandDialog00 extends MyDialog00 {
    MakeCommandActivity activity1;

    public MakeCommandDialog00(Activity activity) {
        super(activity);
        activity1 = (MakeCommandActivity)activity;
    }

    @Override
    protected void doChange(View v, HashMap<String,String> map) {
        activity1.setCheckbox(v, map);

    }
    @Override
    protected void clickBtnOk(){
        activity1.clickBtnOk();
    }

    @Override
    protected void clickBtnCancel(){
        activity1.clickBtnCancel();
    }

    public interface GiftCheckbox {
        public void setCheckbox(View v, HashMap<String, String> map);
    }
}
