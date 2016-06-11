package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.lzyzsd.jsbridge.BridgeWebView;

import org.json.JSONException;
import org.json.JSONObject;

public class HybridService {
    final private static String LOGTAG = "HybridService";

    //Singleton
    private static HybridService ourInstance = new HybridService();

    public static HybridService getInstance() {
        return ourInstance;
    }
//    private HybridService() {
//    }

//    Context _ctx;

//    public HybridService(Context ctx) {
//        this._ctx = ctx;
//    }

    public static HybridUi getHybridUi(String uiName) {

        HybridUi ui = new HybridUi();
        return ui;
    }

    public static HybridApi getHybridApi(String uiName) {
        //HybridUiActivity ui=new HybridUiActivity();
        return null;
    }

    public static BridgeWebView BuildOldJsBridge(Context _ctx) {
        BridgeWebView wv;
        wv = new BridgeWebView(_ctx);
        return wv;
    }

    @SuppressLint("JavascriptInterface")
    public static WebView BuildWebViewWithJsBridgeSupport(Context ctx) {
        final Context _ctx = ctx;

        final BridgeWebView wv = new BridgeWebView(_ctx);

        WebSettings mWebSettings = wv.getSettings();

        if (Build.VERSION.SDK_INT >= 11) {
            mWebSettings.setDisplayZoomControls(false);
        }

        wv.setVerticalScrollBarEnabled(false);
        wv.setHorizontalScrollBarEnabled(false);

        mWebSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            wv.setWebContentsDebuggingEnabled(true);
        }
//        String jsContent = assetFile2Str(wv.getContext(), "JsBridge.js");
//        if (jsContent != null) wv.loadUrl("javascript:" + jsContent);

//        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
//            this.loadUrl(javascriptCommand);
//        }

        //TODO
        //thread handling is not yet ok... wait...
//        wv.addJavascriptInterface(new JavascriptInterface() {
//            @JavascriptInterface
//            public String _send(String cmd_s, String id_s, String param_s) {
//                Log.v(LOGTAG, "cmd_s=" + cmd_s + ",param_s=" + param_s);
//                return "testreturnfromsend";
//            }
//        }, "AndroidWebView");

        return wv;
    }

    public static JSONObject jsoConfig = new JSONObject();

    public static void setJsonConfig(String K, Object V) {
        try {
            jsoConfig.put(K, V);
        } catch (JSONException e) {
            Log.d(LOGTAG, "setConfig " + K);
            e.printStackTrace();
        }
    }

}

//Find instance in the Config, get the config

//according to config, to set the

//return ourInstance.
//        final Context _f_ctx = _ctx;
//        Intent intent = new Intent(_ctx, HybridUiActivity.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//
//        _ctx.startActivity(intent);

//        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//        Intent intent = new Intent(_f_ctx, HybridUiActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        _f_ctx.startActivity(intent);
//            }
//        });
//        //        mWebView.registerHandler("_app_activity_close", new BridgeHandler() {
//
//        @Override
//        public void handler(String data, CallBackFunction function) {
//            Log.i(TAG, "handler = _app_activity_close");
//            finish();
//        }
//
//    });
//
//    mWebView.registerHandler("_app_activity_set_title", new BridgeHandler() {
//
//        @Override
//        public void handler(String title, CallBackFunction function) {
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(title);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }