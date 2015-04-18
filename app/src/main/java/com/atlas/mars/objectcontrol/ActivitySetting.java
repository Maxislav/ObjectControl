package com.atlas.mars.objectcontrol;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

import com.squareup.phrase.Phrase;

/**
 * Created by Администратор on 4/11/15.
 */
public class ActivitySetting extends ActionBarActivity {
    CharSequence formatted;
    final static String TAG = "myLog";
    String web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        web = getString(R.string.web);
        init();
    }
    private void init(){

        parsePasteWeb((WebView)findViewById(R.id.webMultipleSend), getString(R.string.multiple_send_help) );
        parsePasteWeb((WebView)findViewById(R.id.confirmSend), getString(R.string.confirm_send_help) );
       parsePasteWeb((WebView)findViewById(R.id.countHistory), getString(R.string.count_command_history) );
       parsePasteWeb((WebView)findViewById(R.id.countHistoryInStart), getString(R.string.count_command_history_in_start) );
    }
    private void parsePasteWeb(WebView browser,String put ){
        CharSequence formatted = Phrase.from(web).put("content", put).format();
        browser.loadData(formatted.toString(), "text/html; charset=UTF-8", null);
    }
}
