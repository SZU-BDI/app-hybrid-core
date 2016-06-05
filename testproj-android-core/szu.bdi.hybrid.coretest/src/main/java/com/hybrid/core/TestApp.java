package com.hybrid.core;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class TestApp extends Application {
    final private static String LOGTAG = "" + (new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName());

    @Override
    public void onCreate() {
        super.onCreate();

        AppHelper.context = getApplicationContext();

        Log.v(LOGTAG, "Application.onCreate");

    }
}
