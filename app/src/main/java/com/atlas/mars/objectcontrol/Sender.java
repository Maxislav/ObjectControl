package com.atlas.mars.objectcontrol;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Администратор on 4/12/15.
 */
public class Sender {
    ArrayList<HashMap> arraySelectForSend;
    HashMap<String,View> viewHashMap;
    MyJQuery myJQuery;
    DataBaseHelper db;
    FragmentHome fragmentHome;
    Context context;
    static Handler h;
    final static private String TAG = "myLog";
    public Sender(ArrayList<HashMap> arraySelectForSend, HashMap<String,View> viewHashMap, final FragmentHome fragmentHome , Context context){
        this.arraySelectForSend = arraySelectForSend;
        this.viewHashMap= viewHashMap;
        this.fragmentHome = fragmentHome;
        this.context = context;
        myJQuery = new MyJQuery();
        db = new DataBaseHelper(fragmentHome.mainActivity);
        h = new Handler() {
            public static final int ID_0 = 0;
            final FragmentHome _fragmentHome = fragmentHome;
            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "+++" + msg.obj.toString());
                String id = msg.obj.toString();
                FrameLayout row = (FrameLayout)_fragmentHome.viewHashMap.get(id);
                ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
                ImageView imageSms = (ImageView)arrayImgs.get(3);
                imageSms.clearAnimation();
                // обновляем TextView
                // tvInfo.setText("Закачано файлов: " + msg.what);
                // if (msg.what == 10) btnStart.setEnabled(true);
            };
        };
    }


    public void send(){
        for( HashMap<String, String> map:  arraySelectForSend){
            final String id = map.get(db.UID);
            FrameLayout row = (FrameLayout)fragmentHome.viewHashMap.get(id);
            ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
            ImageView imageSms = (ImageView)arrayImgs.get(3);
            Animation animOut = AnimationUtils.loadAnimation(context, R.anim.sender);
            animOut.setRepeatCount(Animation.INFINITE);
            imageSms.startAnimation(animOut);

           // imageSms.clearAnimation();
            Thread t = new Thread(new Runnable() {

                public void run() {
                    try{
                        Thread.sleep(5200);
                        Message msg = Message.obtain(h, 0);
                        msg.obj = id;
                        h.sendMessage(msg);
                    }catch (Exception e){
                        Log.e(TAG, e.toString());
                    }

                }
            });
            t.start();


        }
    }
}
