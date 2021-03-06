package com.atlas.mars.objectcontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;

//import com.squareup.phrase.Phrase;

import com.atlas.mars.objectcontrol.phrase.Phrase;

import java.util.HashMap;

/**
 * Created by Администратор on 4/11/15.
 */
public class ActivitySetting extends ActionBarActivity {
    CharSequence formatted;
    private static final String TAG ="myLog";
    //final static String TAG = "myLog";
    static HashMap<String,String> mapSetting;
    String web;
    DataBaseHelper db;
    CheckBox multipleSend, confirmSend, startOnMap;
    EditText commandInMemory, commandInStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    @Override
    public boolean  onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.actionbar_background, null));
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                Intent answerIntent = new Intent();
                saveSetting();
                setResult(RESULT_OK, answerIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void init(){
        multipleSend = (CheckBox)findViewById(R.id.multipleSend);
        confirmSend = (CheckBox)findViewById(R.id.confirmSend);
        startOnMap = (CheckBox)findViewById(R.id.startOnMap);

        //webStartOnMap
        commandInMemory = (EditText)findViewById(R.id.commandInMemory);
        commandInStart = (EditText)findViewById(R.id.commandInStart);
        if(mapSetting== null) mapSetting = new HashMap<>();
        if(db==null){
            db = new DataBaseHelper(this);
            mapSetting = db.hashSetting;
        }

        web = getString(R.string.web);
        onDraw();

        if(mapSetting.get(db.MULTIPLE_SEND).equals("1")){
            multipleSend.setChecked(true);
        }

        if(mapSetting.get(db.CONFIRM_SEND).equals("1")){
            confirmSend.setChecked(true);
        }

        if(mapSetting.get(db.START_ON_MAP_ACTIVITY)!=null && mapSetting.get(db.START_ON_MAP_ACTIVITY).equals("1")){
            startOnMap.setChecked(true);
        }


        commandInMemory.setText(mapSetting.get(db.COUNT_MEMORY_HISTORY));
        commandInStart.setText(mapSetting.get(db.COUNT_DISPLAY_HISTORY));

        eventListener();


    }


    private void onDraw(){

        parsePasteWeb((WebView)findViewById(R.id.webStartOnMap), getString(R.string.start_on_map_help) );
        parsePasteWeb((WebView)findViewById(R.id.webMultipleSend), getString(R.string.multiple_send_help) );
        parsePasteWeb((WebView)findViewById(R.id.webConfirmSend), getString(R.string.confirm_send_help) );
        parsePasteWeb((WebView)findViewById(R.id.countHistory), getString(R.string.count_command_history) );
        parsePasteWeb((WebView)findViewById(R.id.countHistoryInStart), getString(R.string.count_command_history_in_start) );
    }

    private void parsePasteWeb(WebView browser,String put ){
        CharSequence formatted = Phrase.from(web).put("content", put).format();
        browser.loadData(formatted.toString(), "text/html; charset=UTF-8", null);
    }

    private void eventListener(){
        commandInMemory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG,"+++ "+ hasFocus);
                if(hasFocus) return;
                String changesValue = commandInMemory.getText().toString();
                if(!changesValue.equals(mapSetting.get(db.COUNT_MEMORY_HISTORY))){
                    String result = changesValue.replaceAll("[^0-9]", "");
                    changesValue = result;
                    Log.d(TAG,"+++ "+ changesValue);
                    if(changesValue.isEmpty()){
                        commandInMemory.setText("0");
                    }
                }
            }
        });
        commandInStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) return;
                String changesValue = commandInStart.getText().toString();
                String result = changesValue.replaceAll("[^0-9]", "");
                changesValue = result;
                Log.d(TAG,"+++ "+ changesValue);
                if(changesValue.isEmpty()){
                    commandInStart.setText("0");
                }
            }
        });
    }
    private void saveSetting(){
        HashMap <String,String> changesSettingMap = db.hashSetting;
        /*fillSetting(CONFIRM_SEND, "1",db);
        fillSetting(MULTIPLE_SEND, "0", db);
        fillSetting(COUNT_MEMORY_HISTORY, "100", db);
        fillSetting(COUNT_DISPLAY_HISTORY, "10", db);*/
        if(multipleSend.isChecked()){
            changesSettingMap.put(db.MULTIPLE_SEND,"1");
        }else{
            changesSettingMap.put(db.MULTIPLE_SEND,"0");
        }

        if(confirmSend.isChecked()){
            changesSettingMap.put(db.CONFIRM_SEND,"1");
        }else{
            changesSettingMap.put(db.CONFIRM_SEND,"0");
        }

        if(startOnMap.isChecked()){
            mapSetting.put(db.START_ON_MAP_ACTIVITY, "1");
        }else{
            mapSetting.put(db.START_ON_MAP_ACTIVITY, "0");
        }


        String inMemory = commandInMemory.getText().toString();
        if(inMemory.isEmpty()){
            inMemory ="0";
        }
        int intInMemory = Integer.parseInt(inMemory);
        if(intInMemory<0){
            intInMemory = 0;
        }
        inMemory = Integer.toString(intInMemory);
        changesSettingMap.put(db.COUNT_MEMORY_HISTORY, inMemory);

        String inDisplay = commandInStart.getText().toString();

        if(inDisplay.isEmpty()){
            inDisplay ="0";
        }
        int intInDisplay  = Integer.parseInt(inDisplay);
        if(intInDisplay<0){
            intInDisplay = 0;
        }
        inDisplay = Integer.toString(intInDisplay);

        changesSettingMap.put(db.COUNT_DISPLAY_HISTORY,inDisplay );

        db.setSetting(changesSettingMap);
    }
}
