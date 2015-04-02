package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by mars on 4/1/15.
 */
public class AddObject extends ActionBarActivity {
    public final static String NAME = "NAME";
    public final static String PHONE = "PHONE";


    EditText edTextName;
    EditText edTextPhone;


    FrameLayout btn_ok, btn_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object);

        edTextName = (EditText) findViewById(R.id.edTextName);
        edTextPhone = (EditText) findViewById(R.id.edTextPhone);
        _init();
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
