package com.atlas.mars.objectcontrol;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mars on 3/30/15.
 */
public class MyJQuery {

    ArrayList<View> allViews;

    MyJQuery() {
        allViews = new ArrayList<View>();
    }
    public ArrayList<View> getViewsByTagWithReset(ViewGroup root, Class type){
        allViews = new ArrayList<View>();

        return  getViewsByTag(root, type);

    };

    public ArrayList<View> getViewsByTag(ViewGroup root, Class type) {

        View V = root.findViewWithTag("LinearLayout");
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = root.getChildAt(i);

            if (childView instanceof ViewGroup && 0 < ((ViewGroup) childView).getChildCount()) {
                getViewsByTag((ViewGroup) childView, type);
            }

            if (childView.getClass().equals(type)) {
                allViews.add(childView);
            }


        }

        return allViews;
    }


}
