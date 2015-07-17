package com.atlas.mars.objectcontrol.gps;

import android.view.View;
import android.widget.LinearLayout;

import com.atlas.mars.objectcontrol.R;

import java.util.HashMap;

/**
 * Created by Администратор on 7/17/15.
 */
public class ScrollOnTouchListener extends ListContainerEvents {
    ScrollOnTouchListener(View row, LinearLayout listContainerf, MapsActivity activity, HashMap<String, String> map ) {
        super(row, listContainerf, activity, map);
    }

    @Override
    public void onInit() {
        listContainerf.findViewById(R.id.scrollView).setOnTouchListener(this);
//        super.onInit();
    }
}
