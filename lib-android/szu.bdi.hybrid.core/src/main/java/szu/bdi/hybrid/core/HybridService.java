package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.webkit.WebSettings;

import com.github.lzyzsd.jsbridge.BridgeWebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HybridService {
    final private static String LOGTAG = "HybridService";

    Context _ctx;

    public HybridService(Context ctx) {
        this._ctx = ctx;
    }

    public HybridUi getHybridUi(String addr) {
        final Context _f_ctx = _ctx;
//        Intent intent = new Intent(_ctx, HybridUi.class);
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//
//        _ctx.startActivity(intent);

//        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
                Intent intent = new Intent(_f_ctx, HybridUi.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                _f_ctx.startActivity(intent);
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
        return null;
    }

    public static HybridApi getHybridApi(String uiName) {
        //HybridUi ui=new HybridUi();
        return null;
    }

    public static BridgeWebView BuildOldJsBridge(Context _ctx) {
        BridgeWebView wv;
        wv = new BridgeWebView(_ctx);
        return wv;
    }

    @SuppressLint("JavascriptInterface")
    public static BridgeWebView BuildWebViewWithJsBridgeSupport(Context _ctx) {
        //TODO rewrite or bugfix the WebView later.
        BridgeWebView wv = new BridgeWebView(_ctx);

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

    public static String assetFile2Str(Context c, String urlStr) {
        InputStream in = null;
        try {
            in = c.getAssets().open(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();
            in.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
                in = null;
            }
        }
        return null;
    }

}

