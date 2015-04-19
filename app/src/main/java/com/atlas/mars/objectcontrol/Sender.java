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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

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
    static Handler handler;
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_NUMBER = "number";
    private static final String EXTRA_ID = "idCommand";
    private static final String EXTRA_ID_HISTORY = "idHistory";
    SmsManager smsMgr;
    final static private String TAG = "myLog";

    public Sender(ArrayList<HashMap> arraySelectForSend, HashMap<String, View> viewHashMap, final FragmentHome fragmentHome, Context context, final Activity activity) {
        this.arraySelectForSend = arraySelectForSend;
        this.viewHashMap = viewHashMap;
        this.fragmentHome = fragmentHome;
        this.context = context;
        this.activity = activity;
        myJQuery = new MyJQuery();
        smsMgr = SmsManager.getDefault();
        activity.registerReceiver(receiver, new IntentFilter(SENT));
        activity.registerReceiver(receiverDeliver, new IntentFilter(DELIVERED));

        db = new DataBaseHelper(fragmentHome.mainActivity);
        handler = new MyHandler(fragmentHome);
        /*handler = new Handler() {
            public static final int ID_0 = 0;
            public static final int ID_1 = 1;
            final FragmentHome _fragmentHome = fragmentHome;
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what){
                    case 0:
                        Log.d(TAG, "+++" + msg.obj.toString());
                        String id = msg.obj.toString();
                        FrameLayout row = (FrameLayout) _fragmentHome.viewHashMap.get(id);
                        HashMap<String, String> map = getMap(id);
                        _fragmentHome.rowUnSelect(row, map);
                        ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
                        ImageView imageSms = (ImageView) arrayImgs.get(3);
                        imageSms.clearAnimation();
                        ((MainActivity)activity).regenHistory();
                        break;
                    case 1:
                        toastShort(msg.obj.toString());
                        break;
                    case 2:
                        ((MainActivity)activity).markSendOnHistory(msg.obj.toString());
                        break;
                }


            }
        };*/



        //toastMess = new MyHandler(fragmentHome);
    }

    private  class MyHandler extends Handler{
        FragmentHome _fragmentHome;

        public MyHandler(FragmentHome _fragmentHome){
            super();
            this._fragmentHome = _fragmentHome;
        }

        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 0:
                    Log.d(TAG, "+++" + msg.obj.toString());
                    String id = msg.obj.toString();
                    FrameLayout row = (FrameLayout) _fragmentHome.viewHashMap.get(id);
                    HashMap<String, String> map = getMap(id);
                    _fragmentHome.rowUnSelect(row, map);
                    ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
                    ImageView imageSms = (ImageView) arrayImgs.get(3);
                    imageSms.clearAnimation();
                    ((MainActivity)activity).regenHistory();
                    break;
                case 1:
                    toastShort(msg.obj.toString());
                    break;
                case 2:
                    ((MainActivity)activity).markSendOnHistory(msg.obj.toString());
                    break;
            }
        }
    }


    public void send() {
        for (HashMap<String, String> map : arraySelectForSend) {
            final String id = map.get(db.UID);
            FrameLayout row = (FrameLayout) fragmentHome.viewHashMap.get(id);
            ArrayList<View> arrayImgs = myJQuery.findViewByTagClass(row, ImageView.class);
            ImageView imageSms = (ImageView) arrayImgs.get(3);
            Animation animOut = AnimationUtils.loadAnimation(context, R.anim.sender);
            animOut.setRepeatCount(Animation.INFINITE);

            String phone, command, name;

            phone= map.get(db.VALUE_PHONE);
            name = map.get(db.VALUE_NAME_DEVICE);
            command = map.get(db.VALUE_COMMAND);
            if(phone.equals("0000")){
                toastShort(activity.getResources().getString(R.string.edit_default_value));
                return;
            }
            imageSms.startAnimation(animOut);

            sendText(phone, name, command, id,0);
            toastShort("Sending to "+name+" : " + command);
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

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String s = formatter.format(now);
        Log.d(TAG, "+++"+s);
        HashMap<String,String> map = new HashMap<>();
        map.put(db.VALUE_DATE, s);
        map.put(db.VALUE_ID_COMMAND, idCommand);
        long longIdHistory = db.insertHistory(map);
        String idHistory = Long.toString(longIdHistory);


        Intent sentIntent = new Intent(SENT);
        Intent deliveredIntent = new Intent(DELIVERED);

        sentIntent.putExtra(EXTRA_NUMBER, conNumber);
        sentIntent.putExtra(EXTRA_NAME, conName);
        sentIntent.putExtra(EXTRA_ID, idCommand);

        deliveredIntent.putExtra(EXTRA_NUMBER, conNumber);
        deliveredIntent.putExtra(EXTRA_NAME, conName);
        deliveredIntent.putExtra(EXTRA_ID, idCommand);
        deliveredIntent.putExtra(EXTRA_ID_HISTORY, idHistory);

        PendingIntent sentPI = PendingIntent.getBroadcast(activity, requestCode, sentIntent, 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(activity, requestCode, deliveredIntent, 0);

        //Todo раскоментировать prod
        //smsMgr.sendTextMessage(conNumber, null, mess, sentPI, deliveredPI);

        //Todo закоментировать разработка затычка
        capSend(conNumber, mess, conName, idCommand, idHistory);

    }





     private void capSend(String number, final String mess, final String name, final String id, final String idHistory){


         Thread send = new Thread(new Runnable() {
             public void run() {
                 try {
                     Thread.sleep(2000);
                     Message msg = Message.obtain(handler, 0);
                     msg.obj = id;
                     handler.sendMessage(msg);
                 } catch (Exception e) {
                     Log.e(TAG, e.toString());
                 }

             }
         });
         send.start();
         Thread deliver = new Thread(new Runnable() {
             public void run() {
                 try {
                     Thread.sleep(5000);

                     String mess = name+ " Команда доставлена id: "+id;
                     Message msg = Message.obtain(handler, 1);
                     msg.obj = mess;
                     handler.sendMessage(msg);

                     Message msg2 = Message.obtain(handler, 2);
                     db.updateToDelivered(idHistory);
                     msg2.obj = idHistory;
                     handler.sendMessage(msg2);

                 } catch (Exception e) {
                     Log.e(TAG, e.toString());
                 }

             }
         });
         deliver.start();

     }






    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            String number = intent.getStringExtra("number");
            String id =  intent.getStringExtra(EXTRA_ID);
            if (SENT.equals(intent.getAction())) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //Команда отправлена
                        toastShort("SMS sent to " + name + " : " + number);
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
            Message msg = Message.obtain(handler, 0);
            msg.obj = id;
            handler.sendMessage(msg);


        }
    };

    private BroadcastReceiver receiverDeliver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            String name = intent.getStringExtra("name");
            String number = intent.getStringExtra("number");
            String id =  intent.getStringExtra(EXTRA_ID);
            String  idHistory =  intent.getStringExtra(EXTRA_ID_HISTORY);
            switch (getResultCode())
            {
                case Activity.RESULT_OK:
                    db.updateToDelivered(idHistory);
                    Message msg2 = Message.obtain(handler, 2);
                    msg2.obj = idHistory;
                    handler.sendMessage(msg2);
                    db.updateToDelivered(idHistory);
                    toastShort( name+ " Команда доставлена id: "+id);
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
