package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by Администратор on 4/2/15.
 */
public class MakeCommandActivity extends ActionBarActivity {

    public final static String NAME_COMMAND = "NAME_COMMAND";
    public final static String CODE = "CODE";
    EditText etNameCommand, etCode;
    FrameLayout btnSelectDevice, btnOk, btnCancel;
    MyDialog myDialog;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_commands);
        count = getIntent().getIntExtra(MainActivity.FROM, 0);

        btnCancel = (FrameLayout)findViewById(R.id.btn_cancel);
        btnOk = (FrameLayout)findViewById(R.id.btn_ok);
        etNameCommand = (EditText)findViewById(R.id.edTextNameCommand);
        etCode = (EditText)findViewById(R.id.etCode);
        btnSelectDevice = (FrameLayout)findViewById(R.id.btn_select);
        myDialog = new MyDialog(this);

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
                myDialog.dialogMekeCommand(v);
            }
        });

    }
}
