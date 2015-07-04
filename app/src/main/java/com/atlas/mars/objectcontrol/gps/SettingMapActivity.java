package com.atlas.mars.objectcontrol.gps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.atlas.mars.objectcontrol.DataBaseHelper;
import com.atlas.mars.objectcontrol.R;
import com.squareup.phrase.Phrase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mars on 5/5/15.
 */
public class SettingMapActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    String web;
    Spinner spinner;
    DataBaseHelper db;
    HashMap<String,String> mapSetting = DataBaseHelper.hashSetting;
    String LOGIN, PASS, URL;
    EditText serverUrl, edTextServerLogin, edTextServerPass;

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
        edTextServerLogin = (EditText)findViewById(R.id.edTextServerLogin);
        edTextServerPass = (EditText)findViewById(R.id.edTextServerPass);

       /* List<String> SpinnerArray = new ArrayList<String>();
        SpinnerArray.add("Item 1");
        SpinnerArray.add("Item 2");
        SpinnerArray.add("Item 3");
        SpinnerArray.add("Item 4");
        SpinnerArray.add("Item 5");*/



        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_servers, android.R.layout.simple_spinner_item);

      //  ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, SpinnerArray);
      //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
        db = new DataBaseHelper(this);
        LOGIN = mapSetting.get(db.MAP_LOGIN);
        PASS = mapSetting.get(db.MAP_PASS);
        URL = mapSetting.get(db.MAP_SERVER_URL);

        if(URL!=null){
            serverUrl.setText(URL);
        }
        if(LOGIN!=null){
            edTextServerLogin.setText(LOGIN);
        }
        if(PASS!=null){
            edTextServerPass.setText(PASS);
        }
        web = getString(R.string.web);


        onDraw();
    }
    private void onDraw(){
        parsePasteWeb((WebView)findViewById(R.id.server), getString(R.string.server_url) );
        parsePasteWeb((WebView)findViewById(R.id.wLogin), getString(R.string.server_login) );
        parsePasteWeb((WebView)findViewById(R.id.wPass), getString(R.string.server_pass) );
        parsePasteWeb((WebView)findViewById(R.id.protocol), getString(R.string.server_protocol) );

    }
    private void parsePasteWeb(WebView browser, String put ){
        CharSequence formatted = Phrase.from(web).put("content", put).format();
        browser.loadData(formatted.toString(), "text/html; charset=UTF-8", null);
    }
    private void saveSetting(){
        URL = serverUrl.getText().toString();
        LOGIN = edTextServerLogin.getText().toString();
        PASS = edTextServerPass.getText().toString();

        if(URL!=null && !URL.isEmpty()){
            mapSetting.put(db.MAP_SERVER_URL, URL);
        }
        if(LOGIN!=null && !LOGIN.isEmpty()){
            mapSetting.put(db.MAP_LOGIN, LOGIN);
        }
        if(PASS!=null && !PASS.isEmpty()){
            mapSetting.put(db.MAP_PASS, PASS);
        }
        db.setSetting(mapSetting);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            toastShow(position+"");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void toastShow(String str) {
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
    }
}
