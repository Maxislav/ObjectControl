package com.atlas.mars.objectcontrol.gps;

import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends ActionBarActivity {

    DisplayMetrics displayMetrics;
    private float dpHeight, dpWidth, density;

    public final static String TAG = "myLog";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LocationManager locationManagerGps, locationManagerNet;
    LocationListener locationListenerGps, locationListenerNet;
    ImageButton btnFollow;
    ImageButton btnList;
    LinearLayout listContainer;
    LinearLayout linearLayoutInScroll;
    ScrollView scrollView;


    public LatLng myPos;
    static Marker myPosMarker;
    public static Circle circle;
    private static final LatLng kiev = new LatLng(50.39, 30.47);
    public boolean folowMyPos = false;
    private HashMap<String, HashMap> hashObjects;
    private SupportMapFragment fragment;

    MyHttp myHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        hashObjects = new HashMap<>();

        displayMetrics = getResources().getDisplayMetrics();
        density = displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / density;
        dpWidth = displayMetrics.widthPixels / density;
        Log.d(TAG, "Density: " + density + " Width dp: " + dpWidth + " Width Pixels: " + displayMetrics.widthPixels);



        myHttp = new MyHttp(this);



        //Todo раскоментировать
        myHttp.postData();


        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "Error +++ MapsInitializer" + e.toString());
        }

        btnFollow = (ImageButton) findViewById(R.id.btnFollow);
        btnList = (ImageButton) findViewById(R.id.btnList);
        listContainer = (LinearLayout) findViewById(R.id.listContainer);
        linearLayoutInScroll = (LinearLayout) findViewById(R.id.linearLayoutInScroll);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

       // scrollView.setOnTouchListener(new MyTouchListener());
    //    scrollView.setOnDragListener(new MyDragListener());


        setClickListenerImgTargetMyPos(btnFollow);
        setClickListenerBtnList();
        setUpMapIfNeeded();
        locationManagerGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerNet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    protected void onPause() {
        if (locationManagerGps != null) {
            locationManagerGps.removeUpdates(locationListenerGps);
        }
        if (locationManagerNet != null) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.actionbar_background, null));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent questionIntent;
        switch (item.getItemId()) {
            case R.id.action_settings_map:
                questionIntent = new Intent(MapsActivity.this, SettingMapActivity.class);
                startActivityForResult(questionIntent, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //Todo нажато сохранение
            }
        }
    }

    protected void setClickListenerImgTargetMyPos(ImageView img) {
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myPos != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
                } else {
                    toastShow("Position not available");
                }
            }
        });
    }


    protected void setClickListenerBtnList(){
        final Animation animIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_left);
        final Animation aniOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_left);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)(density*250), FrameLayout.LayoutParams.MATCH_PARENT);

                if(listContainer.isShown()){
                    aniOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            listContainer.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    listContainer.startAnimation(aniOut);
                }else{

                    animIn.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            listContainer.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //btnList.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    listContainer.startAnimation(animIn);

                }

               // lp.setMargins(0, 0, 0, 0);
                //listContainer.setLayoutParams(lp);
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
          //  MapView mapView = (MapView)(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            SupportMapFragment  mainFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            //MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

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
    //    MapView mapView = (MapView)findViewById(R.id.map);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kiev, 10));

        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        mMap.getUiSettings().setZoomControlsEnabled(true);
       // MapView mapView = (MapView)findViewById(R.id.map);

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

    public void toastShow(String str) {
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }

    public void moveCameraToMyPos() {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
    }

    public void setMarkerMyPos(String title) {
        if (myPosMarker != null) {
            myPosMarker.remove();
            myPosMarker = null;
        }
        myPosMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(myPos)
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point)));
    }

    public void setObjectMarkers(ArrayList<HashMap> arrayList) {
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        for (HashMap<String, String> map : arrayList) {
            if (map.get("lat") != null && !map.get("lat").isEmpty()) {
                LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
                hashObjects.put(map.get("id"), map);
                Marker objMarker = mMap.addMarker(
                        new MarkerOptions()
                                .position(pos)
                                .anchor(0.5f, 0.5f)
                                .title(map.get("name"))
                                .snippet(map.get("id"))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point_obj)));
                objMarker.showInfoWindow();
                addRowObject(map);

                IconGenerator iconFactory = new IconGenerator(this);
              //  addIcon(iconFactory, "Default",pos);

               /* iconFactory.setColor(Color.CYAN);
                addIcon(iconFactory, "Custom color", new LatLng(-33.9360, 151.2070));

                iconFactory.setRotation(90);
                iconFactory.setStyle(IconGenerator.STYLE_RED);
                addIcon(iconFactory, "Rotated 90 degrees", new LatLng(-33.8858, 151.096));

                iconFactory.setContentRotation(-90);
                iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
                addIcon(iconFactory, "Rotate=90, ContentRotate=-90", new LatLng(-33.9992, 151.098));*/

                iconFactory.setRotation(0);
                //iconFactory.setContentRotation(90);
                iconFactory.setStyle(IconGenerator.STYLE_WHITE);
                addIcon(iconFactory, map.get("name"), pos, map);
                //Bitmap iconBitmap = bubbleIconFactory

               // MapView.LayoutParams mapParams =

                /*MapView.LayoutParams mapParams = new MapView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        pos,
               0.5,
                0.5,
                MapView.LayoutParams.WRAP_CONTENT);
                map.addView(popUp, mapParams);*/
            }
        }

    }
    private void addIcon(IconGenerator iconFactory, String text, LatLng position, HashMap<String, String> map) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
        snippet(map.get("id")).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        mMap.addMarker(markerOptions);
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {

            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {


            HashMap<String, String> map = hashObjects.get(marker.getSnippet());
            View v = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
            if(map!=null && !map.isEmpty()){
                TextView textName = (TextView) v.findViewById(R.id.textName);
                TextView textDate = (TextView) v.findViewById(R.id.textDate);
                TextView textTime = (TextView) v.findViewById(R.id.textTime);
                TextView textSp = (TextView) v.findViewById(R.id.textSp);
                textName.setText(map.get("name"));
                textDate.setText(map.get("date"));
                textTime.setText(map.get("time"));
                textSp.setText(map.get("speed"));
            }
//            marker.showInfoWindow();
            return v;
        }
    }

    public void setAccuracy(float accuracy) {
        if (circle != null) {
            circle.remove();
            circle = null;
        }

        CircleOptions circleOptions = new CircleOptions()
                .center(myPos)
                .radius(accuracy)
                .strokeColor(getResources().getColor(R.color.strokeColorAccuracy))
                .fillColor(getResources().getColor(R.color.fillColorAccuracy))
                .strokeWidth(2.0f);
        circle = mMap.addCircle(circleOptions);
    }

    private void addRowObject(final HashMap<String, String> map){
        LayoutInflater ltInflater = getLayoutInflater();
        View view = ltInflater.inflate(R.layout.row_map_object, null, false);

        TextView textName = (TextView) view.findViewById(R.id.textName);
        TextView textDate = (TextView) view.findViewById(R.id.textDate);
        TextView textTime = (TextView) view.findViewById(R.id.textTime);
        TextView textSp = (TextView) view.findViewById(R.id.textSp);
        textName.setText(map.get("name"));
        textDate.setText(map.get("date"));
        textTime.setText(map.get("time"));
        textSp.setText(map.get("speed"));

        linearLayoutInScroll.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            final HashMap<String,String> _map = map;
            @Override
            public void onClick(View v) {

                if(map.get("lat")!=null && !map.get("lat").isEmpty()){
                    LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                }
            }
        });
    }



}
