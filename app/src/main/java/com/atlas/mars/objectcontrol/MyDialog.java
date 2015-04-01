package com.atlas.mars.objectcontrol;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

        for(int i = 0; i<arrayList.size(); i++){
            setRow(parent, arrayList.get(i));
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
    private void setRow(LinearLayout parent, HashMap<String, String> map){
        View v = inflater.inflate(R.layout.row_object, null);
        String selected = map.get(db.VALUE_SELECTED);
        ViewGroup vg = (ViewGroup)v;
        CheckBox checkBox = (CheckBox)vg.getChildAt(0);

        if(selected!= null && selected.equals("1")){
            checkBox.setChecked(true);
        }

        checkBox.setText(map.get(db.VALUE_NAME));
        setChecked(checkBox, map);

        parent.addView(v);
    }

    private void setChecked(final CheckBox checkBox, final HashMap <String,String> map){
        final String id = map.get(db.UID);
        final MainActivity my =(MainActivity) activity;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBox.isChecked()){
                    my.setActiveObject(map.get(db.VALUE_NAME), id, true);
                    db.setValueSelected(id, true);
                }else{
                    my.setActiveObject(map.get(db.VALUE_NAME), id, false);
                    db.setValueSelected(id, false);
                }
            }
        });

    }

    private ArrayList<HashMap> getDevices(){
        ArrayList<HashMap> arrayList = db.getListDevices();
        return arrayList;
    }

    public interface OnCreateObjButton {
        void onCreateObjButton(String msg);
    }
}
