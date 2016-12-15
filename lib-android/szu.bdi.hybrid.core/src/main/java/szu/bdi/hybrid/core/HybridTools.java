package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import info.cmptech.JSO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class HybridTools {
    final static String ANDROID_APPLICATION = "_android_applicaton_";
    public final static String NETWORK_STATUS = "_network_status_";
    final static String UI_MAPPING = "ui_mapping";
    final static String API_AUTH = "api_auth";
    final static String API_MAPPING = "api_mapping";
    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();

    private static Map<String, Object> _memStore = new HashMap<String, Object>();

    //private static Application _thisApp = null;

    //private static JSONObject _jAppConfig = new JSONObject();
    private static JSO _jAppConfig = null;//new info.cmptech.JSO();
    private static String _localWebRoot = "";

    //public static boolean flagAppWorking = true;//NOTES: backgroundService might use it.

    public static Application getApplication() {
        Application _thisApp = null;
        try {
            //_thisApp = (Application) _memStore.get(ANDROID_APPLICATION);
            _thisApp = (Application) getCacheFromMem(ANDROID_APPLICATION);
            if (null == _thisApp) {
                _thisApp = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication").invoke(null, (Object[]) null);
                if (null != _thisApp) {
                    //_memStore.put(ANDROID_APPLICATION, _thisApp);
                    setCacheToMem(ANDROID_APPLICATION, _thisApp);
                }
            }
            if (null == _thisApp) KillAppSelf();
        } catch (Exception ex) {
            ex.printStackTrace();
            KillAppSelf();
        }
        return _thisApp;
    }

    public static Object getCacheFromMem(String key) {
        return _memStore.get(key);
    }

    public static Object setCacheToMem(String key, Object val) {
        return _memStore.put(key, val);
    }

    public static Context getAppContext() {
        return getApplication().getApplicationContext();
    }

    public static void quickShowMsgMain(String msg) {
        quickShowMsg(getAppContext(), msg);
    }

    //NOTES: for alert blocking, using appAlert/appConfirm
    public static void quickShowMsg(Context mContext, String msg) {
        //@ref http://blog.csdn.net/droid_zhlu/article/details/7685084
        //A toast is a view containing a quick little message for the user.
        // The toast class helps you create and show those.
        try {
            Toast toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //persistent save/load
    //NOTES:  because getSetting will cause mis-understanding
    public static String getSavedSetting(Context mContext, String whichSp, String field) {
        SharedPreferences sp = mContext.getSharedPreferences(whichSp, Context.MODE_PRIVATE);
        String s = sp.getString(field, "");
        return s;
    }

    public static void saveSetting(Context mContext, String whichSp, String field, String value) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(whichSp, Context.MODE_PRIVATE);
        if (null == value) value = "";//I want to store sth not null
        //sp.edit().putString(field, value).commit();
        sp.edit().putString(field, value).apply();
    }

    public static String webPost(String uu, String post_s) {
        String return_s = null;

        try {
            URL url = new URL(uu);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(true);
                //conn.setChunkedStreamingMode(0);
                conn.setRequestMethod("POST");
                OutputStream out = new BufferedOutputStream(conn.getOutputStream());

                //write to the stream
                out.write(post_s.getBytes("UTF-8"));

                InputStream in = new BufferedInputStream(conn.getInputStream());
                return_s = HybridTools.stream2string(in);
            } finally {
                try {
                    conn.disconnect();
                } catch (Throwable t) {
                }
            }
        } catch (Throwable ex) {
            //TODO 如果是 filenotfound的exception，多数是因为远程错误400之类的，待处理
            ex.printStackTrace();
            return_s = ex.getMessage();
            if (isEmptyString(return_s)) {
                return_s = "" + ex.getClass().getName();
            }
        }
        return return_s;
    }

    //Wrap the raw webPost for cmp api call
    public static JSO apiPost(String url, JSO jo) {
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
                return JSO.s2o(return_s);
        } catch (Exception ex) {
        }
        JSO rt = new JSO();
        rt.setChild("STS", JSO.s2o("KO"));
        rt.setChild("errmsg", JSO.s2o(return_s));
//        try {
//            rt.put("STS", "KO");
//            rt.put("errmsg", "" + return_s);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return rt;
    }

    public static String isoDateTime() {
        //String time_s = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en_US"));
        String time_s = df.format(new Date());
        return time_s;
    }

    private static String readAssetInStrWithoutComments(String s) {
        return readAssetInStrWithoutComments(getAppContext(), s);
    }

    private static String readAssetInStrWithoutComments(Context c, String urlStr) {
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

    //init (replace the app config)
    public static void initAppConfig(JSO o) {
        _jAppConfig = o;
    }

    public static JSO wholeAppConfig() {
        return _jAppConfig;
    }

    public static void setAppConfig(String K, JSO V) {
//        try {
        //_jAppConfig.put(K, V);
        _jAppConfig.setChild(K, V);
//        } catch (JSONException e) {
//            Log.d(LOGTAG, "setConfig " + K);
//            e.printStackTrace();
//        }
    }

    public static void checkAppConfig() {
        if (_jAppConfig == null || _jAppConfig.isNull()) {
            final String sJsonConf = readAssetInStrWithoutComments("config.json");
            final JSO o = JSO.s2o(sJsonConf);
            HybridTools.initAppConfig(o);
        }
    }

    public static JSO getAppConfig(String k) {
        return _jAppConfig.getChild(k);
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
        if (null == cancelListener) {
            cancelListener = new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //dialog.cancel();
                    //Log.v(LOGTAG, ".appConfirm().click()");
                }
            };
        }
        if (null == ctx) ctx = getAppContext();
        AlertDialog.Builder b2;
        b2 = new AlertDialog.Builder(ctx);
        b2.setMessage(msg)
                .setPositiveButton("NO", cancelListener)
                .setNegativeButton("YES", okListener);
        b2.setCancelable(false);
        b2.create();
        b2.show();
    }

    public static void startUi(String name, String overrideParam_s, Activity caller) {
        startUi(name, overrideParam_s, caller, null);
    }

    public static void startUi(String name, String overrideParam_s, Activity caller, HybridUiCallback cb) {
        checkAppConfig();

        JSO uia = getAppConfig(UI_MAPPING);
        if (uia == null || uia.isNull()) {
            HybridTools.quickShowMsgMain("config.json error!!!");
            //HybridTools.appAlert(getAppContext(),"config.json error !",null);
            return;
        }

        JSO defaultParam = uia.getChild(name);
        if (defaultParam == null || defaultParam.isNull()) {
            HybridTools.quickShowMsgMain("config.json not found " + name + " !!!");
            return;
        }

        JSO overrideParam = JSO.s2o(overrideParam_s);
        JSO callParam = JSO.basicMerge(defaultParam, overrideParam);
        Log.v(LOGTAG, "param after merge=" + callParam);

        String mode = callParam.getChild("mode").toString();
        String clsName = callParam.getChild("class").toString();
        if (isEmptyString(clsName)) {
            if ("WebView".equalsIgnoreCase(mode)) {
                clsName = SimpleHybridWebViewUi.class.getName();
            } else {
                HybridTools.quickShowMsgMain("config.json error!!! config not found for name=" + name);
                return;
            }
        }

        //////////////////////////////////////////////
        //caller, calleeClass, uiDataJSO, cb
        Intent intent = null;
        try {
            intent = new Intent(caller, Class.forName(clsName));
        } catch (Exception ex) {
            Looper.prepare();
            HybridTools.quickShowMsgMain("not found " + clsName);
            return;
        }

        if (!isEmptyString(name)) {
            callParam.setChild("name", JSO.s2o(name));
        }

        String uiData_s = JSO.o2s(callParam);

        intent.putExtra("uiData", uiData_s);

        try {
            final Intent tmpIntent = intent;
            final Activity tmpCaller = caller;

            HybridUi.tmpUiCallback = cb;//tmp ugly working solution, improve in future...

            tmpCaller.startActivity(tmpIntent);
        } catch (Throwable t) {
            Log.v(LOGTAG, "Throwable " + t.getMessage() + "  check manifest xml???");
            quickShowMsgMain("Error:" + t.getMessage());
        }
    }

    protected static JSO findSubAuth(JSO jso, String nameOf) {
        JSO _found = null;
        Iterator it = jso.getChildKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            try {
                if (Pattern.matches(key, nameOf)) {
                    _found = jso.getChild(key);
                    break;
                }
            } catch (PatternSyntaxException ex) {
                Log.v(LOGTAG, "wrong regexp=" + key);
                ex.printStackTrace();
            }
        }
        return _found;
    }

    public static void bindWebViewApi(JsBridgeWebView wv, final HybridUi callerAct) {
        String name = optString(callerAct.getUiData("name"));
        if (isEmptyString(name)) {
            quickShowMsgMain("ConfigError: caller act name empty?");
            return;
        }
        JSO uia = getAppConfig(API_AUTH);
        if (uia == null) {
            HybridTools.quickShowMsgMain("ConfigError: empty " + API_AUTH);
            return;
        }
        JSO apia = getAppConfig(API_MAPPING);
        if (apia == null) {
            HybridTools.quickShowMsgMain("ConfigError: empty " + API_MAPPING);
            return;
        }

        //JSONObject authObj = uia.optJSONObject(name);
        JSO authObj = uia.getChild(name);
        if (authObj == null || authObj.isNull()) {
            HybridTools.quickShowMsgMain("ConfigError: not found auth for " + name + " !!!");
            return;
        }
        Log.v(LOGTAG, " authObj=" + authObj);

        String address = optString(callerAct.getUiData("address"));
        JSO foundAuth = findSubAuth(authObj, address);
        if (foundAuth == null) {
            HybridTools.quickShowMsgMain("ConfigError: not found match auth for address (" + address + ") !!!");
            return;
        }
        Log.v(LOGTAG, " foundAuth=" + foundAuth);
        ArrayList<JSO> ja = foundAuth.asArrayList();
        for (int i = 0; i < ja.size(); i++) {
            String v = ja.get(i).asString();
            if (!isEmptyString(v)) {
                String clsName = apia.getChild(v).asString();
                Log.v(LOGTAG, "binding api " + v + " => " + clsName);
                if (isEmptyString(clsName)) {
                    HybridTools.quickShowMsgMain("ConfigError: config not found for api=" + v);
                    continue;
                }
                Class targetClass = null;
                try {
                    //reflection:
                    targetClass = Class.forName(clsName);
                    Log.v(LOGTAG, "class " + clsName + " found for name " + name);
                } catch (ClassNotFoundException e) {
                    HybridTools.quickShowMsgMain("ConfigError: class not found " + clsName);
                    continue;
                }
                try {
                    HybridApi api = (HybridApi) targetClass.newInstance();
                    api.setCallerUi(callerAct);
                    wv.registerHandler(v, api.getHandler());
                } catch (Throwable t) {
                    t.printStackTrace();
                    HybridTools.quickShowMsgMain("ConfigError: faile to create api of " + clsName);
                    continue;
                }
            }
        }

    }

    public static boolean copyAssetFolder(AssetManager assetManager,
                                          String fromAssetPath, String toPath) {
        try {
            Log.v(LOGTAG, "copyAssetFolder " + fromAssetPath + "=>" + toPath);
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains("."))
                    res &= copyAssetFile(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                else
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyAssetFile(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            Log.v(LOGTAG, "copyAsset " + fromAssetPath + "=>" + toPath);
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    //@ref http://stackoverflow.com/questions/10500775/parse-json-from-httpurlconnection-object
    public static String stream2string(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static int getStrLen(String rt_s) {
        if (rt_s == null) return -1;
        return rt_s.length();
    }

    static public String classNameOf(Object o) {
        if (o == null) return "null";
        return o.getClass().getName();
    }

    public static String getLocalWebRoot() {
        if (isEmptyString(_localWebRoot)) {
            _localWebRoot = "/android_asset/web/";
        }
        return _localWebRoot;
    }

    //@ref http://stackoverflow.com/questions/8258725/strict-mode-in-android-2-2
    //StrictMode.ThreadPolicy was introduced since API Level 9 and the default thread policy had been changed since API Level 11,
    // which in short, does not allow network operation (eg: HttpClient and HttpUrlConnection)
    // get executed on UI thread. If you do this, you get NetworkOnMainThreadException.
    public static void uiNeedNetworkPolicyHack() {
        int _sdk_int = android.os.Build.VERSION.SDK_INT;
        if (_sdk_int > 8) {
            try {
                Log.d(LOGTAG, "setThreadPolicy for api level " + _sdk_int);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public static void KillAppSelf() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static String readAssetInStr(String file_s) {
        return readAssetInStr(file_s, false);//default original
    }

    public static String readAssetInStr(String file_s, boolean filterRowComments) {
        InputStream in = null;
        try {
            in = getAppContext().getAssets().open(file_s);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (filterRowComments) {
                    if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                        sb.append(line + "\n");
                    }
                } else {
                    sb.append(line + "\n");
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

    ///////////////////////////////////////////////////////
    //UI-Holders
//    private static ArrayList<HybridUi> _uia = new ArrayList<HybridUi>();
//
//    public static ArrayList<HybridUi> debugHybridUis() {
//        Log.v(LOGTAG, "debugHybridUis size = " + _uia.size());
//        return _uia;
//    }
//
//    public static void addHybridUi(HybridUi ui) {
//        _uia.add(ui);
//    }
//
//    public static void closeHybridUi(HybridUi ui) {
//        if (_uia.contains(ui))
//            _uia.remove(ui);
//    }

}


