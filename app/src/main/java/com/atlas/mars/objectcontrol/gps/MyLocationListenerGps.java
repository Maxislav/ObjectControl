package com.atlas.mars.objectcontrol.gps;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Iterator;

/**
 * Created by mars on 4/24/15.
 */
public class MyLocationListenerGps implements LocationListener, GpsStatus.Listener {
    public final static String TAG = "myLog";
    public GoogleMap mMap;
    MapsActivity mapsActivity;
    public static LatLng myPosGps;
    static Marker myPosMarker;
    public static Circle circle;
    /** statusGps  - зафиксировано ли положение по gps */
    public static boolean statusGps = false;
    public static float accuracy;

    public MyLocationListenerGps(MapsActivity mapsActivity, GoogleMap mMap) {
        this.mapsActivity = mapsActivity;
        this.mMap = mMap;
        mapsActivity.locationManagerGps.addGpsStatusListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        float bearing = location.getBearing();
        mapsActivity.myBearing = bearing;
        myPosGps = new LatLng(lat, lng);
        accuracy = location.getAccuracy();
        statusGps = true;
            mapsActivity.myPos = myPosGps;
            mapsActivity.setMarkerMyPos("My location GPS");
            mapsActivity.moveCameraToMyPos();
             mapsActivity.setAccuracy(accuracy);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        switch (status){
            case GpsStatus.GPS_EVENT_STARTED:
                statusGps = false;
                // Do Something with mStatus info
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                // Do Something with mStatus info
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                // Do Something with mStatus info
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                // Do Something with mStatus info
                break;
        }
        Log.d(TAG,"onStatusChanged +++ "+ provider + " : " + status);

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG,"onProviderEnabled +++ "+ provider );
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG,"onProviderDisabled +++ "+ provider );
    }

    @Override
    public void onGpsStatusChanged(int event) {
        GpsStatus gpsStatus = mapsActivity.locationManagerGps.getGpsStatus(null);
       String strGpsStats = "";
        if(gpsStatus != null) {
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();
            int i=0;
            while (sat.hasNext()) {
                Log.d(TAG, "strGpsStats +++");
                GpsSatellite satellite = sat.next();
                strGpsStats+= (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation()+ "\n\n";
                Log.d(TAG, strGpsStats);
            }
            //tv.setText(strGpsStats);
        }
    }
}
