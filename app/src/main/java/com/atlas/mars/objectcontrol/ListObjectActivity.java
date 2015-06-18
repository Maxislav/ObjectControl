package com.atlas.mars.objectcontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atlas.mars.objectcontrol.dialogs.DialogEditObj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Администратор on 4/12/15.
 */
public class ListObjectActivity extends ActionBarActivity {
    LayoutInflater inflater;
    final private String TAG = "myLog";
    DataBaseHelper db;
    public LinearLayout mainLayout;
    ArrayList<HashMap> arrayListDevices;
    HashMap<String, View> hashMapView;
    MyJQuery myJQuery;
    boolean showEdit = false;
    boolean flagShowMinus = false;
    DialogEditObj dialogEditObj;
    View dialodView;
    Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_object);
        db = new DataBaseHelper(this);
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        myJQuery = new MyJQuery();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        showEdit = false;
        flagShowMinus = false;
        onInit();
        onDraw();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_object, menu);
        this.menu = menu;
        // getMenuInflater().inflate(R.menu.menu_setting, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.actionbar_background, null));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                showDelMinus(View.INVISIBLE);
                menu.findItem(R.id.action_del).setTitle(R.string.del_object);
                flagShowMinus = false;
                if (showEdit) {
                    showEdit(View.INVISIBLE);
                    item.setTitle(R.string.edit);
                    showEdit = false;
                } else {
                    showEdit(View.VISIBLE);
                    item.setTitle(R.string.end_edit);
                    showEdit = true;
                }
                return true;
            case R.id.action_del:
                showEdit(View.INVISIBLE);
                menu.findItem(R.id.action_edit).setTitle(R.string.edit);
                showEdit = false;
                if (flagShowMinus) {
                    showDelMinus(View.INVISIBLE);
                    item.setTitle(R.string.del_object);
                    flagShowMinus = false;
                } else {
                    item.setTitle(R.string.end_del);
                    showDelMinus(View.VISIBLE);
                    flagShowMinus = true;
                }
                return true;
            case R.id.action_add_object:
                showEdit(View.INVISIBLE);
                showDelMinus(View.INVISIBLE);
                menu.findItem(R.id.action_edit).setTitle(R.string.edit);
                menu.findItem(R.id.action_del).setTitle(R.string.del_object);
                Intent questionIntent = new Intent( this, AddObject.class);
                startActivityForResult(questionIntent, 0);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

                String name = data.getStringExtra(AddObject.NAME);
                String phone = data.getStringExtra(AddObject.PHONE);
                if (phone.isEmpty() || name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Пустое значение поля",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //todo Раскоментировать
                long n = db.addNewDevice(name, phone);
                Toast.makeText(getApplicationContext(), "ID : " + n + "", Toast.LENGTH_SHORT).show();
                onRegen();
            }
        }
    }

    private void onInit() {
        showEdit = false;
        flagShowMinus = false;
        arrayListDevices = db.getListDevices();
    }
    private void onRegen(){
        mainLayout.removeAllViews();
        onInit();
        onDraw();
    }

    private void showEdit(int visible) {
        for (Map.Entry entry : hashMapView.entrySet()) {
            ImageView imgEdit = (ImageView) myJQuery.findViewByTagClass((LinearLayout) entry.getValue(), ImageView.class).get(0);
            imgEdit.setVisibility(visible);
            //System.out.println("Key: " + entry.getKey() + " Value: "+ entry.getValue());
        }
    }

    private void showDelMinus(int visible) {
        for (Map.Entry entry : hashMapView.entrySet()) {
            ImageView imgEdit = (ImageView) myJQuery.findViewByTagClass((LinearLayout) entry.getValue(), ImageView.class).get(1);
            imgEdit.setVisibility(visible);

            if(visible == View.INVISIBLE){
                ArrayList<View> butnDelView = myJQuery.findViewByTagClass((LinearLayout) entry.getValue(), Button.class);
                Button btnDel = (Button) butnDelView.get(0);
                btnDel.setVisibility(visible);
                imgEdit.setBackgroundResource(R.drawable.btn_minus);
            }
        }
    }

    private void onDraw() {
        hashMapView = new HashMap<>();
        for (HashMap<String, String> map : arrayListDevices) {
            LinearLayout row = (LinearLayout) inflater.inflate(R.layout.row_list_object, null);
            ArrayList<View> arrayTextView = myJQuery.findViewByTagClass(row, TextView.class);
            ArrayList<View> arrayImageView = myJQuery.findViewByTagClass(row, ImageView.class);
            ArrayList<View> butnDelView = myJQuery.findViewByTagClass(row, Button.class);
            Button btnDel = (Button) butnDelView.get(0);
            ((TextView) arrayTextView.get(1)).setText(map.get(db.VALUE_NAME));
            ((TextView) arrayTextView.get(3)).setText(map.get(db.VALUE_PHONE));
            hashMapView.put(map.get(db.UID), row);
            mainLayout.addView(row);
            setOnClickListenerEdit((ImageView) arrayImageView.get(0), map, row);
            setOnClickListenerMinus((ImageView) arrayImageView.get(1), btnDel, map, row);
            setOnClickListenerBtnDel(btnDel, row, map);
        }
    }

    private void setOnClickListenerEdit(final ImageView imageView, final HashMap<String, String> map, final LinearLayout row) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShow(v, map, row);
            }
        });
    }

    private void setOnClickListenerMinus(final ImageView imageView, final Button btnDel, final HashMap<String, String> map, final LinearLayout row) {
        final Animation animIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_left);
        final Animation animOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_left);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnDel.isShown()) {
                    btnDel.setVisibility(View.VISIBLE);
                    btnDel.startAnimation(animIn);
                    imageView.setBackgroundResource(R.drawable.btn_minus_open);
                } else {
                    btnDel.startAnimation(animOut);
                    imageView.setBackgroundResource(R.drawable.btn_minus);

                    animOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            btnDel.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

            }
        });
    }

    private void setOnClickListenerBtnDel(final Button btn, final LinearLayout row, final HashMap<String, String> map) {
        final Animation animOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_right);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearRow(map, row);
                row.startAnimation(animOut);
                animOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        new Handler().post(new Runnable() {
                            public void run() {
                                clearRow(map, row);
                            }
                        });
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });
    }

    private void clearRow(HashMap<String, String> map, LinearLayout row) {
        String id = map.get(db.UID);
        db.delObject(id);
        for (int i = 0; i < arrayListDevices.size(); i++) {
            if (arrayListDevices.get(i) == map) {
                arrayListDevices.remove(i);
            }
        }
        hashMapView.remove(id);
        ((LinearLayout) row.getParent()).removeView(row);
        Log.d(TAG, "+++");
    }


    private void dialogShow(View v, HashMap<String, String> map, LinearLayout row) {
        if (dialogEditObj != null) {
            dialogEditObj.onDismiss();
            dialogEditObj = null;
        }
        dialogEditObj = new DialogEditObj(this);
        dialodView = dialogEditObj.onCreate();
        dialogEditObj.dialogInflate(map, row, this);
        dialogEditObj.vHide(v);
    }

    public void updateRow(View row, HashMap<String, String> map) {
        ArrayList<View> arrayTextView = myJQuery.findViewByTagClass((LinearLayout) row, TextView.class);
        ((TextView) arrayTextView.get(1)).setText(map.get(db.VALUE_NAME));
        ((TextView) arrayTextView.get(3)).setText(map.get(db.VALUE_PHONE));
    }

}
