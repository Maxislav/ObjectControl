package com.atlas.mars.objectcontrol;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mars on 3/30/15.
 */
public class MyJQuery {


    public  ArrayList<View> getViewsByTag(ViewGroup root, Class type){
        ArrayList<View> allViews = new ArrayList<View>();
        View V = root.findViewWithTag("LinearLayout");
        final int childCount = root.getChildCount();
        for(int i=0; i<childCount; i++){
            final View childView = root.getChildAt(i);

          /*  if(childView instanceof ViewGroup){
                allViews.addAll(getViewsByTag((ViewGroup)childView, tag));
            }*/
                if(childView.getClass().equals(type)){
                    allViews.add(childView);
                }

               /* final Object tagView = childView.getTag();

                childView.findViewWithTag("LinearLayout");
                if(tagView != null && tagView.equals(tag))
                    allViews.add(childView);*/

        }

        return allViews;
    }


}
