package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.gps.MapsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mars on 6/30/15.
 */
abstract public class DialogListTracks extends MyDialog  implements View.OnClickListener {
    List<View> listRows;
    String selectId;

    public DialogListTracks(Activity activity) {
        super(activity);
    }

    @Override
    public View onCreate() {
        displayMetrics = activity.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / density;
        dpWidth = displayMetrics.widthPixels / density;
        Log.d(TAG, "Density: " + density + " Width dp: " + dpWidth + " Width Pixels: " + displayMetrics.widthPixels);
        viewDialog = inflater.inflate(R.layout.dialog_list_tracks, null);
        pw = new PopupWindow(viewDialog, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        // pw.setOutsideTouchable(false);
        pw.setAnimationStyle(R.style.Animation);
        contentDialog = (LinearLayout) viewDialog.findViewById(R.id.contentDialog);


        LinearLayout block = (LinearLayout) viewDialog.findViewById(R.id.block);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int) (310 * density), (int)(dpHeight-(40*density)));
        contentDialog.setLayoutParams(parms);
        viewDialog.findViewById(R.id.btn_ok).setOnClickListener(this);
        viewDialog.findViewById(R.id.btn_cancel).setOnClickListener(this);
        pw.setFocusable(true);
        inflateContent();

        return viewDialog;
    }

    private void inflateContent(){
        List<HashMap<String, String>> list = db.getTraksNameRows();
        LinearLayout  scope = (LinearLayout) contentDialog.findViewById(R.id.linearLayoutScroll);
        listRows = new ArrayList<>();
        for(HashMap<String, String> map : list){
            View rowView =    inflater.inflate(R.layout.track_row, null);
            scope.addView(rowView);
            ArrayList<View> fields = jQuery.findViewByTagClass((ViewGroup)rowView, TextView.class);
            TextView textViewId =  (TextView)fields.get(0);
            TextView textViewName =  (TextView)fields.get(1);
            TextView textViewDate=  (TextView)fields.get(2);
            textViewId.setText(map.get("id")+".");
            textViewName.setText(map.get("name"));
            textViewDate.setText(map.get("date"));
            listRows.add(rowView);
            rowSelect(rowView, map.get("id"));
        }
    }

    private void rowSelect(final View row, final String _id){
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectId = _id;
                for(View _row : listRows){
                    if(_row == row){
                        _row.setBackgroundResource(R.color.activeRouteType);
                    }else{
                        _row.setBackground(null);
                    }
                }
            }
        });
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(activity, v);
                popupMenu.inflate(R.menu.del_track);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.del:
                                Log.d(TAG, "Delete track " + _id);
                                LinearLayout parent = (LinearLayout) contentDialog.findViewById(R.id.linearLayoutScroll);
                                parent.removeView(row);
                                return true;
                        }

                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });

    }

    @Override
    public void vHide(View view) {
        if (pw == null) {
            onCreate();
        }
        if (pw.isShowing()) {
            pw.dismiss();
        } else {
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    void onDismiss() {
        pw.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                onOk(selectId);
                onDismiss();
                break;
            case R.id.btn_cancel:
                selectId = null;
                onCancel();
                onDismiss();
                break;
        }
    }
    abstract public void onOk(String id);

    abstract public void onCancel();
}
