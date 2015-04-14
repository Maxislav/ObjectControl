package com.atlas.mars.objectcontrol.dialogs;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.FragmentHome;
import com.atlas.mars.objectcontrol.MyJQuery;
import com.atlas.mars.objectcontrol.R;
import com.atlas.mars.objectcontrol.Sender;

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
    DisplayMetrics displayMetrics;
    ArrayList<HashMap> arraySelectForSend;
    private float dpHeight, dpWidth, density;

    public DialogSend(Activity activity) {

        super(activity);
    }

    @Override
    public View onCreate() {
        displayMetrics = activity.getResources().getDisplayMetrics();
        density = displayMetrics.density;
        dpHeight = displayMetrics.heightPixels / density;
        dpWidth = displayMetrics.widthPixels / density;
        Log.d(TAG, "Density: " + density + " Width dp: " + dpWidth + " Width Pixels: " + displayMetrics.widthPixels);


        viewDialog = inflater.inflate(R.layout.dialog_send, null);
        pw = new PopupWindow(viewDialog, FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        pw.setOutsideTouchable(false);
        pw.setAnimationStyle(R.style.Animation);
        contentDialog = (LinearLayout)viewDialog.findViewById(R.id.contentDialog);
        initOkCancelSend();
        myJQuery = new MyJQuery();
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
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Sender sender = new Sender(arraySelectForSend, fragmentHome.viewHashMap, fragmentHome, activity.getApplicationContext(), activity);
                sender.send();

              /*  Toast toast = Toast.makeText(activity, "Тут могла быть ваша реклама", Toast.LENGTH_LONG);
                toast.show();*/
                pw.dismiss();
            }
        });


    }
    private void inflateContent(){
        contentDialog.removeAllViews();
        arraySelectForSend = new ArrayList<>();
        for (HashMap<String, String> map : fragmentHome.favoriteCommand) {

            if(map.get(fragmentHome.SELECT_FOR_SEND)!=null &&  map.get(fragmentHome.SELECT_FOR_SEND).equals("1")){
                FrameLayout row = (FrameLayout)inflater.inflate(R.layout.row_send,null);
                ArrayList<View> arrayTextView = myJQuery.findViewByTagClass(row, TextView.class);
               // TextView deviceText = (TextView)arrayTextView.get(0);

                TextView commandText = (TextView)arrayTextView.get(0);
                TextView codeText = (TextView)arrayTextView.get(1);
                TextView deviceText = (TextView)arrayTextView.get(2);
                commandText.setText(map.get(db.VALUE_NAME));
                codeText.setText(map.get(db.VALUE_COMMAND));
                codeText.setText(map.get(db.VALUE_COMMAND));
                deviceText.setText(map.get("valueDeviceName"));
                contentDialog.addView(row);
                arraySelectForSend.add(map);
            }

           // Log.d(TAG, "inflateContent +++");
        }
        if(arraySelectForSend.isEmpty()){
            LinearLayout emtyRow = (LinearLayout)inflater.inflate(R.layout.empty_for_send,contentDialog);
        }
    }
}
