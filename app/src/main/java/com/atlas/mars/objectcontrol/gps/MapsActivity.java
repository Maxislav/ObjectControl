package com.atlas.mars.objectcontrol.gps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.http.MyHttp;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.maps.android.ui.IconGenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends ActionBarActivity {

    DisplayMetrics displayMetrics;
    private DataBaseHelper dataBaseHelper;
    private float dpHeight, dpWidth, density;

    public final static String TAG = "myLog";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TileProvider tileProvider; //
    public LocationManager locationManagerGps, locationManagerNet;
    public LocationListener locationListenerGps, locationListenerNet;
    ImageButton btnFollow;
    ImageButton btnList;
    ImageButton btnBearing;
    ImageButton btnTrack;
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
    private HashMap<String, Marker> hashMarker;
    private HashMap<String, View> hashViewRow;
    private HashMap<String, Marker> hashPopup;
    private HashMap<String, HashMap> hashMapCollection;
    private HashMap<String, String> mapSetting;
    /***
     * Тип карты
     */
    private HashMap<String, String> mapType;


    private boolean targetOn;


    /**
     * targetOn включено ли все время за мной следить
     */
    private boolean isTouch;
    /**
     * isTouch касание
     */
    private boolean bearing;
    /**
     * bearing поворачивать ли карту по своему направлению
     */
    public float myBearing;

    MyHttp myHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        hashObjects = new HashMap<>();
        targetOn = false;
        isTouch = false;
        bearing = false;
        dataBaseHelper = new DataBaseHelper(this);

        mapSetting = new HashMap<>();
        dataBaseHelper.getSetting(mapSetting);

        displayMetrics = getResources().getDisplayMetrics();
        density = displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / density;
        dpWidth = displayMetrics.widthPixels / density;
        Log.d(TAG, "Density: " + density + " Width dp: " + dpWidth + " Width Pixels: " + displayMetrics.widthPixels);
        hashMarker = new HashMap<>();
        hashViewRow = new HashMap<>();
        hashPopup = new HashMap<>();
        hashMapCollection = new HashMap<>();

        // Log.d(TAG, "haveNetworkConnection +++ "+ haveNetworkConnection());
        if (haveNetworkConnection()) {
            myHttp = new MyHttp(this);
            myHttp.postData();
        }

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "Error +++ MapsInitializer" + e.toString());
        }

        btnFollow = (ImageButton) findViewById(R.id.btnFollow);
        btnList = (ImageButton) findViewById(R.id.btnList);
        btnBearing = (ImageButton) findViewById(R.id.btnBearing);
        btnTrack = (ImageButton) findViewById(R.id.btnTrack);
        listContainer = (LinearLayout) findViewById(R.id.listContainer);
        linearLayoutInScroll = (LinearLayout) findViewById(R.id.linearLayoutInScroll);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        setClickListenerImgTargetMyPos(btnFollow);
        setClickListenerImgBearing(btnBearing);
        setClickListenerImgTrack(btnTrack);

        setClickListenerBtnList();
        setUpMapIfNeeded();
        locationManagerGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerNet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                //  toastShow("ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                // toastShow("ACTION_UP");
                break;
        }
        return super.dispatchTouchEvent(ev);
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
        if (myHttp != null) myHttp.onPause();
        LatLng startLatLng = mMap.getCameraPosition().target;
        double lat  = startLatLng.latitude;
        double lng  = startLatLng.longitude;
        float zoom = mMap.getCameraPosition().zoom;

        mapSetting.put(dataBaseHelper.MAP_START_LAT, Double.toString(lat));
        mapSetting.put(dataBaseHelper.MAP_START_LNG, Double.toString(lng));
        mapSetting.put(dataBaseHelper.MAP_START_ZOOM, Float.toString(zoom));
        dataBaseHelper.setSetting(mapSetting);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        locationListenerGps = new MyLocationListenerGps(this, mMap);
        locationListenerNet = new MyLocationListenerNet(this, mMap);
        locationManagerGps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGps);
        locationManagerNet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNet);
        if (haveNetworkConnection()) {
            myHttp.onResume();
        }



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
    public void drawPoly(String path){
        String state = Environment.getExternalStorageState();

        if (!(state.equals(Environment.MEDIA_MOUNTED))) {
            //Toast.makeText(this, "There is no any sd card", Toast.LENGTH_LONG).show();

            toastShow("There is no any sd card");
            return;
        }else{

        }


        Mytrack mytrack = new Mytrack(path);

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(mytrack.getTrack())
                .width(5)
                .color(Color.BLUE));
        line.setZIndex(2.0f);
        //return null;
    }

    protected void setClickListenerImgTrack(final ImageView img ) {
        img.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                OpenFileDialog fileDialog = new OpenFileDialog(MapsActivity.this);
                fileDialog.show();
                /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file*//*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a File to Upload"),1);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    toastShow("Please install a File Manager.");
                    //Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });
    }

    protected void setClickListenerImgBearing(final ImageView img) {

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bearing) {
                    bearing = true;
                    img.setBackgroundResource(R.drawable.bitmap_rotate_follow_on);
                    if (bearing && myBearing != 0.0f) {
                        rotateCamera(myBearing);
                    }

                } else {
                    img.setBackgroundResource(R.drawable.bitmap_nord);
                    bearing = false;
                    rotateCamera(0.0f);
                }
            }
        });
    }

    protected void setClickListenerImgTargetMyPos(final ImageView img) {
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myPos != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(myPos));
                    targetOn = true;
                    img.setBackgroundResource(R.drawable.target_on);
                    //img.setBackgroundDrawable( getResources().getDrawable(R.drawable.target) );
                    toastShow("Autocenter on");
                } else {
                    toastShow("Position not available");
                }
            }
        });
    }


    protected void setClickListenerBtnList() {
        final Animation animIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_left);
        final Animation aniOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_left);
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int)(density*250), FrameLayout.LayoutParams.MATCH_PARENT);

                if (listContainer.isShown()) {
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
                } else {

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


    private void setTileLayer(String mapName){
        setUpTileLayer(mapName);
        TileOverlay tileOverlay = mMap.addTileOverlay( new TileOverlayOptions().tileProvider(tileProvider).zIndex(1.0f));

    }

    private void setUpTileLayer(final String mapName){
        tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String s;
                switch (mapName){
                    case "mapQuest":
                        s = String.format(mapType.get(mapName), zoom, x, y);
                        break;
                    case "ggl":
                        s = String.format(mapType.get(mapName), x, y, zoom);
                        break;
                    default:
                        s = String.format(mapType.get(mapName), x, y, zoom);
                }

               // String
                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }

                try {
                    return new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                }
            }
            private boolean checkTileExists(int x, int y, int zoom) {
                int minZoom = 6;
                int maxZoom = 19;

                if ((zoom < minZoom || zoom > maxZoom)) {
                    return false;
                }

                return true;
            }
        };
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
        mapType = new HashMap<>();
        mapType.put("ggl", "http://mt0.googleapis.com/vt/lyrs=m@207000000&hl=ru&src=api&x=%d&y=%d&z=%d&s=Galile");
        mapType.put("mapQuest", "http://otile3.mqcdn.com/tiles/1.0.0/map/%d/%d/%d.png");


        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            //mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            //  MapView mapView = (MapView)(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            SupportMapFragment mainFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            View mapView = (View) mainFragment.getView();
            //todo раскоментировать потом
            //setTileLayer("mapQuest");

            /*Mytrack mytrack = new Mytrack();

            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(mytrack.getTrack())
                    .width(5)
                    .color(Color.BLUE));
            line.setZIndex(2.0f);*/




            //   TouchableWrapper mTouchView = new TouchableWrapper(mainFragment.getActivity());


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
        String lat = mapSetting.get(dataBaseHelper.MAP_START_LAT);
        String lng = mapSetting.get(dataBaseHelper.MAP_START_LNG);
        LatLng startPos;
        if(lat!=null && lng!=null){
            startPos = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        }else {
            startPos = kiev;
        }
        String strZoom = mapSetting.get(dataBaseHelper.MAP_START_ZOOM);
        float zoom = 10;
        if(strZoom!=null){
            zoom = Float.parseFloat(strZoom);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPos, zoom));
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnCameraChangeListener(mOnCameraChangeListener);

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


    private final GoogleMap.OnCameraChangeListener mOnCameraChangeListener =
            new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    if (isTouch) {
                        //отключаем слежку за собой
                        if (targetOn) {
                            btnFollow.setBackgroundResource(R.drawable.target);
                            toastShow("Autocenter off");
                        }
                        targetOn = false;

                    }
                }
            };

    public void toastShow(String str) {
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }

    public void moveCameraToMyPos() {
        if (targetOn) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(myPos), 500, MyCancelableCallback);
        }
    }

    GoogleMap.CancelableCallback MyCancelableCallback = new GoogleMap.CancelableCallback() {
        @Override
        public void onFinish() {
            if (bearing && myBearing != 0.0f) {
                rotateCamera(myBearing);
            }
        }

        @Override
        public void onCancel() {

        }
    };

    private void rotateCamera(float _bearing) {
        CameraPosition oldPos = mMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(_bearing).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

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
                        .flat(true)
                        .title(title));
        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point)));
        if (MyLocationListenerGps.statusGps && myBearing != 0.0f) {
            myPosMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_obj));
            myPosMarker.setRotation(myBearing);
        } else {
            myPosMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point));
        }

    }

    public void setObjectMarkers(ArrayList<HashMap> arrayList) {
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        for (HashMap<String, String> hashMapMArkerOpt : arrayList) {
            String id = hashMapMArkerOpt.get("id");
            if (hashMapMArkerOpt.get("lat") != null && !hashMapMArkerOpt.get("lat").isEmpty()) {
                // LatLng pos = new LatLng(Float.parseFloat(hashMapMArkerOpt.get("lat")), Float.parseFloat(hashMapMArkerOpt.get("lng")));
                //hashObjects.put(hashMapMArkerOpt.get("id"), hashMapMArkerOpt);

                if (hashMapCollection.get(id) == null) {
                    hashMapCollection.put(id, hashMapMArkerOpt);
                    Log.d(TAG, "Init +++ " + id);
                    drawMarker(hashMapMArkerOpt);
                    drawIcon(hashMapMArkerOpt);
                    addRowObject(hashMapMArkerOpt);
                }

                if (!hashMapCollection.get(id).get("dateLong").equals(hashMapMArkerOpt.get("dateLong"))) {
                    Log.d(TAG, "Move +++ " + id);
                    redrawMarker(hashMapMArkerOpt);
                    redrawIcon(hashMapMArkerOpt);
                    addRowObject(hashMapMArkerOpt);
                }
            }
        }
    }

    private void drawMarker(HashMap<String, String> map) {
        hashObjects.put(map.get("id"), map);
        LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
        Marker objMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(pos)
                        .anchor(0.5f, 0.5f)
                        .title(map.get("name"))
                        .snippet(map.get("id"))
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(map.get("azimuth") != null ? R.drawable.arrow_obj : R.drawable.ico_point_obj)));

        if (map.get("id") != null) {
            hashMarker.put(map.get("id").toString(), objMarker);
        }
        if (map.get("azimuth") != null) {
            objMarker.setRotation(Float.parseFloat(map.get("azimuth")));
        }
    }

    private void redrawMarker(HashMap<String, String> map) {
        String id = map.get("id");
        hashMarker.get(id).remove();
        drawMarker(map);
    }


    private void drawIcon(HashMap<String, String> map) {
        IconGenerator iconFactory = new IconGenerator(this);
        String id = map.get("id");
        LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.
                        fromBitmap(iconFactory.makeIcon(map.get("name")))).
                position(pos).
                snippet(map.get("id")).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        Marker iconMarker = mMap.addMarker(markerOptions);
        hashPopup.put(id, iconMarker);
    }

    private void redrawIcon(HashMap<String, String> map) {
        String id = map.get("id");
        hashPopup.get(id).remove();
        drawIcon(map);
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
            if (map != null && !map.isEmpty()) {
                TextView textName = (TextView) v.findViewById(R.id.textName);
                TextView textDate = (TextView) v.findViewById(R.id.textDate);
                TextView textTime = (TextView) v.findViewById(R.id.textTime);
                TextView textSp = (TextView) v.findViewById(R.id.textSp);
                TextView textSat = (TextView) v.findViewById(R.id.textSat);
                TextView textBat = (TextView) v.findViewById(R.id.textBat);
                textName.setText(map.get("name"));
                textDate.setText(map.get("date"));
                textTime.setText(map.get("time"));
                textSp.setText(map.get("speed"));
                textSat.setText(map.get("gps_level"));
                textBat.setText(map.get("bat_level"));
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
                .strokeWidth(2.0f)
                .zIndex(2.0f);
        circle = mMap.addCircle(circleOptions);
    }

    private void addRowObject(final HashMap<String, String> map) {
        String id = map.get("id");

        LayoutInflater ltInflater = getLayoutInflater();
        View view = hashViewRow.get(id) != null ? hashViewRow.get(id) : ltInflater.inflate(R.layout.row_map_object, null, false);
        TextView textName = (TextView) view.findViewById(R.id.textName);
        TextView textDate = (TextView) view.findViewById(R.id.textDate);
        TextView textTime = (TextView) view.findViewById(R.id.textTime);
        TextView textSp = (TextView) view.findViewById(R.id.textSp);
        TextView textSat = (TextView) view.findViewById(R.id.textSat);
        TextView textBat = (TextView) view.findViewById(R.id.textBat);
        textName.setText(map.get("name"));
        textDate.setText(map.get("date"));
        textTime.setText(map.get("time"));
        textSp.setText(map.get("speed"));
        textSat.setText(map.get("gps_level"));
        textBat.setText(map.get("bat_level"));

        if (hashViewRow.get(map.get("id")) == null) {
            hashViewRow.put(id, view);
            linearLayoutInScroll.addView(view);
        }
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (map.get("lat") != null && !map.get("lat").isEmpty()) {
                    LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                }
            }
        });
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
