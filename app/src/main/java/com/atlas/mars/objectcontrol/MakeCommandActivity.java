package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.atlas.mars.objectcontrol.dialogs.MakeCommandDialog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Администратор on 4/2/15.
 */
public class MakeCommandActivity extends ActionBarActivity implements MakeCommandDialog.GiftCheckbox {

    public final static String TAG = "myLog";
    public final static String NAME_COMMAND = "NAME_COMMAND";


    public final static String CODE = "CODE";
    EditText etNameCommand, etCode;
    FrameLayout btnSelectDevice, btnOk, btnCancel;
    MakeCommandDialog myDialog;

    HashMap<String,String> mapCommand;
  ArrayList<View> arrayView;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_commands);
        setTitle(R.string.create_command);
        count = getIntent().getIntExtra(MainActivity.FROM, 0);
        mapCommand = new HashMap<>();
        arrayView = new ArrayList<>();


        btnCancel = (FrameLayout) findViewById(R.id.btn_cancel);
        btnOk = (FrameLayout) findViewById(R.id.btn_ok);
        etNameCommand = (EditText) findViewById(R.id.edTextNameCommand);
        etCode = (EditText) findViewById(R.id.etCode);
        btnSelectDevice = (FrameLayout) findViewById(R.id.btn_select);
        myDialog = new MakeCommandDialog(this);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerInent = new Intent();
                String nameCommand = etNameCommand.getText().toString();
                String code = etCode.getText().toString();
                answerInent.putExtra(MainActivity.FROM, count);
                answerInent.putExtra(NAME_COMMAND, nameCommand);
                answerInent.putExtra(CODE, code);
                setResult(RESULT_OK, answerInent);
                finish();
            }
        });
        btnSelectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dialogMakeCommand(v);
            }
        });

    }

    @Override
    public void setCheckbox(View v, HashMap<String, String> _map) {
        arrayView.add(v);
        Log.d(TAG, "setCheckbox+++ ");
        final CheckBox checkBox = (CheckBox) v;
        final HashMap<String, String> map = _map;
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String id = map.get(DataBaseHelper.UID);
                if(checkBox.isChecked()){
                    mapCommand.put(map.get(DataBaseHelper.UID), "1");
                }else{
                    mapCommand.remove(map.get(DataBaseHelper.UID));
                }
                Log.d(TAG, "ID = " + map.get(DataBaseHelper.UID) + " " + checkBox.isChecked());

            }
        });
    }

    public void clickBtnOk(){

    }
    public void clickBtnCacel(){
        for(View view: arrayView){
            ((CheckBox)view).setChecked(false);
        }
    }
}
