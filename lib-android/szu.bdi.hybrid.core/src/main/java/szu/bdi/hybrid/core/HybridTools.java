package szu.bdi.hybrid.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import szu.bdi.hybrid.core.jsbridge.BridgeWebView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HybridTools {
    final private static String LOGTAG = "HybridTools";

    private static Context _appContext = null;

    //IMPORTANT !!!: remember in the app entry, set HybridTools.setAppContext(getApplicationContext());
    public static void setAppContext(Context appContext) {
        _appContext = appContext;
    }

    public static Context getAppContext() {
//        if (_appContext == null) {
//            throw new Exception("_appContext is null");
//        }
        return _appContext;
    }

    public static boolean flagAppWorking = true;//NOTES: backgroundService might use it.

    public static void quickShowMsgMain(String msg) {
        quickShowMsg(getAppContext(), msg);
    }

    public static void quickShowMsg(Context mContext, String msg) {
        //@ref http://blog.csdn.net/droid_zhlu/article/details/7685084
        //A toast is a view containing a quick little message for the user.  The toast class helps you create and show those.
        try {
            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String webPost(String url, String post_s) {
        String return_s = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            //it's said that in future is:
            // HttpClient httpClient = HttpClientBuilder.create().build();
//            CloseableHttpClient httpClient;
//            httpClient = HttpClientBuilder.create()
//                    .setDefaultConnectionConfig(config)
//                    .build();

            //NOTES:  to improve this timeout better maybe...
            // Connect Timeout
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 12000);
            // Socket Timeout
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

            HttpPost httpPost = new HttpPost(url);
            Log.v(LOGTAG, "To Post : " + url + "\n" + post_s);

            StringEntity se = new StringEntity(post_s);
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            return_s = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
        } catch (Throwable ex) {
            ex.printStackTrace();
            return_s = ex.getMessage();
            if (isEmptyString(return_s)) {
                return_s = "" + ex.getClass().getName();
            }
        }
        return return_s;
    }

    public static JSONObject s2o(String s) {
        if (s == null) return null;//return new JSONObject();
        try {
            return new JSONObject(s);
        } catch (Exception ex) {
        }
//        JSONArray rto = new JSONArray();
//        rto.put(s);
//        return rto.optJSONObject(0);
        JSONObject rt = new JSONObject();
        try {
            rt.put("STS", "KO");
            rt.put("errmsg", "wrong json");
            rt.put("s", "" + s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rt;
    }

    public static String o2s(JSONObject o) {
        return o.toString();
    }
//    //TODO....
//    public static String o2s(Object o) {
//        return o.toString();
//    }

    //Wrap the raw webPost for our cmp api call
    public static JSONObject apiPost(String url, JSONObject jo) {
        String return_s = null;
        try {
            String post_s = jo.toString();
            return_s = webPost(url, post_s);
        } catch (Exception ex) {
            return_s = ex.getMessage();
            if (isEmptyString(return_s)) {
                return_s = "" + ex.getClass().getName();
            }
            ex.printStackTrace();
        }
        try {
            if (return_s != null && return_s != "")
                return new JSONObject(return_s);
        } catch (Exception ex) {
        }
        JSONObject rt = new JSONObject();
        try {
            rt.put("STS", "KO");
            rt.put("errmsg", "" + return_s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rt;
    }

    public static void quit(boolean playMedia) {
        flagAppWorking = false;//wlll affect the bg service

        quickShowMsgMain("Quiting...");
        Log.d(LOGTAG, "quit " + playMedia);

//        if (playMedia) {
//            try {
//                mp.reset();
//                Uri uu = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
////                    Log.d(LOGTAG, "Uri=" + uu.toString());
//                mp.setDataSource(_appContext, uu);
//                mp.setOnPreparedListener(preparedListener);
//                //mp.prepareAsync();
//                mp.prepare();
//            } catch (IllegalStateException | IOException e) {
//                playMedia = false;
//                e.printStackTrace();
//            }
//        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                KillAppSelf();
            }
        }, (playMedia) ? 5000 : 2500);
    }

    //NOTES: need to run in main thread...
    public static void KillAppSelf() {
        int pid = android.os.Process.myPid();
        Log.d(LOGTAG, "kill and quit pid=" + pid);

        android.os.Process.killProcess(pid);
        System.exit(0);
    }

    public static String isoDateTime() {
        String time_s = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        return time_s;
    }

    //copy from jsbridge, maybe improve or find more elegant version...
    public static String getFileIntoStr(Context c, String urlStr) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
                //in = null;
            }
        }
        return null;
    }

    public static String getFileIntoStr(String s) {
        return getFileIntoStr(_appContext, s);
    }

    public static JSONObject jsonConfig = new JSONObject();

    public static void setJsonConfig(String K, Object V) {
        try {
            jsonConfig.put(K, V);
        } catch (JSONException e) {
            Log.d(LOGTAG, "setConfig " + K);
            e.printStackTrace();
        }
    }

    public static boolean isEmptyString(String s) {
        if (s == null || "".equals(s)) return true;
        if ("null".equals(s)) return true;//tmp solution for json optString() return string "null"
        return false;
    }

    public static boolean isEmpty(Object o) {
        if (o == null) return true;
        return false;
    }

    public static String getString(Object o) {
        if (o == null) return null;
        return o.toString();
    }

    public static String optString(Object o) {
        if (o == null) return "";
        String rt = o.toString();
        if (rt == null) return "";
        return rt;
    }

    public static HybridUi getHybridUi(String uiName) {

        HybridUi ui = new WebViewUi();
        ui.initPageData("{topbar:'Y',addr:'file://android_asset/root.htm'}");

        return ui;
    }

