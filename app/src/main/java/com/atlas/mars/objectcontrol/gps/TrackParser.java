package com.atlas.mars.objectcontrol.gps;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mars on 6/26/15.
 */
public class TrackParser {
    public final static String TAG = "myLog";
    LatLng[] latLngs;
    ObjectNode root;

    ParseLenght parseLenght;
    List<ParseLenght> parseLenghtList;
    PolyLenghth polyLenghth;
    public TrackParser(String json) {
        parseLenghtList = new ArrayList<>();
        Log.d(TAG, json);
        latLngs = new LatLng[0];
        ObjectMapper mapper = new ObjectMapper();
        try {
            root = (ObjectNode) mapper.readTree(json);
            ObjectNode route = (ObjectNode) root.get("route");
            ObjectNode shape = (ObjectNode) route.get("shape");
            ArrayNode shapePoints = (ArrayNode) shape.get("shapePoints");
            latLngs = new LatLng[shapePoints.size() / 2];
            int k = 0;
            for (int i = 0; i < shapePoints.size(); i += 2) {
                double lat = shapePoints.get(i).asDouble();
                double lng = shapePoints.get(i + 1).asDouble();
                latLngs[k] = new LatLng(lat, lng);
                k++;
            }

            Log.e(TAG, "end");
        } catch (IOException e) {
            Log.e(TAG, "Can't parse json");
            e.printStackTrace();
        }
    }

    public LatLng[] getLatLngs(){
        return latLngs;
    }

    public void getPolyLenght(PolyLenghth polyLenghth){
        this.polyLenghth = polyLenghth;
        parseLenght = new ParseLenght();
        parseLenghtList.add(parseLenght);
        parseLenght.execute();
    }

    private void callbackPolyLanght(double len){
        polyLenghth.setDistance(len);
    }

    public  void  onStop(){
        for(ParseLenght pl : parseLenghtList){
            if(!pl.isCancelled()){
                pl.onCancelled();
            }
        }
    }



    private class ParseLenght extends AsyncTask<String, Void, Double>{
        boolean onStop;
        ParseLenght(){
            super();
            this.onStop = false;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            this.onStop = true;
        }

        @Override
        protected Double doInBackground(String... params) {

            ObjectNode route = (ObjectNode) root.get("route");
            ArrayNode legs = (ArrayNode) route.get("legs");
            double dist = 0;
            for(JsonNode leg : legs){
                dist+=leg.path("distance").asDouble();

            }
            return dist;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Double d) {
            super.onPostExecute(d);
            if (!this.onStop)  callbackPolyLanght(d);
        }
    }

}
