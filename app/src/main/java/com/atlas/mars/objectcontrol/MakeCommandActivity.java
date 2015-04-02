package com.atlas.mars.objectcontrol;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * Created by Администратор on 4/2/15.
 */
public class MakeCommandActivity extends ActionBarActivity {

    EditText etNameCommand, etCode;
    FrameLayout btnSelectDevice, btnOk, btnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_commands);
        btnCancel = (FrameLayout)findViewById(R.id.btn_cancel);
        btnOk = (FrameLayout)findViewById(R.id.btn_ok);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
