package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.atlas.mars.objectcontrol.R;

import java.util.HashMap;

/**
 * Created by Администратор on 4/12/15.
 */
public class DialogEditObj extends MyDialog {
    View content;

    public DialogEditObj(Activity activity) {
        super(activity);
    }

    @Override
    public View onCreate() {
        viewDialog = inflater.inflate(R.layout.dialog_select_obj,null);
        pw = new PopupWindow(viewDialog, FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        pw.setOutsideTouchable(false);
        pw.setFocusable(true);
        FrameLayout btnOk = (FrameLayout)viewDialog.findViewById(R.id.btn_ok);
        FrameLayout btnCancel = (FrameLayout)viewDialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

        return viewDialog;
    }

    @Override
    public  void vHide(View view) {
        if(pw==null){
            onCreate();
        }
        if(pw.isShowing()){
            pw.dismiss();
            return;
        }else{

                pw.showAtLocation(view, Gravity.CENTER, 0, 0);


        }
    }

    @Override
    public void onDismiss() {
        pw.dismiss();
    }

    public void dialogInflate(HashMap<String,String>map, LinearLayout row){
       // ((LinearLayout)viewDialog.findViewById(R.id.contentDialog)).removeAllViews();
        content = inflater.inflate(R.layout.contend_dialog_edit_obj,null);
        EditText edTextName = (EditText)content.findViewById(R.id.edTextName);
        edTextName.setText(map.get(db.VALUE_NAME));
        EditText edTextPhone = (EditText)content.findViewById(R.id.edTextPhone);
        edTextPhone.setText(map.get(db.VALUE_PHONE));
        ((LinearLayout)viewDialog.findViewById(R.id.contentDialog)).addView(content);

    }
}
