package com.atlas.mars.objectcontrol.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.dialogs.DialogSaveTrack;
import com.atlas.mars.objectcontrol.http.MapQuest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Администратор on 6/27/15.
 */
public class TrackButton implements View.OnClickListener, GoogleMap.OnMapLongClickListener, PopupMenu.OnMenuItemClickListener {
    private final String TAG = "myLog";
    InflateRoteMenu menu;
    MapsActivity mapsActivity;
    ImageButton btnTrack;
    LinearLayout layoutRouteType;
    LinearLayout layoutRouteMenu;
    LinearLayout layoutStartEnd;

    List<LinearLayout> listRouteType;
    private GoogleMap mMap;
    List<Marker> listMarkerPoints; //Лист  маркеров по пути маршрута;
    List<Polyline> listPolylineTrack;
    HashMap<String, String> mapSetting;
    String from;
    DataBaseHelper db;
    String timeStampCreated;
    long idTrack = 0;
    GetFromServer getFromServer;
    public boolean toObject = false;

    TrackButton(MapsActivity mapsActivity, ImageButton btnTrack, GoogleMap mMap) {
        this.mapsActivity = mapsActivity;
        this.btnTrack = btnTrack;
        this.mMap = mMap;
        menu = new InflateRoteMenu(mapsActivity, this);

        listMarkerPoints = new ArrayList<>();
        listPolylineTrack = new ArrayList<>();
        btnTrack.setOnClickListener(this);
        db = new DataBaseHelper(mapsActivity);
        mapSetting = DataBaseHelper.hashSetting;
        listRouteType = new ArrayList<>();
        if (mapSetting.get("startTrackDraw") == null) {
            mapSetting.put("startTrackDraw", "current");
            db.setSetting(mapSetting);
        }
        if (mapSetting.get(DataBaseHelper.MAP_CURRENT_ID_TRACK) != null && !mapSetting.get(DataBaseHelper.MAP_CURRENT_ID_TRACK).equals("0")) {
            onSelectIdTrack(mapSetting.get(DataBaseHelper.MAP_CURRENT_ID_TRACK));
        }
    }

