package com.atlas.mars.objectcontrol.gps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.R;
import com.squareup.phrase.Phrase;

import java.util.HashMap;

/**
 * Created by mars on 5/5/15.
 */
public class SettingMapActivity extends ActionBarActivity{
    String web;
    DataBaseHelper db;
    HashMap<String,String> mapSetting;
    String LOGIN, PASS, URL;
    EditText serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_map);
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
        serverUrl = (EditText)findViewById(R.id.serverUrl);
        mapSetting = new HashMap<>();
        db = new DataBaseHelper(this);
        db.getSetting(mapSetting);
        LOGIN = mapSetting.get(db.MAP_LOGIN);
        PASS = mapSetting.get(db.MAP_PASS);
        URL = mapSetting.get(db.MAP_SERVER_URL);

        if(URL!=null){
            serverUrl.setText(URL);
        }
        web = getString(R.string.web);
        onDraw();
    }
    private void onDraw(){
        parsePasteWeb((WebView)findViewById(R.id.server), getString(R.string.server_url) );

    }
    private void parsePasteWeb(WebView browser, String put ){
        CharSequence formatted = Phrase.from(web).put("content", put).format();
        browser.loadData(formatted.toString(), "text/html; charset=UTF-8", null);
    }
    private void saveSetting(){
        URL = serverUrl.getText().toString();
        mapSetting.put(db.MAP_SERVER_URL, URL);
        db.setSetting(mapSetting);
    }



}
