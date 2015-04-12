package com.atlas.mars.objectcontrol;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.dialogs.DialogEditObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Администратор on 4/12/15.
 */
public class ListObjectActivity extends ActionBarActivity {
    LayoutInflater inflater;
    DataBaseHelper db;
    public LinearLayout mainLayout;
    ArrayList<HashMap> arrayListDevices;
    HashMap<String, View> hashMapView;
    MyJQuery myJQuery;
    boolean showEdit = false;
    DialogEditObj dialogEditObj;
    View dialodView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_object);
        db = new DataBaseHelper(this);
        mainLayout = (LinearLayout)findViewById(R.id.mainLayout);
        myJQuery = new MyJQuery();

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        showEdit = false;
        onInit();
        onDraw();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_object, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit:
                //item.setTitle("END");
                if(showEdit){
                    showEdit(View.INVISIBLE);
                    item.setTitle(R.string.edit);
                    showEdit = false;
                }else{
                    showEdit(View.VISIBLE);
                    item.setTitle(R.string.end_edit);
                    showEdit = true;
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onInit(){
        arrayListDevices = db.getListDevices();

    }
    private void showEdit(int visible){
        for (Map.Entry entry : hashMapView.entrySet()) {
            ImageView imgEdit = (ImageView)myJQuery.findViewByTagClass((LinearLayout)entry.getValue(), ImageView.class).get(0);
            imgEdit.setVisibility(visible);
            //System.out.println("Key: " + entry.getKey() + " Value: "+ entry.getValue());
        }
    }

    private void onDraw(){
        hashMapView = new HashMap<>();
        for(HashMap<String,String> map : arrayListDevices){
            LinearLayout row = (LinearLayout)inflater.inflate(R.layout.row_list_object, null);
            ArrayList<View> arrayTextView = myJQuery.findViewByTagClass(row, TextView.class);
            ArrayList<View> arrayImageView = myJQuery.findViewByTagClass(row, ImageView.class);
            ((TextView)arrayTextView.get(1)).setText(map.get(db.VALUE_NAME));
            ((TextView)arrayTextView.get(3)).setText(map.get(db.VALUE_PHONE));
            hashMapView.put(map.get(db.UID), row);
            mainLayout.addView(row);
            setOnClickListenerEdit((ImageView) arrayImageView.get(0), map, row);
        }
    }

    private void setOnClickListenerEdit(final ImageView imageView, final HashMap<String, String> map, final LinearLayout row){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShow(v,map, row);

              //  if(dialodView==null) dialodView = dialogEditObj.onCreate();
              //  dialogEditObj.dialogInflate(map, row);
              //  dialogEditObj.vHide(v);
            }
        });
    }
    private void dialogShow(View v, HashMap<String,String>map, LinearLayout row){
        if(dialogEditObj!=null){
            dialogEditObj.onDismiss();
            dialogEditObj = null;
        }
         dialogEditObj = new DialogEditObj(this);
         dialodView = dialogEditObj.onCreate();
         dialogEditObj.dialogInflate(map, row, this);
         dialogEditObj.vHide(v);
    }

    public void updateRow(View row, HashMap<String, String> map){
        ArrayList<View> arrayTextView = myJQuery.findViewByTagClass((LinearLayout)row, TextView.class);
        ((TextView)arrayTextView.get(1)).setText(map.get(db.VALUE_NAME));
        ((TextView)arrayTextView.get(3)).setText(map.get(db.VALUE_PHONE));

    }

}
