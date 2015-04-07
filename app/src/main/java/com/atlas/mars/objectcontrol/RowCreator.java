package com.atlas.mars.objectcontrol;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mars on 4/2/15.
 */
public class RowCreator {
    View view;
    LayoutInflater inflater;
    MyJQuery myJQuery;
    LinearLayout mainLayout;
    static int count = 0;
    final static String TAG = "myLog";


    RowCreator(View view, LayoutInflater inflater) {
        this.view = view;
        this.inflater = inflater;
        myJQuery = new MyJQuery();
        mainLayout = (LinearLayout)view.findViewById(R.id.mainLayout);
    }

    public FrameLayout create(HashMap<String,String> map) {
        FrameLayout row = (FrameLayout) inflater.inflate(R.layout.row_command, null);
        ArrayList<View> arrayList = myJQuery.getViewsByTagWithReset(row, TextView.class);
        TextView tvCmd = (TextView)arrayList.get(0);
        tvCmd.setText(map.get(DataBaseHelper.VALUE_NAME));

        TextView tvCode = (TextView)arrayList.get(1);
        tvCode.setText(map.get(DataBaseHelper.VALUE_COMMAND));

        TextView tvNameDevice = (TextView)arrayList.get(2);
        tvNameDevice.setText(map.get(DataBaseHelper.VALUE_NAME_DEVICE));

        mainLayout.addView(row);

        count++;
        return row;
    }

}
