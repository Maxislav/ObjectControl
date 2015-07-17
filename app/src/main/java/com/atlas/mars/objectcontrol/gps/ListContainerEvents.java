package com.atlas.mars.objectcontrol.gps;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by mars on 7/17/15.
 */
public class ListContainerEvents implements View.OnTouchListener {
    MapsActivity activity;
    View row;
    LinearLayout listContainerf;

    private int _xDelta;
    private int _yDelta;
    private final static String TAG = "moveLog";

    ListContainerEvents(View row, LinearLayout listContainerf, MapsActivity activity) {
        this.row = row;
        this.activity = activity;
        this.listContainerf = listContainerf;
        onInit();
    }

    private void onInit() {
        row.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //  FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) v.getLayoutParams();
                FrameLayout.LayoutParams ffrParams = (FrameLayout.LayoutParams) listContainerf.getLayoutParams();

                //_xDelta = X - lParams.leftMargin;
                _xDelta = X - ffrParams.leftMargin;
                Log.d(TAG, "+++ACTION_DOWN" + _xDelta);
                //_yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                //FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) v.getLayoutParams();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) v.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;

                FrameLayout.LayoutParams frParams = (FrameLayout.LayoutParams) listContainerf.getLayoutParams();
                if (X - _xDelta < 0) {
                    frParams.leftMargin = X - _xDelta;
                    listContainerf.setLayoutParams(frParams);
                    if (X - _xDelta < -100) {
                        activity.hideListObject();
                    }
                }

                // layoutParams.topMargin = Y - _yDelta;
                //layoutParams.rightMargin = -250;
                // layoutParams.bottomMargin = -250;
                Log.d(TAG, "+++ACTION_MOVE  " + " : " + _xDelta + " " + layoutParams.leftMargin);

                break;
        }


        return true;
    }
}
