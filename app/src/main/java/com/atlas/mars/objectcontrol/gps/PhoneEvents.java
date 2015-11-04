package com.atlas.mars.objectcontrol.gps;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.atlas.mars.objectcontrol.R;

/**
 * Created by mars on 11/4/15.
 */
public class PhoneEvents implements View.OnClickListener{
    public final static String TAG = "PhoneEventsLog";
    MapsActivity mapsActivity;
    ImageButton btnPhone;

    public PhoneEvents(ImageButton btnPhone, MapsActivity mapsActivity) {
        this.btnPhone = btnPhone;
        this.mapsActivity = mapsActivity;
        btnPhone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPhone:
                Log.d(TAG, "Click phone");
                break;

        }
    }
}
