package szu.bdi.hybrid.coretest;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

//import com.hybrid.core.UiRoot;

public class TestApp2 extends Application {
    final private static String LOGTAG = "" + (new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName());

    @Override
    public void onCreate() {
        super.onCreate();
        final Context _ctx = //AppHelper.appContext =
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
        //NOTES: for main thread (could) using network, can do a policy config hack..
        int _sdk_int = android.os.Build.VERSION.SDK_INT;
        if (_sdk_int > 9) {
            Log.d(LOGTAG, "setThreadPolicy for " + _sdk_int);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Log.v(LOGTAG, "Application.onCreate");

//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                Intent intent = new Intent(_ctx, HybridUi.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
////                HybridService hs = new HybridService(_ctx);
////                HybridUi ui = hs.getHybridUi("file://android_asset/root.htm");
////                HybridService.getHybridUi(_ctx, "file://android_asset/root.htm");
//            }
//        }, 10);

//        Context _ctx = getApplicationContext();

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
////                Intent intent = new Intent(_ctx, HybridUi.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(intent);
//                HybridService hs = new HybridService(_ctx);
//                HybridUi ui = hs.getHybridUi("file://android_asset/root.htm");
////                ui.show();TODO
////                HybridService.getHybridUi(_ctx, "file://android_asset/root.htm");
//            }
//        }, 1);
//
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                Intent intent = new Intent(_ctx, UiContent.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
////                HybridService hs = new HybridService(_ctx);
////                HybridUi ui = hs.getHybridUi("file://android_asset/root.htm");
////                ui.show();TODO
////                HybridService.getHybridUi(_ctx, "file://android_asset/root.htm");
//            }
//        }, 5000);

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
////                Intent intent = new Intent(_ctx, HybridUi.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(intent);
//                HybridService.getHybridUi(_ctx, "file://android_asset/root.htm");
//            }
//        }, 8000);

//        intent.putExtra("Topbar", Topbar);
//        intent.putExtra("Mode", Mode);
//        intent.putExtra("Address", Address);

        //TODO start the background network status checker...
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                int pid = android.os.Process.myPid();
//                Log.d(LOGTAG, "kill and quit pid=" + pid);
//
//                android.os.Process.killProcess(pid);
//                System.exit(0);
//            }
//        }, (playMedia) ? 5000 : 3000);

    }
}
