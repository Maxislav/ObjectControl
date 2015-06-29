package com.atlas.mars.objectcontrol.gps;

import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.http.MapQuest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Администратор on 6/27/15.
 */
public class TrackButton implements View.OnClickListener, GoogleMap.OnMapLongClickListener, PopupMenu.OnMenuItemClickListener {
    private final String TAG = "myLog";
    MapsActivity mapsActivity;
    ImageButton btnTrack;
    LinearLayout layoutRouteType;
    List<LinearLayout> listRouteType;
    private GoogleMap mMap;
    List<Marker> listMarkerPoints;
    List<Polyline> listPolylyneTrack;
    HashMap<String, String> mapSetting;
    String from;
    DataBaseHelper db;

    TrackButton(MapsActivity mapsActivity, ImageButton btnTrack, GoogleMap mMap) {
        this.mapsActivity = mapsActivity;
        this.btnTrack = btnTrack;
        this.mMap = mMap;
        listMarkerPoints = new ArrayList<>();
        listPolylyneTrack = new ArrayList<>();
        btnTrack.setOnClickListener(this);
        db = new DataBaseHelper(mapsActivity);
        mapSetting = DataBaseHelper.hashSetting;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTrack:
                showPopupMenu(v);
                break;
            case R.id.car:
                mapSetting.put(DataBaseHelper.MAP_ROUTE_TYPE, "car");
                setActiveRouteType((LinearLayout) v);
                break;
            case R.id.velo:
                mapSetting.put(DataBaseHelper.MAP_ROUTE_TYPE, "velo");
                setActiveRouteType((LinearLayout) v);
                break;
            case R.id.moto:
                mapSetting.put(DataBaseHelper.MAP_ROUTE_TYPE, "moto");
                setActiveRouteType((LinearLayout) v);
                break;
            case R.id.hand:
                mapSetting.put(DataBaseHelper.MAP_ROUTE_TYPE, "hand");
                setActiveRouteType((LinearLayout) v);
                break;
        }
    }


    private void setActiveRouteType(LinearLayout layout) {
        for (LinearLayout _layout : listRouteType) {
            if (_layout == layout) {
                _layout.setBackgroundResource(R.color.activeRouteType);
            } else {
                _layout.setBackground(null);
            }
        }
    }

    private void inflateLayoutRouteType() {
        LayoutInflater layoutInflater = mapsActivity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.route_type, null, false);
        FrameLayout frameLayout = (FrameLayout) mapsActivity.findViewById(R.id.globalLayout);
        layoutRouteType = (LinearLayout) view;
        frameLayout.addView(layoutRouteType);
        float width = mapsActivity.getResources().getDisplayMetrics().density * 200;
        float height = mapsActivity.getResources().getDisplayMetrics().density * 50;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) width, (int) height);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        layoutRouteType.setLayoutParams(layoutParams);
        listRouteType = new ArrayList<>();
        LinearLayout layoutCar = (LinearLayout) layoutRouteType.findViewById(R.id.car);
        LinearLayout layoutMoto = (LinearLayout) layoutRouteType.findViewById(R.id.moto);
        LinearLayout layoutVelo = (LinearLayout) layoutRouteType.findViewById(R.id.velo);
        LinearLayout layoutHand = (LinearLayout) layoutRouteType.findViewById(R.id.hand);
        listRouteType.add(layoutCar);
        listRouteType.add(layoutMoto);
        listRouteType.add(layoutVelo);
        listRouteType.add(layoutHand);
        for (LinearLayout layout : listRouteType) {
            layout.setOnClickListener(this);
        }
        String mapRouteType = mapSetting.get(DataBaseHelper.MAP_ROUTE_TYPE);
        if(mapRouteType==null){
            setActiveRouteType(layoutCar);
            mapSetting.put(DataBaseHelper.MAP_ROUTE_TYPE, "car");
            db.setSetting(mapSetting);
        }else{
            switch (mapRouteType){
                case "car":
                    setActiveRouteType(layoutCar);
                    break;
                case "moto":
                    setActiveRouteType(layoutMoto);
                    break;
                case "velo":
                    setActiveRouteType(layoutVelo);
                    break;
                case "hand":
                    setActiveRouteType(layoutHand);
                    break;
            }
        }
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(mapsActivity, v);
        popupMenu.inflate(R.menu.menu_traks_action);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        toastShow("" + latLng.latitude + ": " + latLng.longitude);
        //    mMap.setOnMapLongClickListener(null);

        Marker trackPointMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.track_control_point)));
        listMarkerPoints.add(trackPointMarker);

        GetFromServer getFromServer = new GetFromServer(mapsActivity);
        if (from == null) {
            from = String.valueOf(mapsActivity.myPos.latitude) + "," + String.valueOf(mapsActivity.myPos.longitude);
        }
        String to = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
        getFromServer.findRoute(from, to, mapSetting.get(DataBaseHelper.MAP_ROUTE_TYPE));
        from = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.loadFromCd):
                OpenFileDialog fileDialog = new OpenFileDialog(mapsActivity);
                mapsActivity.toastShow("JSON format only");
                fileDialog.show();
                return true;
            case (R.id.createRoute):
                if (layoutRouteType == null) {
                    inflateLayoutRouteType();
                }
                mMap.setOnMapLongClickListener(this);
                return true;
        }
        return false;
    }

    public void drawPoly(String result) {
        LatLng[] latLngs = new Track().parseTrack(result);
        if (latLngs != null && 1 < latLngs.length) {
            Polyline polyTrack = mMap.addPolyline(new PolylineOptions()
                    .add(latLngs)
                    .width(8)
                    .color(R.color.colorTrack));
            polyTrack.setZIndex(2.0f);
            listPolylyneTrack.add(polyTrack);
        }
    }


    class GetFromServer extends MapQuest {

        public GetFromServer(MapsActivity mapsActivity) {
            super(mapsActivity);
        }

        @Override
        public void onCallBack(String result) {
            Log.d(TAG, result);
            drawPoly(result);
        }
    }

    private void toastShow(String str) {
        mapsActivity.toastShow(str);
    }
}
