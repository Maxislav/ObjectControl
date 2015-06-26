package com.atlas.mars.objectcontrol.gps;

import com.atlas.mars.objectcontrol.http.Gpsies;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mars on 6/26/15.
 */
public class Track implements GoogleMap.OnMapLongClickListener{
    GoogleMap mMap;
    MapsActivity mapsActivity;

    public Track(MapsActivity mapsActivity, GoogleMap mMap){
        this.mapsActivity = mapsActivity;
        this.mMap = mMap;
        mMap.setOnMapLongClickListener(this);
        new Gpsies(mapsActivity);
    }

    private void toastShow(String str){
        mapsActivity.toastShow(str);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        toastShow(""+ latLng.latitude +": "+ latLng.longitude);
    }
}
