package com.atlas.mars.objectcontrol.gps;

import android.content.DialogInterface;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.atlas.mars.objectcontrol.R;

/**
 * Created by Администратор on 6/27/15.
 */
public class TrackButton implements View.OnClickListener {
    MapsActivity mapsActivity;
    ImageButton btnTrack;

    TrackButton( MapsActivity mapsActivity,ImageButton btnTrack ){
        this.mapsActivity = mapsActivity;
        this.btnTrack = btnTrack;
        btnTrack.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
       /* OpenFileDialog fileDialog = new OpenFileDialog(mapsActivity);
        fileDialog.show();*/
        showPopupMenu(v);
    }
    private void showPopupMenu(View v){
        PopupMenu popupMenu = new PopupMenu(mapsActivity, v);
        popupMenu.inflate(R.menu.menu_traks_action);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.loadFromCd):
                        OpenFileDialog fileDialog = new OpenFileDialog(mapsActivity);
                        mapsActivity.toastShow("JSON format only");
                        fileDialog.show();
                        return  true;
                    case (R.id.createRoute):

                        return true;

                }
                return false;
            }
        });
        popupMenu.show();
    }
}
