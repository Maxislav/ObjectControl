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
public class Track {
    public final static String TAG = "myLog";

    public Track(){

    };


    public LatLng[] parseTrack(String json){
        Log.d(TAG, json);
        LatLng[] latLngs = new LatLng[0] ;
        ObjectMapper mapper = new ObjectMapper();
        try {
            ObjectNode root = (ObjectNode) mapper.readTree(json);
            ObjectNode route =(ObjectNode)root.get("route");
            ObjectNode shape = (ObjectNode)route.get("shape");
            ArrayNode shapePoints  = (ArrayNode)shape.get("shapePoints");
            latLngs = new LatLng[shapePoints.size()/2];
            int k=0;
            for(int i=0; i<shapePoints.size(); i+=2){
                double lat = shapePoints.get(i).asDouble();
                double lng = shapePoints.get(i+1).asDouble();
                latLngs[k] =  new LatLng(lat, lng);
                k++;
        }

            Log.e(TAG, "end");



        } catch (IOException e) {
            Log.e(TAG, "Can't parse json");
            e.printStackTrace();
        }

        return latLngs;


    }




}
