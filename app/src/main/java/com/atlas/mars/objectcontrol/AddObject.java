package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by mars on 4/1/15.
 */
public class AddObject extends ActionBarActivity {
    final private String TAG = "myLOg";
    public final static String NAME = "NAME";
    public final static String PHONE = "PHONE";
    int count;


    EditText edTextName;
    EditText edTextPhone;


    FrameLayout btn_ok, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object);
        setTitle(R.string.create_object);
        count = getIntent().getIntExtra("FROM", 0);
        Log.d(TAG, "FROM "+ count +"");
        edTextName = (EditText) findViewById(R.id.edTextName);
        edTextPhone = (EditText) findViewById(R.id.edTextPhone);
        _init();
    }
    @Override
    public boolean  onCreateOptionsMenu(Menu menu){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.actionbar_background, null));
        return true;
    }

    public void _init(){
        btn_ok = (FrameLayout)findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerInent = new Intent();
                String name = edTextName.getText().toString();
                String phone = edTextPhone.getText().toString();
                answerInent.putExtra(NAME, name);
                answerInent.putExtra(PHONE, phone);
                answerInent.putExtra(MainActivity.FROM, count);
                setResult(RESULT_OK, answerInent);
                finish();
            }
        });
        btn_cancel =  (FrameLayout)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
