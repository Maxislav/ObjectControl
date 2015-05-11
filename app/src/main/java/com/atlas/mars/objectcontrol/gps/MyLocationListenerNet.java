package com.atlas.mars.objectcontrol.gps;

import android.location.Location;
import android.location.LocationListener;
import android.util.Log;

import com.atlas.mars.objectcontrol.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Администратор on 4/25/15.
 */
public class MyLocationListenerNet extends MyLocationListenerGps implements LocationListener {
    public static LatLng myPosNet;
    public static float accuracy;
    public MyLocationListenerNet(MapsActivity mapsActivity, GoogleMap mMap) {
       super(mapsActivity, mMap);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        myPosNet = new LatLng(lat, lng);
        accuracy = location.getAccuracy();

        if(!MyLocationListenerGps.statusGps){
            mapsActivity.myPos = myPosNet;
            mapsActivity.setMarkerMyPos("My location NET");
          //  mapsActivity.moveCameraToMyPos();
            mapsActivity.setAccuracy(accuracy);
        }
    }
}
