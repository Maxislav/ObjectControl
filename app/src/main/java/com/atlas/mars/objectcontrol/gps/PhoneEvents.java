package com.atlas.mars.objectcontrol.gps;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.atlas.mars.objectcontrol.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by mars on 11/4/15.
 */
public class PhoneEvents   implements View.OnClickListener{
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
                mapsActivity.toastShow(getAddress(mapsActivity.myPos.latitude,mapsActivity.myPos.longitude));
                Log.d(TAG, "Click phone");
                break;

        }
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(mapsActivity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }
}
