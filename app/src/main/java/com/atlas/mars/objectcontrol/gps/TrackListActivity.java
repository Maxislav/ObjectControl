package com.atlas.mars.objectcontrol.gps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.MyJQuery;
import com.atlas.mars.objectcontrol.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Администратор on 6/30/15.
 */
public class TrackListActivity extends ActionBarActivity implements View.OnClickListener{
    LinearLayout linearLayoutScroll;
    DataBaseHelper db;
    List<View> listRows;
    LayoutInflater inflater;
    MyJQuery jQuery;
    String selectId;
    final public static String TAG = "myLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_track);
        linearLayoutScroll = (LinearLayout)findViewById(R.id.linearLayoutScroll);
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new DataBaseHelper(this);
        jQuery = new MyJQuery();
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);


        List<HashMap<String, String>> list = db.getTraksNameRows();
        listRows = new ArrayList<>();
        for(HashMap<String, String> map : list){
            View rowView =    inflater.inflate(R.layout.track_row, null);
            linearLayoutScroll.addView(rowView);
            ArrayList<View> fields = jQuery.findViewByTagClass((ViewGroup)rowView, TextView.class);
            TextView textViewId =  (TextView)fields.get(0);
            TextView textViewName =  (TextView)fields.get(1);
            TextView textViewDate=  (TextView)fields.get(2);
            textViewId.setText(map.get("id")+".");
            textViewName.setText(map.get("name"));
            textViewDate.setText(map.get("date"));
            listRows.add(rowView);
            rowSelect(rowView, map.get("id"));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                Intent answerIntent = new Intent();
                answerIntent.putExtra("selectId", selectId);
                setResult(RESULT_OK, answerIntent);
                finish();
               break;
            case  R.id.btn_cancel:
                selectId = null;
                finish();
                break;

        }

    }

    private void rowSelect(final View row, final String _id){
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectId = _id;
                for(View _row : listRows){
                    if(_row == row){
                        _row.setBackgroundResource(R.color.activeRouteType);
                    }else{
                        _row.setBackground(null);
                    }
                }
            }
        });
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TrackListActivity.this, v);
                popupMenu.inflate(R.menu.del_track);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.del:
                                Log.d(TAG, "Delete track " + _id);
                                if(db.delTrack(_id)){
                                    linearLayoutScroll.removeView(row);
                                }
                                return true;
                        }

                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });

    }
}
