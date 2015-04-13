package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.atlas.mars.objectcontrol.dialogs.MakeCommandDialog00;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Администратор on 4/2/15.
 */
public class MakeCommandActivity extends ActionBarActivity implements MakeCommandDialog00.GiftCheckbox {

    public final static String TAG = "myLog";
    public final static String NAME_COMMAND = "NAME_COMMAND";


    public final static String CODE = "CODE";
    EditText etNameCommand, etCode;
    TextView tvSelectObject;
    FrameLayout btnSelectDevice, btnOk, btnCancel;
    MakeCommandDialog00 myDialog;

    HashMap<String, String> mapCommand;
    ArrayList<View> arrayView;
    int count;
    DataBaseHelper db;

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
        tvSelectObject = (TextView)findViewById(R.id.tvSelectObject);


        myDialog = new MakeCommandDialog00(this);

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
                clickBtnOkClose();
               // finish();
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
                if (checkBox.isChecked()) {
                    mapCommand.put(map.get(DataBaseHelper.UID), map.get(DataBaseHelper.VALUE_NAME) );
                } else {
                    mapCommand.remove(map.get(DataBaseHelper.UID));
                }
                Log.d(TAG, "ID = " + map.get(DataBaseHelper.UID) + " " + checkBox.isChecked());
                setTvSelectObject();
            }
        });
    }

    private void setTvSelectObject(){
        String text = "";
        for (Map.Entry entry : mapCommand.entrySet()) {
           text+=entry.getValue()+" | ";
            System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
        }

        text = text.replaceAll("\\|\\s$","");
        tvSelectObject.setText(text);
        if(text.isEmpty()){
            tvSelectObject.setText("NONE");
        }

    }

    //нажатие на диалог OK
    public void clickBtnOk() {

        for (Map.Entry entry : mapCommand.entrySet()) {
           //Log.d(TAG, "+++ ID: " + entry.getKey());
           System.out.println("Key: " + entry.getKey() + " Value: "+ entry.getValue());
        }
    }
    //нажатие на диалог CANCEL
    public void clickBtnCancel() {
        for (View view : arrayView) {
            ((CheckBox) view).setChecked(false);
        }
    }


    private  void clickBtnOkClose(){
        db = new DataBaseHelper(this);

        String nameCommand = etNameCommand.getText().toString();
        String code = etCode.getText().toString();

        ArrayList<HashMap> arrayList = new ArrayList<>();

        for (Map.Entry entry : mapCommand.entrySet()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("name", nameCommand);
            map.put("idDev", entry.getKey().toString());
            arrayList.add(map);

            //System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
        }

        db.addCommand(arrayList);
        finish();
    }
}
