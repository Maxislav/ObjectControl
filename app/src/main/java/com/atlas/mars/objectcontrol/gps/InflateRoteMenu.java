package com.atlas.mars.objectcontrol.gps;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.Density;
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
    Density dns;


    InflateRoteMenu(Activity activity, TrackButton trackButton) {
        this.activity = activity;
        this.trackButton = trackButton;
        dns = new Density(activity);
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
        final Animation animIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.show_down);
        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                routeType.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        routeType.startAnimation(animIn);

    }

    public void hideRouteType() {
        if (routeType == null) {
            inflateRouteType();
        }
        final Animation animIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.hide_down);
        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // routeMenu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                routeType.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        routeType.startAnimation(animIn);
    }

    protected void showRouteMenu() {
        if (routeMenu == null) {
            inflateRouteMenu();
        }
        if (!routeMenu.isShown()) {

            final Animation animIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.show_up);
            animIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    routeMenu.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            routeMenu.startAnimation(animIn);


        }

    }

    protected void hideRouteMenu() {
        if (routeMenu == null) {
            inflateRouteMenu();
        }

        final Animation animIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.hide_up);
        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
               // routeMenu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                routeMenu.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        routeMenu.startAnimation(animIn);
    }

/*
    protected void showRouteStartEnd() {
        if (routeStartEnd == null) {
            inflateStartEnd();
        }
        routeStartEnd.setVisibility(View.VISIBLE);
    }
*/

   /* protected void hideRouteStartEnd() {
        if (routeStartEnd == null) {
            inflateStartEnd();
        }
        routeStartEnd.setVisibility(View.INVISIBLE);
    }*/


    private void inflateRouteType() {
        View view = layoutInflater.inflate(R.layout.route_type, null, false);
        routeType = (LinearLayout) view;
        routeType.setVisibility(View.INVISIBLE);
        globalLayout.addView(routeType, 1);
        width = activity.getResources().getDisplayMetrics().density * 200;
        height = activity.getResources().getDisplayMetrics().density * 40;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) width, (int) height);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        routeType.setLayoutParams(layoutParams);
        LinearLayout layoutCar = (LinearLayout) routeType.findViewById(R.id.car);
        LinearLayout layoutMoto = (LinearLayout) routeType.findViewById(R.id.moto);
        LinearLayout layoutVelo = (LinearLayout) routeType.findViewById(R.id.velo);
        LinearLayout layoutHand = (LinearLayout) routeType.findViewById(R.id.hand);

        ImageButton fromToPoint =  (ImageButton) routeType.findViewById(R.id.fromToPoint);
        fromToPoint.setOnClickListener(trackButton);

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
        globalLayout.addView(routeMenu, 1);
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

    /*private void inflateStartEnd() {
        routeStartEnd = (LinearLayout) layoutInflater.inflate(R.layout.track_start_end_menu, null, false);
        routeStartEnd.setVisibility(View.INVISIBLE);
        LinearLayout.LayoutParams layoutStartEndParams = new LinearLayout.LayoutParams((int) (40 * density), (int) (100 * density));
        layoutStartEndParams.gravity = Gravity.TOP | Gravity.LEFT;
        routeStartEnd.setLayoutParams(layoutStartEndParams);
        routeStartEnd.findViewById(R.id.fromPoint).setOnClickListener(trackButton);
        routeStartEnd.findViewById(R.id.toPoint).setOnClickListener(trackButton);
        globalLayout.addView(routeStartEnd, 1);

    }*/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeAll:
               /* if (routeStartEnd != null && routeStartEnd.isShown()) {
                    hideRouteStartEnd();
                    return;
                }*/
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
