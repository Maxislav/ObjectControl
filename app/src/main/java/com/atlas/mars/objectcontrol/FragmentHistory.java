package com.atlas.mars.objectcontrol;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by mars on 4/14/15.
 */
public class FragmentHistory extends MyFragmentView {
    FrameLayout btnFrom;
    FrameLayout btnTo;
    TextView textBtnFrom, textBtnTo;
    LinearLayout mainLayout;

    final static String MONTH = "M";
    final static String YEAR = "y";
    final static String DAY_OF_MONTH = "d";
    final static String TAG = "myLog";
    static Resources resources;


    ArrayList<HashMap> arrayList;
    public HashMap<String, View> hashViews; // id в истории / View - row

    Calendar calFrom, calTo;

    FragmentHistory(MainActivity mainActivity, View viewFragment, LayoutInflater inflater) {
        super(mainActivity, viewFragment, inflater);
    }

    @Override
    public void onInit() {
        resources = mainActivity.getResources();
        mainLayout = (LinearLayout)viewFragment.findViewById(R.id.mainLayout);
        btnFrom =(FrameLayout) viewFragment.findViewById(R.id.btnFrom);
        textBtnFrom =(TextView) myJQuery.findViewByTagClass(btnFrom, TextView.class).get(0);
        btnTo =(FrameLayout) viewFragment.findViewById(R.id.btnTo);
        textBtnTo =(TextView) myJQuery.findViewByTagClass(btnTo, TextView.class).get(0);
        Calendar c = Calendar.getInstance();



        calFrom = Calendar.getInstance();
        calFrom.set(Calendar.YEAR, c.get(Calendar.YEAR));
        calFrom.set(Calendar.MONTH, c.get(Calendar.MONTH));
        calFrom.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)-1);
        calFrom.set(Calendar.HOUR, 0);
        calFrom.set(Calendar.MINUTE, 0);
        calFrom.set(Calendar.SECOND, 0);
        setTextFrom();


        calTo = Calendar.getInstance();
        calTo.set(Calendar.YEAR, c.get(Calendar.YEAR));
        calTo.set(Calendar.MONTH, c.get(Calendar.MONTH));
        calTo.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        calTo.set(Calendar.HOUR,0);
        calTo.set(Calendar.MINUTE,0);
        calTo.set(Calendar.SECOND,0);
        setTextBtnTo();



        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(mainActivity, new listenerFrom(), calFrom.get(Calendar.YEAR), calFrom.get(Calendar.MONTH), calFrom.get(Calendar.DAY_OF_MONTH));
                dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, resources.getString(R.string.btn_ok), dialog);
                dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, resources.getString(R.string.btn_cancel), new CancelListener());
                dialog.show();
            }
        });
        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog dialog = new DatePickerDialog(mainActivity, new listenerTo(), calTo.get(Calendar.YEAR), calTo.get(Calendar.MONTH), calTo.get(Calendar.DAY_OF_MONTH));
                dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, resources.getString(R.string.btn_ok), dialog);
                dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, resources.getString(R.string.btn_cancel), new CancelListener());
                dialog.show();
            }
        });


        getListData();
        onDraw();
    }


    private void getListData(){
        arrayList = db.getHistoryCommand(mainActivity.mapSetting.get(db.COUNT_DISPLAY_HISTORY));
    }

    private void getListDataFromTo(){
        arrayList = db.getHistoryCommand(calFrom, calTo);
    }

    private void onDraw(){
        hashViews = new HashMap<>();
        for(HashMap<String, String> map : arrayList){
            FrameLayout row =(FrameLayout)inflater.inflate(R.layout.row_command_history, null);
            ArrayList<View> arrayTextView = myJQuery.findViewByTagClass(row, TextView.class);
            String sDate = map.get(db.VALUE_DATE);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = null;
            try {
                 date = format.parse(sDate);
            } catch (ParseException e) {
                Log.e(TAG,e.toString());
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String toDate = dateFormat.format(date);

           // Log.d(TAG, String.valueOf(date));
            ((TextView)arrayTextView.get(0)).setText(toDate);
            ((TextView)arrayTextView.get(1)).setText(map.get(db.VALUE_NAME));
            ((TextView)arrayTextView.get(2)).setText(map.get(db.VALUE_COMMAND));
            ((TextView)arrayTextView.get(3)).setText(map.get(db.VALUE_NAME_DEVICE));

            if(map.get(db.VALUE_DELIVERED).equals("1")){
                ((TextView)arrayTextView.get(0)).setTextColor(mainActivity.getResources().getColor(R.color.colorDelivered));
            }

            mainLayout.addView(row);
            hashViews.put(map.get(db.UID), row);
        }
    }

    private void onRedrawFromTo(){
        mainLayout.removeAllViews();
        getListDataFromTo();
        onDraw();
    }

    public void onRedraw(){
        mainLayout.removeAllViews();
        getListData();
        onDraw();
    }
    private  class CancelListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                Log.d(TAG, "Негатив");
            }
        }
    }


    private class listenerFrom implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {

            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
          //  Log.d(TAG, mDay+"."+mMonth+"."+mYear);

            calFrom.set(Calendar.YEAR, year);
            calFrom.set(Calendar.MONTH,mMonth);
            calFrom.set(Calendar.DAY_OF_MONTH,mDay);
            calFrom.set(Calendar.HOUR,0);
            calFrom.set(Calendar.MINUTE,0);
            calFrom.set(Calendar.SECOND,0);
            setTextFrom();
            onRedrawFromTo();
        }
    }

    private class listenerTo implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {

            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            //Log.d(TAG, mDay+"."+mMonth+"."+mYear);

            calTo.set(Calendar.YEAR, year);
            calTo.set(Calendar.MONTH,mMonth);
            calTo.set(Calendar.DAY_OF_MONTH,mDay);
            calTo.set(Calendar.HOUR,0);
            calTo.set(Calendar.MINUTE,0);
            calTo.set(Calendar.SECOND,0);
            setTextBtnTo();
            onRedrawFromTo();
        }
    }

    private void setTextFrom( ){
        Date dateFrom = calFrom.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        formatter.setTimeZone(TimeZone.getDefault());
        String s = formatter.format(dateFrom);
        textBtnFrom.setText(s);
    }
    private void setTextBtnTo( ){
        Date dateFrom = calTo.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        formatter.setTimeZone(TimeZone.getDefault());
        String s = formatter.format(dateFrom);
        textBtnTo.setText(s);
    }

    public  void setDelivered(String id){
        FrameLayout row = (FrameLayout)hashViews.get(id);
        TextView dateText  =(TextView) myJQuery.findViewByTagClass(row, TextView.class).get(0);
        dateText.setTextColor(mainActivity.getResources().getColor(R.color.colorDelivered));
    }

    @Override
    public void regenParams() {

    }
}
