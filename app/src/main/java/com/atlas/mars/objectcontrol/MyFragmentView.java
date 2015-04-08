package com.atlas.mars.objectcontrol;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import java.util.HashMap;

/**
 * Created by mars on 4/8/15.
 */
public abstract class MyFragmentView {
    MainActivity mainActivity;
    View viewFragment;
    LayoutInflater inflater;
    RowCreator rowCreator;
    DataBaseHelper db;
    HashMap<String, FrameLayout> hashMapRow;
    View dialog;
    PopupWindow pw;
    static MyJQuery myJQuery;

    public MyFragmentView(MainActivity mainActivity, View viewFragment, LayoutInflater inflater){
        this.mainActivity = mainActivity;
        this.viewFragment = viewFragment;
        this.inflater = inflater;
        myJQuery = new MyJQuery();
        db = new DataBaseHelper(mainActivity);
        onInit();
    }

    abstract public void onInit();

}
