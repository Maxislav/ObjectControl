package com.atlas.mars.objectcontrol.geoMath;

/**
 * Created by mars on 5/18/15.
 */
public class GeoMath {
    public double toAthimuth(double llat1, double llng1, double llat2, double llng2) {
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

        //вычисления длины большого круга
        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;
        double ad = Math.atan2(y, x);
        double dist = ad * rad;


        //вычисление начального азимута
        x = (cl1 * sl2) - (sl1 * cl2 * cdelta);
        y = sdelta * cl2;
        double z = Math.toDegrees(Math.atan(-y / x));

        if (x < 0) {
            z = z + 180;
        }
        double z2 = (z + 180) % 360 - 180;
        z2 = -Math.toRadians(z2);

        double anglerad2 = z2 - ((2 * Math.PI) * Math.floor((z2 / (2 * Math.PI))));
        double angledeg = (anglerad2 * 180.) / Math.PI;
        return angledeg;
    }
}
