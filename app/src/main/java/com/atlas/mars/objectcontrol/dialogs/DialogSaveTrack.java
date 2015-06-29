package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.MyJQuery;
import com.atlas.mars.objectcontrol.R;

/**
 * Created by Администратор on 6/29/15.
 */
abstract public  class DialogSaveTrack extends MyDialog implements View.OnClickListener{

    public DialogSaveTrack(Activity activity) {
        super(activity);
    }

    @Override
    public View onCreate() {
        displayMetrics = activity.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / density;
        dpWidth = displayMetrics.widthPixels / density;
        Log.d(TAG, "Density: " + density + " Width dp: " + dpWidth + " Width Pixels: " + displayMetrics.widthPixels);
        viewDialog = inflater.inflate(R.layout.dialog_save_track, null);
        pw = new PopupWindow(viewDialog, FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
       // pw.setOutsideTouchable(false);
        pw.setAnimationStyle(R.style.Animation);
        contentDialog = (LinearLayout)viewDialog.findViewById(R.id.contentDialog);
        LinearLayout block =(LinearLayout) viewDialog.findViewById(R.id.block);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int)(310*density),FrameLayout.LayoutParams.WRAP_CONTENT);
        parms.gravity = Gravity.TOP;
        float marginTop = 120*density;
        parms.setMargins(0,((int)(marginTop)), 0, 0);
        block.setLayoutParams(parms);
        viewDialog.findViewById(R.id.btn_ok).setOnClickListener(this);
        viewDialog.findViewById(R.id.btn_cancel).setOnClickListener(this);
        pw.setFocusable(true);
      //  pw.update();
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



    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
    abstract  public void onCancel();


}
