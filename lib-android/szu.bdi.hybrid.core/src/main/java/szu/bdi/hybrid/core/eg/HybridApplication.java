package szu.bdi.hybrid.core.eg;

import android.app.Application;
import android.util.Log;

import szu.bdi.hybrid.core.HybridTools;

public class HybridApplication extends Application {
    final private static String LOGTAG = "" + (new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName());

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOGTAG, "HybridApplication.onCreate");
        HybridTools.setAppContext(getApplicationContext());
    }
}
