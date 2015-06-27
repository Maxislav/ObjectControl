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

import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.http.MapQuest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Администратор on 6/27/15.
 */
public class TrackButton implements View.OnClickListener, GoogleMap.OnMapLongClickListener , PopupMenu.OnMenuItemClickListener{
    private  final  String TAG = "myLog";
    MapsActivity mapsActivity;
    ImageButton btnTrack;
    LinearLayout layoutRouteType;
    List<LinearLayout> listRouteType;
    private GoogleMap mMap;

    TrackButton( MapsActivity mapsActivity,ImageButton btnTrack, GoogleMap mMap ){
        this.mapsActivity = mapsActivity;
        this.btnTrack = btnTrack;
        this.mMap = mMap;
        btnTrack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnTrack:
                showPopupMenu(v);
                break;
            case R.id.car:
            case R.id.velo:
            case R.id.moto:
                setActiveRouteType((LinearLayout)v);
                break;
        }
    }


    private void setActiveRouteType(LinearLayout layout){
        for(LinearLayout _layout : listRouteType){
            if(_layout == layout){
                _layout.setBackgroundResource(R.color.activeRouteType);
            }else {
                _layout.setBackground(null);
            }
        }
    }

    private void inflateLayoutRouteType(){
        LayoutInflater layoutInflater = mapsActivity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.route_type,null, false);
        FrameLayout frameLayout = (FrameLayout)mapsActivity.findViewById(R.id.globalLayout);
        layoutRouteType = (LinearLayout)view;
        frameLayout.addView(layoutRouteType);
        float width = mapsActivity.getResources().getDisplayMetrics().density *150;
        float height  = mapsActivity.getResources().getDisplayMetrics().density *50;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int)width,(int)height);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        layoutRouteType.setLayoutParams(layoutParams);
        listRouteType = new ArrayList<>();
        LinearLayout layoutCar =(LinearLayout)layoutRouteType.findViewById(R.id.car);
        LinearLayout layoutMoto =(LinearLayout)layoutRouteType.findViewById(R.id.moto);
        LinearLayout layoutVelo =(LinearLayout)layoutRouteType.findViewById(R.id.velo);
        listRouteType.add(layoutCar);
        listRouteType.add(layoutMoto);
        listRouteType.add(layoutVelo);
        for(LinearLayout layout : listRouteType){
            layout.setOnClickListener(this);
        }

    }

    private void showPopupMenu(View v){
        PopupMenu popupMenu = new PopupMenu(mapsActivity, v);
        popupMenu.inflate(R.menu.menu_traks_action);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        toastShow("" + latLng.latitude + ": " + latLng.longitude);
        mMap.setOnMapLongClickListener(null);
        GetFromServer  getFromServer= new GetFromServer(mapsActivity);
        String from = String.valueOf(mapsActivity.myPos.latitude)+","+String.valueOf(mapsActivity.myPos.longitude);
        String to = String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude);
        //getFromServer.findRoute("50.3891,30.49373", "50.446,30.44852");
        getFromServer.findRoute(from, to);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case (R.id.loadFromCd):
                    OpenFileDialog fileDialog = new OpenFileDialog(mapsActivity);
                    mapsActivity.toastShow("JSON format only");
                    fileDialog.show();
                    return  true;
                case (R.id.createRoute):
                    if(layoutRouteType==null){
                        inflateLayoutRouteType();
                    }
                    mMap.setOnMapLongClickListener(this);
                    return true;
            }
            return false;
    }

    public void drawPoly(String result){
        Track track = new Track();
        LatLng[] latLngs = track.parseTrack(result);

        if(latLngs!=null && 1<latLngs.length){
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(latLngs)
                    .width(5)
                    .color(Color.BLUE));
            line.setZIndex(2.0f);
        }


       // Mytrack mytrack = new Mytrack(path);

        /*Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(mytrack.getTrack())
                .width(5)
                .color(Color.BLUE));
        line.setZIndex(2.0f);*/
        //return null;
    }

    private void toastShow(String str){
        mapsActivity.toastShow(str);
       // GetFromServer getFromServer = new GetFromServer(mapsActivity);
      //  getFromServer.findRoute("50.3891,30.49373", "50.446,30.44852");

    }

    class GetFromServer extends MapQuest{

        public GetFromServer(MapsActivity mapsActivity) {
            super(mapsActivity);
        }
        @Override
        public void onCallBack(String result){
            Log.d(TAG, result);
            drawPoly(result);
        }
    }
}
