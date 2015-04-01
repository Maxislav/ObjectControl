package com.atlas.mars.objectcontrol;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mars on 4/1/15.
 */

public class MyDialog {

    Activity activity;
    PopupWindow pw;
    LayoutInflater inflater;
    DataBaseHelper db;
    MyDialog(Activity activity){
       this.activity = activity;
       inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       db = new DataBaseHelper(activity);
    }

    public void dialogSelectObj(View view){
        if(pw!=null && pw.isShowing()){
            pw.dismiss();
            return;
        }
        View v = inflater.inflate(R.layout.dialog_select_obj, null);
        pw = new PopupWindow(v, FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        pw.setOutsideTouchable(false);
        pw.showAtLocation(view, Gravity.CENTER, 0, 0);
        LinearLayout parent = (LinearLayout) v.findViewById(R.id.parent);

        ArrayList<HashMap> arrayList = getDevices();

        for(int i = 0; i<2; i++){
            setRow(parent);
        }
        FrameLayout btn_ok = (FrameLayout) v.findViewById(R.id.btn_ok);
        FrameLayout btn_cancel = (FrameLayout) v.findViewById(R.id.btn_cancel);
        clickListiner(btn_ok);
        clickListiner(btn_cancel);
    }

    private void clickListiner(View v){
        v.setOnClickListener(new View.OnClickListener() {
            //final PopupWindow _pw = pw;
            MainActivity my =(MainActivity) activity;
            @Override
            public void onClick(View v) {
                pw.dismiss();
                my.goToNewObjCreate();
            }
        });
    }
    private void setRow(LinearLayout parent){
        View v = inflater.inflate(R.layout.row_object, null);
        parent.addView(v);
    }

    private ArrayList<HashMap> getDevices(){
        ArrayList<HashMap> arrayList = db.getListDevices();
        return arrayList;
    }

    public interface OnCreateObjButton {
        void onCreateObjButton(String msg);
    }
}
