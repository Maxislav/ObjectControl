package com.atlas.mars.objectcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.atlas.mars.objectcontrol.gps.MapsActivity;

import java.util.HashMap;

import retrofit2.Retrofit;

/**
 * Created by mars on 11/4/15.
 */
public class StartActivity extends Activity {
    public final String FROM_ACTIVITY = "FROM_ACTIVITY";
    public final String ACTIVITY_NAME = "StartActivity";

    DataBaseHelper db;
    static HashMap<String,String> mapSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DataBaseHelper(this);
        mapSetting = db.hashSetting;
        Intent intent;
        if(mapSetting.get(db.START_ON_MAP_ACTIVITY)!=null && mapSetting.get(db.START_ON_MAP_ACTIVITY).equals("1")){


            //Retrofit.Builder client = new Retrofit.Builder();




            intent = new Intent(this, MapsActivity.class);
            intent.setFlags(1);

        }else{
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
