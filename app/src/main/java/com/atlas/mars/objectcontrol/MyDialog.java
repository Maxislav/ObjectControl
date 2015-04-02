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
    View dialog;
    FrameLayout btn_ok, btn_cancel;
    MyDialog(Activity activity){
       this.activity = activity;
       inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       db = new DataBaseHelper(activity);
    }

    protected void onCreate(View view){
        if(pw!=null && pw.isShowing()){
            pw.dismiss();
            return;
        }
        dialog = inflater.inflate(R.layout.dialog_select_obj, null);
        pw = new PopupWindow(dialog, FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        pw.setOutsideTouchable(false);
        pw.showAtLocation(view, Gravity.CENTER, 0, 0);
        btn_ok = (FrameLayout) dialog.findViewById(R.id.btn_ok);
        btn_cancel = (FrameLayout) dialog.findViewById(R.id.btn_cancel);
    }

    public void dialogSelectObj(View view){
        onCreate(view);

        LinearLayout parent = (LinearLayout) dialog.findViewById(R.id.parent);
        ArrayList<HashMap> arrayList = getDevices();
        for(int i = 0; i<arrayList.size(); i++){
            setRow(parent, arrayList.get(i), true);
        }
        clickListener(btn_ok);
        clickListener(btn_cancel);
    }

    public void dialogMekeCommand(View view){
        onCreate(view);

        LinearLayout parent = (LinearLayout) dialog.findViewById(R.id.parent);
        ArrayList<HashMap> arrayList = getDevices();
        for(int i = 0; i<arrayList.size(); i++){
            setRow(parent, arrayList.get(i), false);
        }
       // FrameLayout btn_cancel = (FrameLayout) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

    }

    private void clickListener(View v){
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
    private void setRow(LinearLayout parent, HashMap<String, String> map, boolean checked){
        View v = inflater.inflate(R.layout.row_object, null);
        String selected = map.get(db.VALUE_SELECTED);
        ViewGroup vg = (ViewGroup)v;
        CheckBox checkBox = (CheckBox)vg.getChildAt(0);
        if(checked){
            if(selected!= null && selected.equals("1")){
                checkBox.setChecked(true);
            }

            setChecked(checkBox, map);
        }
        checkBox.setText(map.get(db.VALUE_NAME));
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
