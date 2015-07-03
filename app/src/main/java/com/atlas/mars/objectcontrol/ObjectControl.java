package com.atlas.mars.objectcontrol;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by mars on 6/24/15.
 */
@ReportsCrashes( formUri = "http://www.bugsense.com/api/acra?api_key=863af310",  mode = ReportingInteractionMode.TOAST,
        forceCloseDialogAfterToast = false, // optional, default false
        resToastText = R.string.app_error)
public class ObjectControl  extends Application {
    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA


        super.onCreate();
        ACRA.init(this);
    }
}
