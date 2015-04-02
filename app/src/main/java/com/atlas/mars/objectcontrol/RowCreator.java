package com.atlas.mars.objectcontrol;

import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

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

    public FrameLayout create() {
        FrameLayout row = (FrameLayout) inflater.inflate(R.layout.row_command, null);
        ViewGroup vgRow = (ViewGroup) row;
        ArrayList<View> arrayList = new  ArrayList<>();
        arrayList = myJQuery.getViewsByTagWithReset(vgRow, SurfaceView.class);
      //  SurfaceView surface = (SurfaceView) arrayList.get(0);
       // surfaseCreate(surface);

        TextView tvCmd = (TextView)myJQuery.getViewsByTagWithReset(row, TextView.class).get(0);
        tvCmd.setText("Команда: "+ (count+1)+"");

        mainLayout.addView(row);

        count++;
        return row;
    }

}
