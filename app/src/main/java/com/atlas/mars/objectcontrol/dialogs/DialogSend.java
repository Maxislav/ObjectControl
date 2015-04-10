package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.FragmentHome;
import com.atlas.mars.objectcontrol.MyJQuery;
import com.atlas.mars.objectcontrol.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mars on 4/8/15.
 */
public class DialogSend extends MyDialog {
    FragmentHome fragmentHome;
    LinearLayout contentDialog;
    MyJQuery myJQuery;
    final static String TAG = "myLog";

    public DialogSend(Activity activity) {
        super(activity);
    }

    @Override
    public View onCreate() {
        viewDialog = inflater.inflate(R.layout.dialog_select_obj, null);
        pw = new PopupWindow(viewDialog, FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        pw.setOutsideTouchable(false);
        contentDialog = (LinearLayout)viewDialog.findViewById(R.id.contentDialog);
        initOkCancelSend();
        myJQuery = new MyJQuery();
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
            inflateContent();
            pw.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void onDismiss() {
        pw.dismiss();
    }


    public void initFragment(FragmentHome fragmentHome){
        this.fragmentHome = fragmentHome;
    }


    private void initOkCancelSend(){
        FrameLayout btnOk = (FrameLayout)viewDialog.findViewById(R.id.btn_ok);
        FrameLayout btnCancel = (FrameLayout)viewDialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });

    }
    private void inflateContent(){
        contentDialog.removeAllViews();
        for (HashMap<String, String> map : fragmentHome.favoriteCommand) {

            if(map.get(fragmentHome.SELECT_FOR_SEND)!=null &&  map.get(fragmentHome.SELECT_FOR_SEND).equals("1")){
                FrameLayout row = (FrameLayout)inflater.inflate(R.layout.row_send,null);
                ArrayList<View> arrayTextView = myJQuery.findViewByTagClass(row, TextView.class);
                TextView deviceText = (TextView)arrayTextView.get(0);
                deviceText.setText(map.get("valueDeviceName"));
                contentDialog.addView(row);
            }

           // Log.d(TAG, "inflateContent +++");
        }
    }
}
