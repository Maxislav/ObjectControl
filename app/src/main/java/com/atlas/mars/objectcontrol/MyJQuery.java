package com.atlas.mars.objectcontrol;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mars on 3/30/15.
 */
public class MyJQuery {

    ArrayList<View> allViews;

    public MyJQuery() {
        allViews = new ArrayList<View>();
    }
    public ArrayList<View> findViewByTagClass(ViewGroup root, String type){
        allViews = new ArrayList<View>();

        return  getViewsByTag(root, type);

    };
    public ArrayList<View> findViewByTagClass(ViewGroup root, Class type){
        allViews = new ArrayList<View>();

        return  getViewsByTag(root, type);

    };

    public ArrayList<View> getViewsByTag(ViewGroup root, String type) {

        View V = root.findViewWithTag("LinearLayout");
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = root.getChildAt(i);

            if (childView instanceof ViewGroup && 0 < ((ViewGroup) childView).getChildCount()) {
                getViewsByTag((ViewGroup) childView, type);
            }
           // Button btn;
           // childView.getClass().getName().equalsIgnoreCase("android.widget.Button");
           // if (childView.getClass().equals(type)) {
            if (childView.getClass().getName().equalsIgnoreCase(type)) {
                allViews.add(childView);
            }


        }

        return allViews;
    }

    public ArrayList<View> getViewsByTag(ViewGroup root, Class type) {

        View V = root.findViewWithTag("LinearLayout");
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = root.getChildAt(i);

            if (childView instanceof ViewGroup && 0 < ((ViewGroup) childView).getChildCount()) {
                getViewsByTag((ViewGroup) childView, type);
            }
            // Button btn;
            // childView.getClass().getName().equalsIgnoreCase("android.widget.Button");
             if (childView.getClass().equals(type)) {
            //if (childView.getClass().getName().equalsIgnoreCase(type)) {
                allViews.add(childView);
            }


        }

        return allViews;
    }

}
