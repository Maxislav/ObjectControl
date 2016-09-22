package com.atlas.mars.objectcontrol;

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;


import java.util.HashMap;

/**
 * Created by mars on 6/24/15.
 */
//@ReportsCrashes( formUri = "http://www.bugsense.com/api/acra?api_key=863af310",  mode = ReportingInteractionMode.TOAST,
//@ReportsCrashes( formUri = "http://192.168.126.73:8000/AtlasRevolution/acra/rest.php?key=000",  mode = ReportingInteractionMode.TOAST,
@ReportsCrashes(
        formUri = "http://178.62.44.54/dev/acra/rest.php?key=863af310",  mode = ReportingInteractionMode.TOAST,
//@ReportsCrashes( formUri = "http://192.168.126.73:88?key=000",  mode = ReportingInteractionMode.TOAST,
        //forceCloseDialogAfterToast = false, // optional, default false
        resToastText = R.string.app_error
)
public class ObjectControl  extends Application {
    DataBaseHelper db;
    static HashMap hashMapSetting;
   /* @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA

        db = new DataBaseHelper(this);
        hashMapSetting = db.hashSetting;
        super.onCreate();
        ACRA.init(this);




    }*/

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        db = new DataBaseHelper(this);
        hashMapSetting = db.hashSetting;

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
