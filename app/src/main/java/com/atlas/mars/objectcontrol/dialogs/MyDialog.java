package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.atlas.mars.objectcontrol.DataBaseHelper;

/**
 * Created by mars on 4/8/15.
 */
abstract public class MyDialog {
    Activity activity;
    PopupWindow pw;
    LayoutInflater inflater;
    DataBaseHelper db;
    View viewDialog;
    MyDialog(Activity activity){
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new DataBaseHelper(activity);
    }

    abstract View onCreate();
    abstract void vHide(View view);
    abstract void onDismiss();
}