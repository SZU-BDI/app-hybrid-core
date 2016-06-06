package com.hybrid.coretest;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.util.Log;

public class TestApp extends Application {
//    final private static String LOGTAG = "" + (new Object() {
//        public String getClassName() {
//            String clazzName = this.getClass().getName();
//            return clazzName.substring(0, clazzName.lastIndexOf('$'));
//        }
//    }.getClassName());

    @Override
    public void onCreate() {
        super.onCreate();

        AppHelper.appContext = getApplicationContext();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            AppHelper.showMsg(getApplicationContext(),
                    "Your Phone is too old...May have some problem");
        }
        //Log.v(LOGTAG, "Application.onCreate");
    }
}
