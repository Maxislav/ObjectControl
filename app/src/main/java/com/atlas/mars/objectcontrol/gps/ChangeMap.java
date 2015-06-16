package com.atlas.mars.objectcontrol.gps;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.R;

/**
 * Created by mars on 6/16/15.
 */
public class ChangeMap {
    MapsActivity mapsActivity;
    ImageButton btnLayers;

    public ChangeMap(MapsActivity mapsActivity){
        this.mapsActivity = mapsActivity;
        _init();
    }

    private void  _init(){
        btnLayers = (ImageButton)mapsActivity.findViewById(R.id.btnLayers);
        btnLayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(mapsActivity, v);
        popupMenu.inflate(R.menu.menu_select_layer);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gglLayer:
                        mapsActivity.setTileLayer("ggl");
                        mapsActivity.mapSetting.put(DataBaseHelper.MAP_TYPE, "ggl");
                        if(mapsActivity.dataBaseHelper!=null){
                            mapsActivity. dataBaseHelper.setSetting(mapsActivity.mapSetting);
                        }
                        return  true;
                    case R.id.mapQuestLayer:
                        mapsActivity.setTileLayer("mapQuest");
                        mapsActivity.mapSetting.put(DataBaseHelper.MAP_TYPE, "mapQuest");
                        if(mapsActivity.dataBaseHelper!=null){
                            mapsActivity. dataBaseHelper.setSetting(mapsActivity.mapSetting);
                        }
                        return true;
                    case R.id.osmLayer:
                        mapsActivity.setTileLayer("osm");
                        mapsActivity.mapSetting.put(DataBaseHelper.MAP_TYPE, "osm");
                        if(mapsActivity.dataBaseHelper!=null){
                            mapsActivity. dataBaseHelper.setSetting(mapsActivity.mapSetting);
                        }
                        return true;
                }
                return false;
            }
        });

        /*popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {

            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(mapsActivity.getApplicationContext(), "onDismiss",
                        Toast.LENGTH_SHORT).show();
            }
        });
*/
        popupMenu.show();

    }

}
