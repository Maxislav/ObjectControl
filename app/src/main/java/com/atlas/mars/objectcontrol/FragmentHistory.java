package com.atlas.mars.objectcontrol;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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


    ArrayList<HashMap> arrayList;
    Calendar calFrom, calTo;

    FragmentHistory(MainActivity mainActivity, View viewFragment, LayoutInflater inflater) {
        super(mainActivity, viewFragment, inflater);
    }

    @Override
    public void onInit() {
        mainLayout = (LinearLayout)viewFragment.findViewById(R.id.mainLayout);
        btnFrom =(FrameLayout) viewFragment.findViewById(R.id.btnFrom);
        textBtnFrom =(TextView) myJQuery.findViewByTagClass(btnFrom, TextView.class).get(0);
        btnTo =(FrameLayout) viewFragment.findViewById(R.id.btnTo);
        textBtnTo =(TextView) myJQuery.findViewByTagClass(btnTo, TextView.class).get(0);
        Calendar c = Calendar.getInstance();



        calFrom = Calendar.getInstance();
        calFrom.set(Calendar.YEAR, c.get(Calendar.YEAR));
        calFrom.set(Calendar.MONTH, c.get(Calendar.MONTH) - 1);
        calFrom.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        setTextFrom();


        calTo = Calendar.getInstance();
        calTo.set(Calendar.YEAR, c.get(Calendar.YEAR));
        calTo.set(Calendar.MONTH, c.get(Calendar.MONTH));
        calTo.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
        setTextBtnTo();



        btnFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(mainActivity, new listenerFrom(), calFrom.get(Calendar.YEAR), calFrom.get(Calendar.MONTH), calFrom.get(Calendar.DAY_OF_MONTH));
                dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", new CancelListener());
                dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Done", dialog);
                dialog.show();
            }
        });
        btnTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(mainActivity, new listenerTo(), calTo.get(Calendar.YEAR), calTo.get(Calendar.MONTH), calTo.get(Calendar.DAY_OF_MONTH));
                dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", new CancelListener());
                dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Done", dialog);
                dialog.show();
            }
        });


        getListData();
        onDraw();
    }


    private void getListData(){
        arrayList = db.getHistoryCommand();

    }

    private void onDraw(){
        for(HashMap<String, String> map : arrayList){
          LinearLayout row =(LinearLayout)inflater.inflate(R.layout.row_command, mainLayout);

            ArrayList<View> arrayTextView = myJQuery.findViewByTagClass(row, TextView.class);

            ((TextView)arrayTextView.get(0)).setText(map.get(db.VALUE_NAME));
            ((TextView)arrayTextView.get(1)).setText(map.get(db.VALUE_COMMAND));
            ((TextView)arrayTextView.get(2)).setText(map.get(db.VALUE_NAME_DEVICE));
            ((TextView)arrayTextView.get(3)).setText(map.get(db.VALUE_DATE));
            ((TextView)arrayTextView.get(3)).setVisibility(View.VISIBLE);

        }
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
            Log.d(TAG, mDay+"."+mMonth+"."+mYear);

            calFrom.set(Calendar.YEAR, year);
            calFrom.set(Calendar.MONTH,mMonth);
            calFrom.set(Calendar.DAY_OF_MONTH,mDay);
            setTextFrom();
        }
    }

    private class listenerTo implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {

            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            Log.d(TAG, mDay+"."+mMonth+"."+mYear);

            calTo.set(Calendar.YEAR, year);
            calTo.set(Calendar.MONTH,mMonth);
            calTo.set(Calendar.DAY_OF_MONTH,mDay);
            setTextBtnTo();
        }
    }

    private void setTextFrom( ){
        Date dateFrom = calFrom.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getDefault());
        String s = formatter.format(dateFrom);
        textBtnFrom.setText(s);
    }
    private void setTextBtnTo( ){
        Date dateFrom = calTo.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getDefault());
        String s = formatter.format(dateFrom);
        textBtnTo.setText(s);
    }
    @Override
    public void regenParams() {

    }
}
