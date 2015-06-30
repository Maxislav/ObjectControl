package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mars on 6/30/15.
 */
abstract public class DialogListTracks extends MyDialog  implements View.OnClickListener {

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
      //  block.setLayoutParams(parms);

        contentDialog.setLayoutParams(parms);

       /* ScrollView scrollView = (ScrollView)viewDialog.findViewById(R.id.scrollView);

        scrollView.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, 350));;*/


        viewDialog.findViewById(R.id.btn_ok).setOnClickListener(this);
        viewDialog.findViewById(R.id.btn_cancel).setOnClickListener(this);
        pw.setFocusable(true);
        inflateContent();
       // TextView edTextName = (TextView) contentDialog.findViewById(R.id.edTextName);
        //setValueText(edTextName);
        return viewDialog;
    }

    private void inflateContent(){
        List<HashMap<String, String>> list = db.getTraksNameRows();
        LinearLayout  scope = (LinearLayout) contentDialog.findViewById(R.id.linearLayoutScroll);
        for(HashMap<String, String> map : list){
            View rowView =    inflater.inflate(R.layout.track_row, null);
            scope.addView(rowView);
            ArrayList<View> fields = jQuery.findViewByTagClass((ViewGroup)rowView, TextView.class);
            TextView textViewName =  (TextView)fields.get(0);
            TextView textViewDate=  (TextView)fields.get(1);
            textViewName.setText(map.get("name"));
            textViewDate.setText(map.get("date"));
        }
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
                onOk();
                onDismiss();
                break;
            case R.id.btn_cancel:
                onCancel();
                onDismiss();
                break;
        }
    }
    abstract public void onOk();

    abstract public void onCancel();
}
