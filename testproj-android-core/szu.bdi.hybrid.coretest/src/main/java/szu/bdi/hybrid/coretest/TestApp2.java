package szu.bdi.hybrid.coretest;

import android.app.Application;
import android.content.Context;
import android.util.Log;

//import com.hybrid.core.UiRoot;

public class TestApp2 extends Application {
    //    final private static String LOGTAG = "" + (new Object() {
//        public String getClassName() {
//            String clazzName = this.getClass().getName();
//            return clazzName.substring(0, clazzName.lastIndexOf('$'));
//        }
//    }.getClassName());
    final private static String LOGTAG = "TestApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Context _ctx = //AppHelper.appContext =
         getApplicationContext();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            AppHelper.showMsg(getApplicationContext(),
//                    "Your Phone is too old...May have some problem");
//        }
//        Intent intent = new Intent(_ctx, UiRoot.class);
//
////        intent.putExtra(Extra_openwebsite_key, text);
////        intent.setData(url);
//        _ctx.startActivity(intent);

//        Intent bg = new Intent(_ctx, UiRoot.class);
//        this.startService(bg);
        //TODO have to run a backgroup service to check network from time to time...

        Log.v(LOGTAG, "Application.onCreate");
    }
}
