package com.atlas.mars.objectcontrol.geoMath;

/**
 * Created by mars on 5/18/15.
 */
public class GeoMath {
    public float toAthimuth(float llat1, float llng1, float llat2, float llng2) {
        double rad = 6372795;

        double lat1 = llat1 * Math.PI / 180;
        double lat2 = llat2 * Math.PI / 180;
        double lng1 = llng1 * Math.PI / 180;
        double lng2 = llng2 * Math.PI / 180;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);

        double delta = lng2 - lng1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;
        double ad = Math.atan2(y,x);
        double  dist = ad*rad;


        return 0.0f;

    }
}
