package com.atlas.mars.objectcontrol.gps;

import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.MainActivity;
import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.http.M2Http;
import com.atlas.mars.objectcontrol.http.NaviZone;
import com.atlas.mars.objectcontrol.rest.HillRest;
import com.atlas.mars.objectcontrol.rest.MapQuestRest;
import com.atlas.mars.objectcontrol.rest.OsmRest;
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
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.maps.android.ui.IconGenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends ActionBarActivity implements FragmentZoomControl.OnClickListener {

    DisplayMetrics displayMetrics;
    public DataBaseHelper dataBaseHelper;
    private float dpHeight, dpWidth, density;
    private TileOverlay tileOverlay, tileHill;

    public final static String TAG = "MapsActivityLog";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TileProvider tileProvider; //
    public LocationManager locationManagerGps, locationManagerNet;
    public LocationListener locationListenerGps, locationListenerNet;
    ImageButton btnFollow;
    ImageButton btnList;
    ImageButton btnBearing;
    ImageButton btnTrack;
    ImageButton btnPhone;


    LinearLayout listContainer;
    LinearLayout linearLayoutInScroll;
    ScrollView scrollView;

    int fromActivityFlag;

    public LatLng myPos;
    public float mySpeed;
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
    public HashMap<String, String> mapSetting;
    TrackButton trackButton;
    FragmentZoomControl fragmentZoomControl;
    public int countObj = 0;
    /**
     * Тип карты
     */
    private String mapType;
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

    M2Http m2Http;
    NaviZone naviZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();

        fromActivityFlag = intent.getFlags();

        hashObjects = new HashMap<>();
        targetOn = false;
        isTouch = false;
        bearing = false;
        dataBaseHelper = new DataBaseHelper(this);

        mapSetting = dataBaseHelper.hashSetting;

        displayMetrics = getResources().getDisplayMetrics();
        density = displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / density;
        dpWidth = displayMetrics.widthPixels / density;
        Log.d(TAG, "Density: " + density + " Width dp: " + dpWidth + " Width Pixels: " + displayMetrics.widthPixels);
        hashMarker = new HashMap<>();
        hashViewRow = new HashMap<>();
        hashPopup = new HashMap<>();
        hashMapCollection = new HashMap<>();

        fragmentZoomControl = new FragmentZoomControl();

        // Log.d(TAG, "haveNetworkConnection +++ "+ haveNetworkConnection());
        if (haveNetworkConnection()) {
            m2Http = new M2Http(this);
        }
        //todo  авторизация на навазоне
        //new NaviZone().init();
        //todo  авторизация на Oko
        //new OkoServ().init();

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
        btnPhone = (ImageButton) findViewById(R.id.btnPhone);



        setOnClickListinerPhone();

        listContainer = (LinearLayout) findViewById(R.id.listContainer);


        linearLayoutInScroll = (LinearLayout) findViewById(R.id.linearLayoutInScroll);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        setClickListenerBtnList();
        new ChangeMap(this);
        setUpMapIfNeeded();
        setClickListenerImgTargetMyPos(btnFollow);
        setClickListenerImgBearing(btnBearing);
        trackButton = new TrackButton(this, btnTrack, mMap);

        new PhoneEvents(btnPhone, this);



        locationManagerGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManagerNet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //todo навешивание на сткролл
        new ScrollOnTouchListener(null, listContainer, this, null);

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
        if(myPosMarker!=null){
            myPosMarker.remove();
            myPosMarker = null;
        }
        if (locationManagerGps != null) {
            locationManagerGps.removeUpdates(locationListenerGps);
        }
        if (isNetworkAvailable()) {
            if (locationManagerNet != null) {
                locationManagerNet.removeUpdates(locationListenerNet);
            }
        }

        MyLocationListenerGps.statusGps = false;
        if (m2Http != null) m2Http.onPause();
        if (naviZone != null) naviZone.onPause();

        if (mMap == null) {
            super.onPause();
            return;
        }

        LatLng startLatLng = mMap.getCameraPosition().target;
        double lat = startLatLng.latitude;
        double lng = startLatLng.longitude;
        float zoom = mMap.getCameraPosition().zoom;

        if (mapSetting != null) {
            mapSetting.put(dataBaseHelper.MAP_START_LAT, Double.toString(lat));
            mapSetting.put(dataBaseHelper.MAP_START_LNG, Double.toString(lng));
            mapSetting.put(dataBaseHelper.MAP_START_ZOOM, Float.toString(zoom));
        }


        if (trackButton != null) {
            trackButton.onPause();
        }
        dataBaseHelper.setSetting(mapSetting);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();


        locationListenerGps = new MyLocationListenerGps(this, mMap);
        locationManagerGps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGps);


        if (!haveNetworkConnection() || !isNetworkAvailable()) {
            toastShow("No connections");
            return;
        }


        locationListenerNet = new MyLocationListenerNet(this, mMap);
        locationManagerNet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNet);
        if (haveNetworkConnection()) {
            if (mapSetting.get(SettingMapActivity.PROTOCOL_TYPE) == null || mapSetting.get(SettingMapActivity.PROTOCOL_TYPE).equals("0")) {
                if (m2Http == null) {
                    m2Http = new M2Http(this);
                }
                m2Http.onResume();
            } else if (mapSetting.get(SettingMapActivity.PROTOCOL_TYPE).equals("1")) {
                if (naviZone == null) {
                    naviZone = new NaviZone(this);
                }
                naviZone.onResume();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, fromActivityFlag+"");
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
            case R.id.action_go_command:
                questionIntent = new Intent(MapsActivity.this, MainActivity.class);

                if(fromActivityFlag == 1){
                    startActivity(questionIntent);
                }else {
                    startActivityForResult(questionIntent, 1);
                }
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
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                trackButton.onSelectIdTrack(data.getStringExtra("selectId"));
            }
        }
    }

    public void drawPoly(String path) {
        String state = Environment.getExternalStorageState();
        if (!(state.equals(Environment.MEDIA_MOUNTED))) {
            toastShow("There is no any sd card");
            return;
        }
        Mytrack mytrack = new Mytrack(path);

        if (trackButton != null) {
            LatLng[] latLngs = mytrack.getTrack();
            if (latLngs.length < 2) {
                return;
            }
            trackButton.drawPoly(latLngs);

            trackButton.addMarker(latLngs[0]);
            trackButton.addMarker(latLngs[latLngs.length - 1]);
        }

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
                if (mMap == null) {
                    setUpMapIfNeeded();
                }
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
        if (mapSetting.get(dataBaseHelper.MAP_SHOW_LIST) != null && mapSetting.get(dataBaseHelper.MAP_SHOW_LIST).equals("1")) {
            listContainer.setVisibility(View.VISIBLE);
        }
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listContainer.isShown()) {
                    hideListObject();
                } else {
                    if (countObj < 1) {
                        toastShow("Empty list objects");
                        return;
                    }
                    showListObgects();
                }
            }
        });
    }

    public void showListObgects() {

        final Animation animIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_left);
        FrameLayout.LayoutParams frParams = (FrameLayout.LayoutParams) listContainer.getLayoutParams();
        //frParams.leftMargin = 0;
        listContainer.setLayoutParams(frParams);
        final float leftMargin = (float) frParams.leftMargin;
        //  float fromXDelta = leftMargin + listContainer.getWidth()/2;
        float fromXDelta = leftMargin;
        float toXDelta = listContainer.getWidth() / 2;
        float fromYDelta = listContainer.getHeight() / 2;
        float toYDelta = listContainer.getHeight() / 2;


        TranslateAnimation translateAnimation = new TranslateAnimation(0, -fromXDelta, 0, 0);
        translateAnimation.setDuration(222);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                listContainer.setVisibility(View.VISIBLE);
                mapSetting.put(dataBaseHelper.MAP_SHOW_LIST, "1");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listContainer.clearAnimation();
                FrameLayout.LayoutParams frParams = (FrameLayout.LayoutParams) listContainer.getLayoutParams();
                frParams.leftMargin = 0;
                listContainer.setLayoutParams(frParams);
                //btnList.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // listContainer.startAnimation(animIn);
        listContainer.startAnimation(translateAnimation);
    }

    public void hideListObject() {
        final Animation aniOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_left);
        if (trackButton != null) trackButton.toObject = false;
        FrameLayout.LayoutParams frParams = (FrameLayout.LayoutParams) listContainer.getLayoutParams();
        float fromXDelta = frParams.leftMargin;
        float toXDelta = -listContainer.getWidth();
        TranslateAnimation translateAnimation = new TranslateAnimation(0, fromXDelta + toXDelta, 0, 0);
        translateAnimation.setDuration(222);
        final int width = listContainer.getWidth();

        //listContainer.setLayoutParams(frParams);
        //  TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta, 0, 0, 0);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listContainer.setVisibility(View.INVISIBLE);
                mapSetting.put(dataBaseHelper.MAP_SHOW_LIST, "0");
                FrameLayout.LayoutParams frParams = (FrameLayout.LayoutParams) listContainer.getLayoutParams();
                frParams.leftMargin = -width;
                listContainer.setLayoutParams(frParams);
              /*  FrameLayout.LayoutParams frParams = (FrameLayout.LayoutParams) listContainer.getLayoutParams();
                frParams.leftMargin = -listContainer.getWidth();

                listContainer.setLayoutParams(frParams);*/
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        listContainer.startAnimation(translateAnimation);
    }


    public void setTileLayer(String mapName) {
        if (mapType == null || mapType != mapName) {
            mapType = mapName;

            if(tileHill!=null){
                tileHill.remove();
                tileHill.clearTileCache();
            }

            tileHill  = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(
                    getUrlTileProvider("hill")
            ).zIndex(2.0f));

            //todo кастомный свой запрос
           /* tileHill = mMap.addTileOverlay(new TileOverlayOptions()
                    .tileProvider(
                            new HillTileProvider()
                    ).zIndex(2.0f));*/

            switch (mapName) {
                case "ggl":
                    if (tileOverlay != null) {
                        tileOverlay.remove();
                    }
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case "osm":
                    if (tileOverlay != null) {
                        tileOverlay.remove();
                        tileOverlay.clearTileCache();
                    }
                   // setUpTileLayer(mapName);
                   // CustomMapTileProvider customMapTileProvider = new CustomMapTileProvider();

                    //todo рабочий вариант
                   // tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(1.0f));

                    //todo попытка своего
                    tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                            .tileProvider(
                                    new OsmMapTileProvider()
                            ).zIndex(1.0f));



                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                    break;
                case "mapQuest":
                    if (tileOverlay != null) {
                        tileOverlay.remove();
                        tileOverlay.clearTileCache();
                    }
                    /*setUpTileLayer(mapName);
                    tileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(1.0f));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);*/
                    tileOverlay = mMap.addTileOverlay(new TileOverlayOptions()
                            .tileProvider(
                                    new MapQuestTileProvider()
                            ).zIndex(1.0f));
                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                    break;
            }

        }
    }









    /*private void setUpTileLayer(final String mapName) {
        tileProvider = new UrlTileProvider(512, 512) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String s;

                switch (mapName) {
                    case "mapQuest":
                        s = String.format("http://otile3.mqcdn.com/tiles/1.0.0/map/%d/%d/%d.png", zoom, x, y);
                        break;
                    case "osm":
                        s = String.format("http://a.tile.openstreetmap.org/%d/%d/%d.png", zoom, x, y);
                        break;
                    case "ggl":
                        s = String.format("http://mt0.googleapis.com/vt/lyrs=m@207000000&hl=ru&src=api&x=%d&y=%d&z=%d&s=Galile", x, y, zoom);
                        break;
                    default:
                        s = String.format("http://otile3.mqcdn.com/tiles/1.0.0/map/%d/%d/%d.png", zoom, x, y);
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
*/

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
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            SupportMapFragment mainFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            View mapView = (View) mainFragment.getView();
            if (mapSetting != null && mapSetting.get(DataBaseHelper.MAP_TYPE) != null) {
                setTileLayer(mapSetting.get(DataBaseHelper.MAP_TYPE));
            }
            // mMap.getUiSettings().setCompassEnabled(false);

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
        if (lat != null && lng != null) {
            startPos = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        } else {
            startPos = kiev;
        }
        String strZoom = mapSetting.get(dataBaseHelper.MAP_START_ZOOM);
        float zoom = 10;
        if (strZoom != null) {
            zoom = Float.parseFloat(strZoom);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPos, zoom));
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        mMap.setOnCameraChangeListener(mOnCameraChangeListener);
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

    public void moveCameraToMarkerPos(LatLng pos) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(pos), 500, MyCancelableCallback);
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
        if (mMap == null) return;

        MarkerOptions markerOptions = new MarkerOptions()
                .position(myPos)
                .anchor(0.5f, 0.5f)
                .flat(true)
                .title(title);



        if (myPosMarker == null) {
            myPosMarker = mMap.addMarker(markerOptions);
            //myPosMarker.remove();
           // myPosMarker = null;
        }
        myPosMarker.setVisible(true);


        if (MyLocationListenerGps.statusGps && myBearing != 0.0f) {
            myPosMarker.setRotation(myBearing);
            myPosMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_obj));

        } else {
            myPosMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ico_point));
        }
        myPosMarker.setPosition(myPos);
        myPosMarker.setAnchor(0.5f, 0.5f);

    }

    public void setObjectMarkers(ArrayList<HashMap> arrayList) {
        if (mMap == null) return;
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
                    countObj++;
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

    @Override
    public void onItemSelected(View v) {
        CameraPosition oldPos = mMap.getCameraPosition();
        CameraPosition pos;
        Log.d(TAG, "+++ zoom in out");
        float zoom = oldPos.zoom;
        switch (v.getId()) {
            case R.id.zoomIn:
                zoom++;
                pos = CameraPosition.builder(oldPos).zoom(zoom).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 300, MyCancelableCallback);
                break;
            case R.id.zoomOut:
                zoom--;
                pos = CameraPosition.builder(oldPos).zoom(zoom).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 300, MyCancelableCallback);
                break;
        }

    }


    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        public MarkerInfoWindowAdapter() {
            super();
        }

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
        if (mMap == null) return;
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

        if (hashViewRow.get(id) == null) {
            new ListContainerEvents(view, listContainer, this, map);

            view.findViewById(R.id.textName).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
                    moveCameraToMarkerPos(pos);
                    if (trackButton != null && trackButton!=null) {
                        trackButton.onListObjectClick(pos);
                    }

                    /*LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                    if (trackButton != null) {
                        trackButton.onListObjectClick(pos);
                    }*/
                }
            });

        }

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
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.get("lat") != null && !map.get("lat").isEmpty()) {
                    LatLng pos = new LatLng(Float.parseFloat(map.get("lat")), Float.parseFloat(map.get("lng")));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                    if (trackButton != null) {
                        trackButton.onListObjectClick(pos);
                    }
                }
            }
        });*/
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
            return false;
        }

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Нажатие на телефон
     */
    public void setOnClickListinerPhone(){
      //  img.setOnClickListener(new View.OnClickListener() {

    }



    public class OsmMapTileProvider implements TileProvider {
        private static final int TILE_WIDTH = 512;
        private static final int TILE_HEIGHT = 512;
        private static final int BUFFER_SIZE = 16 * 1024;

        @Override
        public Tile getTile(int x, int y, int zoom) {
            byte[] image = readTileImage(x, y, zoom);
            return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
        }

        private byte[] readTileImage(int x, int y, int zoom) {
            OsmRest osmRest = new OsmRest(zoom, x, y);
            byte[] byteArray = osmRest.getResultByteArray();
            return byteArray;
        }
    }

    public class MapQuestTileProvider implements TileProvider {
        private static final int TILE_WIDTH = 512;
        private static final int TILE_HEIGHT = 512;
        private static final int BUFFER_SIZE = 16 * 1024;

        @Override
        public Tile getTile(int x, int y, int zoom) {
            byte[] image = readTileImage(x, y, zoom);
            return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
        }

        private byte[] readTileImage(int x, int y, int zoom) {
            MapQuestRest mapQuestRest = new MapQuestRest(zoom, x, y);
            byte[] byteArray = mapQuestRest.getResultByteArray();
            return byteArray;
        }
    }

    public class HillTileProvider implements TileProvider {
        private static final int TILE_WIDTH = 512;
        private static final int TILE_HEIGHT = 512;
        private static final int BUFFER_SIZE = 16 * 1024;

        @Override
        public Tile getTile(int x, int y, int zoom) {
            byte[] image = readTileImage(x, y, zoom);
            return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
        }

        private byte[] readTileImage(int x, int y, int zoom) {
            HillRest hillRest = new HillRest(zoom, x, y);
            byte[] byteArray = hillRest.getResultByteArray();
            return byteArray;
        }
    }

    private UrlTileProvider getUrlTileProvider(final String mapName){
        return new UrlTileProvider(256,256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                URL url = null;


                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }
                switch (mapName){
                    case "hill":
                        try {
                            url = new URL(String.format("http://hills.gpsies.com/%d/%d/%d.png", zoom, x, y));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        return url;
                }
                return null;
            }

            private boolean checkTileExists(int x, int y, int zoom) {
                int minZoom = 10;
                int maxZoom = 20;

                if ((zoom < minZoom || zoom > maxZoom)) {
                    return false;
                }
                return true;
            }
        };
    }

}
