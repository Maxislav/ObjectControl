package com.atlas.mars.objectcontrol.gps;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.atlas.mars.objectcontrol.R;

import java.util.zip.Inflater;

/**
 * Created by Администратор on 7/4/15.
 */
public  class LoaderBar {
    static Activity activity;
    static LayoutInflater inflater;
    static FrameLayout linearLayoutCOntext;
    static LinearLayout progress;
    static float density;
    private static int count=0;
    static FrameLayout globalLayout;
    public LoaderBar(Activity activity){
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        globalLayout = (FrameLayout) activity.findViewById(R.id.globalLayout);
    }

    private static void onCreate(){

        View v = inflater.inflate(R.layout.progressbar, null, false);
        linearLayoutCOntext = (FrameLayout)v;
        density = activity.getResources().getDisplayMetrics().density;
        progress = (LinearLayout)linearLayoutCOntext.findViewById(R.id.progress);
        v.setVisibility(View.INVISIBLE);
        globalLayout.addView(v);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , (int)(3*density));
        layoutParams.gravity = Gravity.TOP;
        linearLayoutCOntext.setLayoutParams(layoutParams);
    }

    static public void show(){
        if(linearLayoutCOntext==null){
            onCreate();
        }
        linearLayoutCOntext.setVisibility(View.VISIBLE);
        count++;
        final Animation animIn = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.sender);
        animIn.setRepeatCount(Animation.INFINITE);
        progress.startAnimation(animIn);

    }
    static public void hide(){
        if(0<count){
            count--;
        }
        if(count==0){
            progress.clearAnimation();
            globalLayout.removeView(linearLayoutCOntext);
            linearLayoutCOntext = null;
        }
    }
}
