package com.atlas.mars.objectcontrol;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class Dialog1 extends  DialogFragment implements OnClickListener {


    Activity activity;

    /*public static void setActivity(Activity activity){
        Dialog1.activity = activity;
    }
*/

    final String LOG_TAG = "myLogs";

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"Theme" + getTheme()+""); //0
       setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Title!");
        View v = inflater.inflate(R.layout.dialog1, null);

        LinearLayout ll = (LinearLayout) v.findViewById(R.id.ll);

        LayoutInflater inflatert = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.dialog_select_obj, null);
        LinearLayout rowLinearLayout = (LinearLayout) row;
        ll.addView(rowLinearLayout);
       /* LayoutInflater inflatert = getLayoutInflater();

        LinearLayout rowLinearLayout = (LinearLayout) row;
        v.addView(rowLinearLayout);*/

        v.findViewById(R.id.btnYes).setOnClickListener(this);
        v.findViewById(R.id.btnNo).setOnClickListener(this);
        v.findViewById(R.id.btnMaybe).setOnClickListener(this);

        return v;
    }

    public void onClick(View v) {
        Log.d(LOG_TAG, "Dialog 1: " + ((Button) v).getText());
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 1: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 1: onCancel");
    }

}