    @Override
    public void onClick(View v) {
        PopupMenu popupMenu;
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
            case R.id.save:
                saveTrack(v);
                break;
            case R.id.back:
                stepBack();
                break;
            case R.id.closePath:
                closePath();
                break;
            case R.id.delTrack:
                delTrack();
                break;
            case R.id.fromPoint:
                popupMenu = new PopupMenu(mapsActivity, v);
                popupMenu.inflate(R.menu.menu_select_from);

                if (mapSetting.get("startTrackDraw").equals("current")) {
                    popupMenu.getMenu().findItem(R.id.current).setChecked(true);
                }
                if (mapSetting.get("startTrackDraw").equals("hand")) {
                    popupMenu.getMenu().findItem(R.id.handStart).setChecked(true);
                }

                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            case R.id.toPoint:
                if(mapsActivity.countObj<1){
                    toastShow("Empty list objects. Select end point on map.");
                    return;
                }
                popupMenu = new PopupMenu(mapsActivity, v);
                popupMenu.inflate(R.menu.menu_end_point);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;

        }
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.loadFromCd):
                OpenFileDialog fileDialog = new OpenFileDialog(mapsActivity);
                fileDialog.show();
                return true;
            case (R.id.createRoute):
                menu.showRouteType();
                menu.showRouteMenu();
                menu.showRouteStartEnd();
                mMap.setOnMapLongClickListener(this);
                return true;
            case (R.id.trackList):
                Intent questionIntent;
                questionIntent = new Intent(mapsActivity, TrackListActivity.class);
                mapsActivity.startActivityForResult(questionIntent, 1);
                return true;
            case (R.id.current):
                item.setChecked(true);
                mapSetting.put("startTrackDraw", "current");
                db.setSetting(mapSetting);
                return true;
            case (R.id.handStart):
                item.setChecked(true);
                mapSetting.put("startTrackDraw", "hand");
                db.setSetting(mapSetting);
                return true;
            case R.id.selectObject:
                toObject = true;
                if (mapSetting.get("startTrackDraw").equals("hand") && listMarkerPoints.size() < 1) {
                    toastShow("Are necessary select start point");
                    return true;
                }
                mapsActivity.showListObgects();
                return true;
        }
        return false;
    }

    protected void offLongClickListener(){
        mMap.setOnMapLongClickListener(null);
    }


    public void setActiveRouteType(LinearLayout layout) {
        for (LinearLayout _layout : listRouteType) {
            if (_layout == layout) {
                _layout.setBackground(mapsActivity.getResources().getDrawable(R.drawable.background_select));
            } else {
                _layout.setBackground(null);
            }
        }
    }



    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(mapsActivity, v);
        popupMenu.inflate(R.menu.menu_traks_action);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    public void addMarker(LatLng latLng) {

        Marker trackPointMarker = mMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.track_control_point)));
        listMarkerPoints.add(trackPointMarker);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (getFromServer == null) {
            getFromServer = new GetFromServer(mapsActivity);
        }

        toastShow("Accept: " + round(latLng.latitude, 4) + "; " + round(latLng.longitude, 4));

        if (mapSetting.get("startTrackDraw").equals("hand")) {
            if (listMarkerPoints.size() == 0) {
                addMarker(latLng);
                return;
            }
            addMarker(latLng);
            Marker from = listMarkerPoints.get(listMarkerPoints.size() - 2);
            Marker to = listMarkerPoints.get(listMarkerPoints.size() - 1);
            String strFrom = String.valueOf(from.getPosition().latitude) + "," + String.valueOf(from.getPosition().longitude);
            String strTo = String.valueOf(to.getPosition().latitude) + "," + String.valueOf(to.getPosition().longitude);

            if(mapSetting.get(db.MAP_ROUTE_TYPE).equals("hand")){
                LatLng[] latLngs = new LatLng[2];
                latLngs[0] = new LatLng(from.getPosition().latitude, from.getPosition().longitude);
                latLngs[1] = new LatLng(to.getPosition().latitude, to.getPosition().longitude);
                drawPoly(latLngs);
            }else{
                getFromServer.findRoute(strFrom, strTo, mapSetting.get(DataBaseHelper.MAP_ROUTE_TYPE));
            }


            return;
        }

        if (mapSetting.get("startTrackDraw").equals("current")) {
            if (listMarkerPoints.size() == 0) {
                addMarker(mapsActivity.myPos);
            }
            addMarker(latLng);
            Marker from = listMarkerPoints.get(listMarkerPoints.size() - 2);
            Marker to = listMarkerPoints.get(listMarkerPoints.size() - 1);
            String strFrom = String.valueOf(from.getPosition().latitude) + "," + String.valueOf(from.getPosition().longitude);
            String strTo = String.valueOf(to.getPosition().latitude) + "," + String.valueOf(to.getPosition().longitude);

            if(mapSetting.get(db.MAP_ROUTE_TYPE).equals("hand")) {
                LatLng[] latLngs = new LatLng[2];
                latLngs[0] = new LatLng(from.getPosition().latitude, from.getPosition().longitude);
                latLngs[1] = new LatLng(to.getPosition().latitude, to.getPosition().longitude);
                drawPoly(latLngs);
            }else{
                getFromServer.findRoute(strFrom, strTo, mapSetting.get(DataBaseHelper.MAP_ROUTE_TYPE));
            }
            return;
        }


    }

    private void stepBack() {
        if (getFromServer != null) {
            getFromServer.onCancelled();
        }
        if (0 < listMarkerPoints.size()) {
            listMarkerPoints.remove(listMarkerPoints.size() - 1).remove();
            if (listPolylineTrack.size() == listMarkerPoints.size() && 0 < listPolylineTrack.size()) {
                listPolylineTrack.remove(listPolylineTrack.size() - 1).remove();
            }
        }
    }

    private void closePath() {
        if (listMarkerPoints.size() < 2) {
            toastShow("Are necessary two points");
            return;
        }
        LatLng latLng = new LatLng(listMarkerPoints.get(0).getPosition().latitude, listMarkerPoints.get(0).getPosition().longitude);
        addMarker(latLng);
       // addMarker(latLng);
        Marker from = listMarkerPoints.get(listMarkerPoints.size() - 2);
        Marker to = listMarkerPoints.get(listMarkerPoints.size() - 1);
        toastShow("Accept: " + round(to.getPosition().latitude, 4) + "; " + round(to.getPosition().longitude, 4));
        String strFrom = String.valueOf(from.getPosition().latitude) + "," + String.valueOf(from.getPosition().longitude);
        String strTo = String.valueOf(to.getPosition().latitude) + "," + String.valueOf(to.getPosition().longitude);
        if(mapSetting.get(db.MAP_ROUTE_TYPE).equals("hand")) {
            LatLng[] latLngs = new LatLng[2];
            latLngs[0] = new LatLng(from.getPosition().latitude, from.getPosition().longitude);
            latLngs[1] = new LatLng(to.getPosition().latitude, to.getPosition().longitude);
            drawPoly(latLngs);
        }else{
            if(getFromServer == null){
                getFromServer = new GetFromServer(mapsActivity);
            }
            getFromServer.findRoute(strFrom, strTo, mapSetting.get(DataBaseHelper.MAP_ROUTE_TYPE));
        }
    }

    private void delTrack() {
        while (0 < listMarkerPoints.size()) {
            listMarkerPoints.remove(listMarkerPoints.size() - 1).remove();
        }
        while (0 < listPolylineTrack.size()) {
            listPolylineTrack.remove(listPolylineTrack.size() - 1).remove();
        }
        idTrack = 0;
        mapSetting.put(DataBaseHelper.MAP_CURRENT_ID_TRACK, "0");
        db.setSetting(mapSetting);
    }

    public void drawPoly(String result) {
        if (result != null) {
            TrackParser track = new TrackParser(result);
            LatLng[] latLngs = track.getLatLngs();
            drawPoly(latLngs);
        }

    }

    public void drawPoly(LatLng[] latLngs) {
        if (latLngs != null && 1 < latLngs.length) {
            Polyline polyTrack = mMap.addPolyline(new PolylineOptions()
                    .add(latLngs)
                    .width(8)
                    .color(mapsActivity.getResources().getColor(R.color.colorTrack)));
            polyTrack.setZIndex(2.0f);
            listPolylineTrack.add(polyTrack);
            if(menu.routeMenu == null || !menu.routeMenu.isShown()){
                menu.showRouteMenu();
            }
        }
    }

    public void onPause() {
        Log.d(TAG, "++++ ID Track: " + String.valueOf(idTrack));
        mapSetting.put(DataBaseHelper.MAP_CURRENT_ID_TRACK, String.valueOf(idTrack));
        // db.setSetting(mapSetting);
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

    private void saveTrack(View v) {
        if (listPolylineTrack.size() < 1) {
            toastShow("Empty track");
            return;
        }
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeStampCreated = formatter.format(now);
       // idTrack = db.createRowNameTrack();
        DialogSaveTrack dialogSaveTrack = new Dialog(mapsActivity);
        dialogSaveTrack.onCreate();
        dialogSaveTrack.vHide(v);
    }


    class Dialog extends DialogSaveTrack {
        private long _idTrack = idTrack;
        private class MyHundler extends Handler {
            public static final int ID_0 = 0;

            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0:
                        toastShow(msg.obj.toString());
                        Log.d(TAG, msg.obj.toString());

                        break;
                }
            }
        }

        private class MyThread extends Thread {
            final List<HashMap<String, Double>> listControlPointsTrack;
            final DataBaseHelper _db = new DataBaseHelper(mapsActivity);
            Handler h;
            String name;

            MyThread(List<HashMap<String, Double>> listControlPointsTrack, Handler h) {
                super();
                this.listControlPointsTrack = listControlPointsTrack;
                this.h = h;
                name = ((TextView) contentDialog.findViewById(R.id.edTextName)).getText().toString();
            }

            @Override
            public void run() {
                if (_db.fillRowNameTrack(idTrack, timeStampCreated, name, listControlPointsTrack)) {
                    Message msg = Message.obtain(h, MyHundler.ID_0);
                    msg.obj = "Save Ok, ID: " + idTrack;
                    h.sendMessage(msg);
                }
            }
        }

        public Dialog(Activity activity) {
            super(activity);
        }

        @Override
        public void setValueText(TextView text) {
            text.setText(timeStampCreated);
        }

        @Override
        public void onOk() {
            idTrack = db.createRowNameTrack();
            final Handler h = new MyHundler();
            List<HashMap<String, Double>> listControlPointsTrack = new ArrayList<>();
            for (Polyline line : listPolylineTrack) {
                List<LatLng> latLngList = line.getPoints();
                for (LatLng latLng : latLngList) {
                    Double lat = latLng.latitude;
                    Double lng = latLng.longitude;
                    HashMap<String, Double> hm = new HashMap<>();
                    hm.put("lat", lat);
                    hm.put("lng", lng);
                    listControlPointsTrack.add(hm);
                }
            }
            Thread thread = new MyThread(listControlPointsTrack, h);
            thread.start();
        }

        @Override
        public void onCancel() {
            /*if (!db.deleteRowNameTrack(idTrack)) toastShow("Error");
            idTrack = _idTrack;*/
        }
    }

    public void onSelectIdTrack(String id) {
        idTrack = Long.valueOf(id).longValue();
        // toastShow("Select id " + id);
        LatLng[] latLngs = db.getTrack(id);
        if(latLngs.length<1){
           return;
        }
        addMarker(latLngs[0]);
        addMarker(latLngs[latLngs.length - 1]);
        drawPoly(latLngs);
    }

    public void onListObjectClick(LatLng latLng) {
        if(!toObject){
            return;
        }
        if (listMarkerPoints.size() < 1 && mapSetting.get("startTrackDraw").equals("current")) {
            if(mapsActivity.myPos==null){
                toastShow("Current position not set");
                return;
            }
            addMarker(mapsActivity.myPos);
        }
        addMarker(latLng);
        toObject = false;
        Marker from = listMarkerPoints.get(listMarkerPoints.size() - 2);
        Marker to = listMarkerPoints.get(listMarkerPoints.size() - 1);
        toastShow("Accept: " + round(to.getPosition().latitude, 4) + "; " + round(to.getPosition().longitude, 4));
        String strFrom = String.valueOf(from.getPosition().latitude) + "," + String.valueOf(from.getPosition().longitude);
        String strTo = String.valueOf(to.getPosition().latitude) + "," + String.valueOf(to.getPosition().longitude);
        if (getFromServer == null) {
            getFromServer = new GetFromServer(mapsActivity);
        }
        getFromServer.findRoute(strFrom, strTo, mapSetting.get(DataBaseHelper.MAP_ROUTE_TYPE));
        mapsActivity.hideListObject();
    }

    private void toastShow(String str) {
        mapsActivity.toastShow(str);
    }

    private double round(double d, int prec) {
        return new BigDecimal(d).setScale(prec, RoundingMode.UP).doubleValue();
    }
}
