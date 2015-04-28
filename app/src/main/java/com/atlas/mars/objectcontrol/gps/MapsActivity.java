package com.atlas.mars.objectcontrol.gps;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.http.MyHttp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {
       public final static String TAG = "myLog";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LocationManager locationManagerGps, locationManagerNet;
    LocationListener locationListenerGps, locationListenerNet;
    ImageButton btnFollow;
    public  LatLng myPos;
    static Marker myPosMarker;
    public static Circle circle;
   //public static Marker myPosMarker;
    private static final LatLng kiev = new LatLng(50.39, 30.47);
    public boolean folowMyPos = false;
    MyHttp myHttp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
       /* myHttp = new MyHttp(this);
        myHttp.postData("http://gps-tracker.com.ua/login.php");*/

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "+++ MapsInitializer" + e.toString());
        }

        btnFollow = (ImageButton)findViewById(R.id.btnFollow);
        setClickListenerImgTargetMyPos(btnFollow);
        setUpMapIfNeeded();
        locationManagerGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerNet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    protected void onPause() {
        if(locationManagerGps != null){
            locationManagerGps.removeUpdates(locationListenerGps);
        }
        if(locationManagerNet != null){
            locationManagerNet.removeUpdates(locationListenerNet);
        }
        MyLocationListenerGps.statusGps = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        locationListenerGps = new MyLocationListenerGps(this, mMap);
        locationListenerNet = new MyLocationListenerNet(this, mMap);
        locationManagerGps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListenerGps);
        locationManagerNet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNet);

    }


    protected void  setClickListenerImgTargetMyPos(ImageView img){
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myPos!=null){
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
                }else{
                    toastShow("Position not available");
                }
            }
        });
    }



    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
       // private static final LatLng MELBOURNE = new LatLng(-37.813, 144.962);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kiev, 10));

        /*mMap.addMarker(new MarkerOptions().position(kiev).title("Home").flat(true)
                .anchor(0.5f,0.5f)
                .alpha(0.7f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point)));*/




         /*mMap.addMarker(new MarkerOptions().position(kiev).title("Melbourne")

        LatLng MELBOURNE = new LatLng(-37.813, 144.962);
         Marker melbourne = mMap.addMarker(new MarkerOptions()

                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_minus))
                 .position(kiev));

//Todo поворот карты
/*
        CameraPosition oldPos = mMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(45.0f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
*/
    }

    public void toastShow(String str){
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }
    public void moveCameraToMyPos(){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
    }
    public void setMarkerMyPos(String title){
        if (myPosMarker != null) {
            myPosMarker.remove();
            myPosMarker = null;
        }
        myPosMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(myPos)
                        .anchor(0.5f,0.5f)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point)));
    }
    public  void setAccuracy(float accuracy){
        if (circle != null) {
            circle.remove();
            circle = null;
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(myPos)
                .radius(accuracy)
                .strokeColor(getResources().getColor(R.color.strokeColorAccuracy))
                .fillColor( getResources().getColor(R.color.fillColorAccuracy))
                .strokeWidth(2.0f);
        circle = mMap.addCircle(circleOptions);
    }
}