//    public static void showUi(HybridUi ui, Context ctx) {
//        if (ctx == null) ctx = getAppContext();
//        final Context _ctx = ctx;
//        Intent intent = new Intent(_ctx, ui.getClass());
//        intent.putExtra("uiData", ui.uiData.toString());
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        _ctx.startActivity(intent);
//    }
//
//    public static void showUi(HybridUi ui) {
//        showUi(ui, null);
//    }

    public static HybridApi getHybridApi(String uiName) {
        //HybridUiActivity ui=new HybridUiActivity();
        return null;
    }

    public static BridgeWebView BuildOldJsBridge(Context _ctx) {
        BridgeWebView wv;
        wv = new BridgeWebView(_ctx);
        return wv;
    }

    public static void appAlert(Context ctx, String msg, AlertDialog.OnClickListener clickListener) {
        AlertDialog.Builder b2;
        b2 = new AlertDialog.Builder(ctx);
        b2.setMessage(msg).setPositiveButton("Close", clickListener);
        b2.setCancelable(false);//click other place would cause cancel
        b2.create();
        b2.show();
    }

    public static void appConfirm(
            Context ctx, String msg,
            AlertDialog.OnClickListener okListener,
            AlertDialog.OnClickListener cancelListener) {
        AlertDialog.Builder b2;
        b2 = new AlertDialog.Builder(ctx);
        b2.setMessage(msg).setPositiveButton("Close", okListener)
                .setNegativeButton("cancel", cancelListener);
        b2.setCancelable(false);
        b2.create();
        b2.show();
    }

    public static void startUi(String name, String initParam, Activity caller, Class targetClass) {
//        if (ctx == null) ctx = getAppContext();
//        Context _ctx = ctx;
        Intent intent = new Intent(caller, targetClass);//TODO the class from config
//        intent.putExtra("uiData", ui.uiData.toString());
        intent.putExtra("uiData", initParam);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //caller.startActivity(intent);
        caller.startActivityForResult(intent, 1);//onActivityResult()
    }
    //TODO rewrite JsBridge
//    @SuppressLint("JavascriptInterface")
//    public static WebView BuildWebViewWithJsBridgeSupport(Context ctx) {
//        final Context _ctx = ctx;
//
//        final BridgeWebView wv = new BridgeWebView(_ctx);
//
//        WebSettings mWebSettings = wv.getSettings();
//
//        if (Build.VERSION.SDK_INT >= 11) {
//            mWebSettings.setDisplayZoomControls(false);
//        }
//
//        wv.setVerticalScrollBarEnabled(false);
//        wv.setHorizontalScrollBarEnabled(false);
//
//        mWebSettings.setJavaScriptEnabled(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            wv.setWebContentsDebuggingEnabled(true);
//        }
////        String jsContent = getFileIntoStr(wv.getContext(), "JsBridge.js");
////        if (jsContent != null) wv.loadUrl("javascript:" + jsContent);
//
////        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
////            this.loadUrl(javascriptCommand);
////        }
//
//        //TODO
//        //thread handling is not yet ok... wait...
////        wv.addJavascriptInterface(new JavascriptInterface() {
////            @JavascriptInterface
////            public String _send(String cmd_s, String id_s, String param_s) {
////                Log.v(LOGTAG, "cmd_s=" + cmd_s + ",param_s=" + param_s);
////                return "testreturnfromsend";
////            }
////        }, "AndroidWebView");
//
//        return wv;
//    }
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

//run backgroup service
//        Intent bg = new Intent(_ctx, BackService.class);
//        this.startService(bg);

