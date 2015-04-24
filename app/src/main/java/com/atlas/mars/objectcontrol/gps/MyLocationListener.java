package com.atlas.mars.objectcontrol.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.atlas.mars.objectcontrol.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by mars on 4/24/15.
 */
public class MyLocationListener implements LocationListener {
    private GoogleMap mMap;
    MapsActivity mapsActivity;
    LatLng myPos = mapsActivity.myPos;
    Marker myPosMarker;

    public MyLocationListener(MapsActivity mapsActivity, GoogleMap mMap) {
        this.mapsActivity = mapsActivity;
        this.mMap = mMap;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        myPos = new LatLng(lat, lng);

        if (myPosMarker != null) {
            myPosMarker.remove();
            myPosMarker = null;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 14));
        myPosMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(myPos)
                        .title("Melbourne")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point)));



    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
