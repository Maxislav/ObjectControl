package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.atlas.mars.objectcontrol.R;

/**
 * Created by mars on 4/8/15.
 */
public class DialogSelectObj extends MyDialog {

    DisplayMetrics displayMetrics;
    private float dpHeight, dpWidth, density;
    public DialogSelectObj(Activity activity) {
        super(activity);
    }

    @Override
    public View onCreate() {
        displayMetrics = activity.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / density;
        dpWidth = displayMetrics.widthPixels / density;
        Log.d(TAG, "Density: " + density + " Width dp: " + dpWidth + " Width Pixels: " + displayMetrics.widthPixels);

        viewDialog = inflater.inflate(R.layout.dialog_select_obj, null);
        pw = new PopupWindow(viewDialog, FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        pw.setOutsideTouchable(false);
        pw.setFocusable(true);
        pw.setAnimationStyle(R.style.Animation);
        LinearLayout block =(LinearLayout) viewDialog.findViewById(R.id.block);

        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int)(310*density),FrameLayout.LayoutParams.WRAP_CONTENT);
        block.setLayoutParams(parms);
        return viewDialog;
    }

    @Override
    public void vHide(View view) {
        if(pw==null){
            onCreate();
        }
        if(pw.isShowing()){
            pw.dismiss();
        }else{
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void onDismiss() {
        pw.dismiss();
    }
}
