package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.gps.TrackParser;

import java.util.List;

/**
 * Created by Администратор on 6/29/15.
 */
abstract public class DialogSaveTrack extends MyDialog implements View.OnClickListener {
    public Double distance;

    public DialogSaveTrack(Activity activity) {
        super(activity);
    }

    public void setDistance(List<Double> distance){
        this.distance = 0.0;
        for(Double d : distance){
            this.distance+=d;
        }
        this.distance = TrackParser.round(this.distance,2);
    }

    @Override
    public View onCreate() {

        displayMetrics = activity.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / density;
        dpWidth = displayMetrics.widthPixels / density;
        Log.d(TAG, "Density: " + density + " Width dp: " + dpWidth + " Width Pixels: " + displayMetrics.widthPixels);
        viewDialog = inflater.inflate(R.layout.dialog_save_track, null);
        pw = new PopupWindow(viewDialog, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        // pw.setOutsideTouchable(false);
        pw.setAnimationStyle(R.style.Animation);
        contentDialog = (LinearLayout) viewDialog.findViewById(R.id.contentDialog);


        LinearLayout block = (LinearLayout) viewDialog.findViewById(R.id.block);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int) (310 * density), FrameLayout.LayoutParams.WRAP_CONTENT);
        parms.gravity = Gravity.TOP;
        parms.setMargins(0, ((int) (120 * density)), 0, 0);
        block.setLayoutParams(parms);
        viewDialog.findViewById(R.id.btn_ok).setOnClickListener(this);
        viewDialog.findViewById(R.id.btn_cancel).setOnClickListener(this);
        pw.setFocusable(true);
        TextView edTextName = (TextView) contentDialog.findViewById(R.id.edTextName);
        TextView distanceText = (TextView) contentDialog.findViewById(R.id.distance);
        setValueText(edTextName, distanceText);
        return viewDialog;
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
    public void onDismiss() {
        pw.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                onOk();
                onDismiss();
                break;
            case R.id.btn_cancel:
                onCancel();
                onDismiss();
                break;
        }
    }

    abstract public void setValueText(TextView text, TextView distanceText);

    abstract public void onOk();

    abstract public void onCancel();

}
