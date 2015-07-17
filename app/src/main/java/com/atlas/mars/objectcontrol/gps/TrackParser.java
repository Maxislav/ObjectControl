package com.atlas.mars.objectcontrol.gps;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by mars on 6/26/15.
 */
public class TrackParser {
    public final static String TAG = "myLog";
    public static final String DIST = "distance";
    public static final String LATLNGS = "latLngs";
    LatLng[] latLngs;
    Double distance;
    ObjectNode root;
    String json;

    public TrackParser(String json) {
        this.json = json;
    }

    private void onParse() {
        latLngs = new LatLng[0];
        ObjectMapper mapper = new ObjectMapper();
        try {
            root = (ObjectNode) mapper.readTree(json);
            ObjectNode route = (ObjectNode) root.get("route");

            ArrayNode legs = (ArrayNode) route.get("legs");
            double dist = 0;
            for (JsonNode leg : legs) {
                dist += leg.path("distance").asDouble();
            }
            distance = round(dist, 3);

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


    public LatLng[] getLatLngs() {
        Log.d(TAG, json);
        if (latLngs == null) {
            onParse();
        }
        return latLngs;
    }

    public Double getDistance() {
        if (latLngs == null) {
            onParse();
        }
        return distance;
    }


    /**
     * Расстояние трека по массиву координат
     *
     * @param arrTrackFull
     * @return
     */
    public static Double getDistance(LatLng[] arrTrackFull) {
        double dist_sum = 0.0;
        double R = 6372795;  //радиус Земли
        double lat1, lat2, long1, long2;
        double cl1, cl2, sl1, sl2;

        for (int i = 0; i < (arrTrackFull.length - 1); i++) {
            lat1 = arrTrackFull[i].latitude;
            long1 = arrTrackFull[i].longitude;
            lat2 = arrTrackFull[i + 1].latitude;
            long2 = arrTrackFull[i + 1].longitude;

            lat1 *= Math.PI / 180;
            lat2 *= Math.PI / 180;
            long1 *= Math.PI / 180;
            long2 *= Math.PI / 180;


            cl1 = Math.cos(lat1);
            cl2 = Math.cos(lat2);
            sl1 = Math.sin(lat1);
            sl2 = Math.sin(lat2);

            double delta = long2 - long1;
            double cdelta = Math.cos(delta);
            double sdelta = Math.sin(delta);


            double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
            double x = sl1 * sl2 + cl1 * cl2 * cdelta;
            double ad = Math.atan2(y, x);
            double dist = ad * R; //расстояние между двумя координатами в метрах
            dist_sum = dist_sum + dist;

        }

        dist_sum = dist_sum / 1000;
        dist_sum = round(dist_sum, 3);
        return dist_sum;
    }

    public static Double round(Double d, int precise) {
        return new BigDecimal(d).setScale(precise, RoundingMode.UP).doubleValue();
    }
}
