package com.atlas.mars.objectcontrol;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Администратор on 4/12/15.
 */
public class Sender {
    Activity activity;
    ArrayList<HashMap> arraySelectForSend;
    HashMap<String, View> viewHashMap;
    MyJQuery myJQuery;
    DataBaseHelper db;
    FragmentHome fragmentHome;
    Context context;
    static Handler h;
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_NUMBER = "number";
    private static final String EXTRA_ID = "idCommand";
    Communicator communicator;
    SmsManager smsMgr;
    IntentFilter filter;


    final static private String TAG = "myLog";

    public Sender(ArrayList<HashMap> arraySelectForSend, HashMap<String, View> viewHashMap, final FragmentHome fragmentHome, Context context, Activity activity) {
        this.arraySelectForSend = arraySelectForSend;
        this.viewHashMap = viewHashMap;
        this.fragmentHome = fragmentHome;
        this.context = context;
        this.activity = activity;
        myJQuery = new MyJQuery();
        //communicator = (Communicator)getA

        //communicator.registerReceiver(receiver);
        smsMgr = SmsManager.getDefault();
       // filter = new IntentFilter(SENT);
       // filter.addAction(DELIVERED);
        activity.registerReceiver(receiver, new IntentFilter(SENT));
        activity.registerReceiver(receiverDeliver, new IntentFilter(DELIVERED));

        db = new DataBaseHelper(fragmentHome.mainActivity);
        h = new Handler() {
            public static final int ID_0 = 0;
            final FragmentHome _fragmentHome = fragmentHome;

            @Override
            public void handleMessage(android.os.Message msg) {
                Log.d(TAG, "+++" + msg.obj.toString());
                String id = msg.obj.toString();
                FrameLayout row = (FrameLayout) _fragmentHome.viewHashMap.get(id);
                HashMap<String, String> map = getMap(id);
                _fragmentHome.rowUnSelect(row, map);
                ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
                ImageView imageSms = (ImageView) arrayImgs.get(3);
                imageSms.clearAnimation();

            }
        };
    }


    public void send() {
        for (HashMap<String, String> map : arraySelectForSend) {
            final String id = map.get(db.UID);
            FrameLayout row = (FrameLayout) fragmentHome.viewHashMap.get(id);
            ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
            ImageView imageSms = (ImageView) arrayImgs.get(3);
            Animation animOut = AnimationUtils.loadAnimation(context, R.anim.sender);
            animOut.setRepeatCount(Animation.INFINITE);
            imageSms.startAnimation(animOut);
            String phone, command, name;

            phone= map.get(db.VALUE_PHONE);
            name = map.get(db.VALUE_NAME_DEVICE);
            command = map.get(db.VALUE_COMMAND);

            // imageSms.clearAnimation();
           // toastShort(name+" : " + command);

            //Todo отправка раскоментировать
            sendText(phone, name, command, id,0);
            toastShort("Sending to "+name+" : " + command);
            /*Thread t = new Thread(new Runnable() {

                public void run() {
                    try {
                        Thread.sleep(5200);
                        Message msg = Message.obtain(h, 0);
                        msg.obj = id;
                        h.sendMessage(msg);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                }
            });
            t.start();*/


        }
    }


    private HashMap<String, String> getMap(String id) {
        for (HashMap<String, String> map : arraySelectForSend) {
            if (id.equals(map.get(db.UID))) {
                return map;
            }
        }
        return null;
    }



    private void sendText(String conNumber, String conName, String mess, String idCommand, int requestCode)
    {
        Intent sentIntent = new Intent(SENT);
        Intent deliveredIntent = new Intent(DELIVERED);

        sentIntent.putExtra(EXTRA_NUMBER, conNumber);
        sentIntent.putExtra(EXTRA_NAME, conName);
        sentIntent.putExtra(EXTRA_ID, idCommand);

        PendingIntent sentPI = PendingIntent.getBroadcast(activity, requestCode, sentIntent, 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(activity, requestCode, deliveredIntent, 0);

        smsMgr.sendTextMessage(conNumber, null, mess, sentPI, deliveredPI);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SENT.equals(intent.getAction())) {
                String name = intent.getStringExtra("name");
                String number = intent.getStringExtra("number");
                String id =  intent.getStringExtra(EXTRA_ID);
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Команда отправлена
                        toastShort("SMS sent to " + name + " & " + number);
                        Message msg = Message.obtain(h, 0);
                        msg.obj = id;
                        h.sendMessage(msg);

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
            } else if (DELIVERED.equals(intent.getAction())) {
                switch (getResultCode()) {
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

    private BroadcastReceiver receiverDeliver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            switch (getResultCode())
            {
                case Activity.RESULT_OK:

                    toastShort( "Команда доставлена");
                    break;
                case Activity.RESULT_CANCELED:

                    toastShort( "Команда не доставлена");
                    break;
            }
        }
    };

    private void toastShort(String msg) {
           Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }


}
