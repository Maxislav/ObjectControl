package com.atlas.mars.objectcontrol;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
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
public class Sender  {
    ArrayList<HashMap> arraySelectForSend;
    HashMap<String,View> viewHashMap;
    MyJQuery myJQuery;
    DataBaseHelper db;
    FragmentHome fragmentHome;
    Context context;
    static Handler h;
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    Communicator communicator;


    final static private String TAG = "myLog";

    public Sender(ArrayList<HashMap> arraySelectForSend, HashMap<String,View> viewHashMap, final FragmentHome fragmentHome , Context context){
        this.arraySelectForSend = arraySelectForSend;
        this.viewHashMap= viewHashMap;
        this.fragmentHome = fragmentHome;
        this.context = context;
        myJQuery = new MyJQuery();
        communicator.registerReceiver(receiver);

        db = new DataBaseHelper(fragmentHome.mainActivity);
        h = new Handler() {
            public static final int ID_0 = 0;
            final FragmentHome _fragmentHome = fragmentHome;
            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "+++" + msg.obj.toString());
                String id = msg.obj.toString();
                FrameLayout row = (FrameLayout)_fragmentHome.viewHashMap.get(id);
                HashMap<String,String> map = getMap(id);
                _fragmentHome.rowUnSelect(row, map);
                ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
                ImageView imageSms = (ImageView)arrayImgs.get(3);
                imageSms.clearAnimation();

            }
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


    private HashMap<String,String> getMap(String id){
        for(HashMap<String, String> map:  arraySelectForSend){
            if(id.equals(map.get(db.UID))){
                return map;
            }
        }
        return null;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (SENT.equals(intent.getAction()))
            {
                String name = intent.getStringExtra("name");
                String number = intent.getStringExtra("number");

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        toastShort("SMS sent to " + name + " & " + number);
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        toastShort("Generic failure");
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        toastShort("No service");
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        toastShort("Null PDU");
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        toastShort("Radio off");
                        break;
                }
            }
            else if (DELIVERED.equals(intent.getAction()))
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        toastShort("SMS delivered");
                        break;

                    case Activity.RESULT_CANCELED:
                        toastShort("SMS not delivered");
                        break;
                }
            }
        }
    };

    private void toastShort(String msg)
    {
     //   Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


}
