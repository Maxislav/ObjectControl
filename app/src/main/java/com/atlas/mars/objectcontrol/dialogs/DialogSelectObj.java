package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.atlas.mars.objectcontrol.R;

/**
 * Created by mars on 4/8/15.
 */
public class DialogSelectObj extends MyDialog {


    public DialogSelectObj(Activity activity) {
        super(activity);
    }

    @Override
    public View onCreate() {
        viewDialog = inflater.inflate(R.layout.dialog_select_obj, null);
        pw = new PopupWindow(viewDialog, FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        pw.setOutsideTouchable(false);
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
