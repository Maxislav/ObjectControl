package com.atlas.mars.objectcontrol.gps;

import android.graphics.Color;
import android.util.Log;

import com.atlas.mars.objectcontrol.http.MapQuest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;

/**
 * Created by mars on 6/26/15.
 */
public class Track implements GoogleMap.OnMapLongClickListener{
    public final static String TAG = "myLog";
    GoogleMap mMap;
    MapsActivity mapsActivity;
    MapQuest mapQuest;
    public Track(MapsActivity mapsActivity, GoogleMap mMap){
        this.mapsActivity = mapsActivity;
        this.mMap = mMap;
        mMap.setOnMapLongClickListener(this);
        mapQuest = new MapQuest(mapsActivity, this);
        //todo stop hear
       // mapQuest.findRoute("50.3891,30.49373", "50.446,30.44852");
    };


    public void parseTrack(String json){
        Log.d(TAG, json);
        LatLng[] latLngs = new LatLng[0] ;
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode root = (ObjectNode) mapper.readTree(json);
            ObjectNode route =(ObjectNode)root.get("route");

            ObjectNode legs = (ObjectNode)route.get("legs").get(0);

            ArrayNode maneuvers = (ArrayNode)legs.get("maneuvers");
            latLngs = new LatLng[maneuvers.size()];
            int i = 0;

            for (JsonNode jsonNode : maneuvers) {

                ObjectNode startPoint = (ObjectNode)jsonNode.get("startPoint");
                double lat = startPoint.path("lat").asDouble();
                double lng = startPoint.path("lng").asDouble();
                latLngs[i] =  new LatLng(lat, lng);
                i++;

            }
            Log.d(TAG, json);

        } catch (IOException e) {
            Log.e(TAG, "Can't parse json");
            e.printStackTrace();
        }
        if(latLngs!=null && 1<latLngs.length){
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(latLngs)
                    .width(5)
                    .color(Color.BLUE));
            line.setZIndex(2.0f);
        }

    }



    @Override
    public void onMapLongClick(LatLng latLng) {
        toastShow("" + latLng.latitude + ": " + latLng.longitude);
    }

    private void toastShow(String str){
        mapsActivity.toastShow(str);
    }


}
