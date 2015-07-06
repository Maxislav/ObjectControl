package com.atlas.mars.objectcontrol.gps;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.R;

import java.util.HashMap;

/**
 * Created by mars on 7/3/15.
 */
public class InflateRoteMenu implements View.OnClickListener {
    Activity activity;
    TrackButton trackButton;
    LayoutInflater layoutInflater;
    LinearLayout routeType;
    LinearLayout routeMenu;
    LinearLayout routeStartEnd;
    FrameLayout globalLayout;
    float density, width, height;
    DataBaseHelper db;
    HashMap<String, String> mapSetting;


    InflateRoteMenu(Activity activity, TrackButton trackButton) {
        this.activity = activity;
        this.trackButton = trackButton;
        layoutInflater = activity.getLayoutInflater();
        globalLayout = (FrameLayout) activity.findViewById(R.id.globalLayout);
        density = activity.getResources().getDisplayMetrics().density;
        db = new DataBaseHelper(activity);
        mapSetting = DataBaseHelper.hashSetting;
    }

    public void showRouteType() {
        if (routeType == null) {
            inflateRouteType();
        }
        routeType.setVisibility(View.VISIBLE);
    }

    public void hideRouteType() {
        if (routeType == null) {
            inflateRouteType();
        }
        routeType.setVisibility(View.INVISIBLE);
    }

    protected void showRouteMenu() {
        if (routeMenu == null) {
            inflateRouteMenu();
        }
        if (!routeMenu.isShown()) {
            routeMenu.setVisibility(View.VISIBLE);
        }

    }

    protected void hideRouteMenu() {
        if (routeMenu == null) {
            inflateRouteMenu();
        }
        routeMenu.setVisibility(View.INVISIBLE);
    }

    protected void showRouteStartEnd() {
        if (routeStartEnd == null) {
            inflateStartEnd();
        }
        routeStartEnd.setVisibility(View.VISIBLE);
    }

    protected void hideRouteStartEnd() {
        if (routeStartEnd == null) {
            inflateStartEnd();
        }
        routeStartEnd.setVisibility(View.INVISIBLE);
    }


    private void inflateRouteType() {
        View view = layoutInflater.inflate(R.layout.route_type, null, false);
        routeType = (LinearLayout) view;
        routeType.setVisibility(View.INVISIBLE);
        globalLayout.addView(routeType);
        width = activity.getResources().getDisplayMetrics().density * 160;
        height = activity.getResources().getDisplayMetrics().density * 40;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) width, (int) height);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        routeType.setLayoutParams(layoutParams);
        LinearLayout layoutCar = (LinearLayout) routeType.findViewById(R.id.car);
        LinearLayout layoutMoto = (LinearLayout) routeType.findViewById(R.id.moto);
        LinearLayout layoutVelo = (LinearLayout) routeType.findViewById(R.id.velo);
        LinearLayout layoutHand = (LinearLayout) routeType.findViewById(R.id.hand);
        trackButton.listRouteType.add(layoutCar);
        trackButton.listRouteType.add(layoutMoto);
        trackButton.listRouteType.add(layoutVelo);
        trackButton.listRouteType.add(layoutHand);
        for (LinearLayout layout : trackButton.listRouteType) {
            layout.setOnClickListener(trackButton);
        }
        String mapRouteType = mapSetting.get(DataBaseHelper.MAP_ROUTE_TYPE);
        if (mapRouteType == null) {
            trackButton.setActiveRouteType(layoutCar);
            mapSetting.put(DataBaseHelper.MAP_ROUTE_TYPE, "car");
            db.setSetting(mapSetting);
        } else {
            switch (mapRouteType) {
                case "car":
                    trackButton.setActiveRouteType(layoutCar);
                    break;
                case "moto":
                    trackButton.setActiveRouteType(layoutMoto);
                    break;
                case "velo":
                    trackButton.setActiveRouteType(layoutVelo);
                    break;
                case "hand":
                    trackButton.setActiveRouteType(layoutHand);
                    break;
            }
        }
    }

    private void inflateRouteMenu() {
        View view = layoutInflater.inflate(R.layout.route_menu, null, false);
        routeMenu = (LinearLayout) view;
        routeMenu.setVisibility(View.INVISIBLE);
        globalLayout.addView(routeMenu);
        float width = density * 200;
        float height = density * 40;
        FrameLayout.LayoutParams layoutParamsMenu = new FrameLayout.LayoutParams((int) width, (int) height);
        layoutParamsMenu.gravity = Gravity.TOP | Gravity.CENTER;
        routeMenu.setLayoutParams(layoutParamsMenu);
        routeMenu.findViewById(R.id.save).setOnClickListener(trackButton);
        routeMenu.findViewById(R.id.back).setOnClickListener(trackButton);
        routeMenu.findViewById(R.id.closePath).setOnClickListener(trackButton);
        routeMenu.findViewById(R.id.delTrack).setOnClickListener(trackButton);
        routeMenu.findViewById(R.id.closeAll).setOnClickListener(this);
    }

    private void inflateStartEnd() {
        routeStartEnd = (LinearLayout) layoutInflater.inflate(R.layout.track_start_end_menu, null, false);
        routeStartEnd.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams layoutStartEndParams = new LinearLayout.LayoutParams((int) (40 * density), (int) (100 * density));
        layoutStartEndParams.gravity = Gravity.TOP | Gravity.LEFT;
        routeStartEnd.setLayoutParams(layoutStartEndParams);
        routeStartEnd.findViewById(R.id.fromPoint).setOnClickListener(trackButton);
        routeStartEnd.findViewById(R.id.toPoint).setOnClickListener(trackButton);
        globalLayout.addView(routeStartEnd);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeAll:
                if (routeStartEnd != null && routeStartEnd.isShown()) {
                    hideRouteStartEnd();
                    return;
                }
                if (routeType != null && routeType.isShown()) {
                    hideRouteType();
                    trackButton.offLongClickListener();
                    return;
                }
                hideRouteMenu();

                break;

        }
    }
}
