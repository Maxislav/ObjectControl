package com.atlas.mars.objectcontrol.gps;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.atlas.mars.objectcontrol.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by mars on 7/17/15.
 */
public class ListContainerEvents implements View.OnTouchListener {
    MapsActivity activity;
    View row;
    LinearLayout listContainerf;
    HashMap<String, String> map;

    private int _xDelta;
    private int _yDelta;
    private final static String TAG = "moveLog";

    ListContainerEvents(View row, LinearLayout listContainerf, MapsActivity activity, HashMap<String, String> map) {
        this.row = row;
        this.activity = activity;
        this.listContainerf = listContainerf;
        this.map = map;
        onInit();
    }

    public void onInit() {
        row.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        if(v.getParent() instanceof FrameLayout){

        }

        FrameLayout.LayoutParams frParams;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                frParams = (FrameLayout.LayoutParams) listContainerf.getLayoutParams();
                _xDelta = X - frParams.leftMargin;
                Log.d(TAG, "+++ACTION_DOWN  " + _xDelta);

                if(map!=null){
                    LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
                    activity.moveCameraToMarkerPos(pos);
                    if (activity.trackButton != null && activity.trackButton!=null) {
                        activity.trackButton.onListObjectClick(pos);
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "+++ACTION_UP  " + _xDelta);
                frParams = (FrameLayout.LayoutParams) listContainerf.getLayoutParams();
                if(frParams.leftMargin<-100){
                    activity.hideListObject();
                }else{
                    activity.showListObgects();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "+++ACTION_POINTER_DOWN  ");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "+++ACTION_POINTER_UP  ");
                break;
            case MotionEvent.ACTION_MOVE:
               // LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
                //layoutParams.leftMargin = X - _xDelta;
                frParams = (FrameLayout.LayoutParams) listContainerf.getLayoutParams();
                if (X - _xDelta < 0) {
                    frParams.leftMargin = X - _xDelta;
                    listContainerf.setLayoutParams(frParams);
                }

                break;
            case MotionEvent.ACTION_OUTSIDE:
                Log.d(TAG, "+++ACTION_MOVE");
                break;
            case MotionEvent.ACTION_CANCEL:
                frParams = (FrameLayout.LayoutParams) listContainerf.getLayoutParams();
                if(frParams.leftMargin<-100){
                    activity.hideListObject();
                }else{
                    activity.showListObgects();
                }
                break;
        }
        return true;
    }
}
