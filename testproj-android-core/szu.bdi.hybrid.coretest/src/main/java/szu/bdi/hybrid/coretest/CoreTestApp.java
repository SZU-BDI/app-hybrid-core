package szu.bdi.hybrid.coretest;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import szu.bdi.hybrid.core.HybridTools;

/**
 * For core test, it only do basic test about WebView/JsBridge/Ui-Open/Ui-Close
 * For dynamic reflection based on "config.json" to link api and ui to class,
 * please refer to app-hybrid-demo for more detail.
 */

public class CoreTestApp extends Application {
    final private static String LOGTAG = "" + (new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName());

    @Override
    public void onCreate() {
        super.onCreate();
        final Context _ctx = getApplicationContext();
        HybridTools.setAppContext(_ctx);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            HybridTools.quickShowMsgMain("Warning: Your Phone is too old (API " + Build.VERSION.SDK_INT + ")... May have some problem...");
        }

        //NOTES: for main thread (could) using network, do a policy config hack:
        int _sdk_int = android.os.Build.VERSION.SDK_INT;
        if (_sdk_int > Build.VERSION_CODES.GINGERBREAD) {
            Log.d(LOGTAG, "setThreadPolicy for " + _sdk_int);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Log.v(LOGTAG, "Application.onCreate");

    }
}
