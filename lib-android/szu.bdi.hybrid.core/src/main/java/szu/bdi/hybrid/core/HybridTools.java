package szu.bdi.hybrid.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class HybridTools {
    final private static String LOGTAG = "HybridTools";

    private static Context _appContext = null;
    public static String localWebRoot;
    final static String UI_MAPPING = "ui_mapping";

    //IMPORTANT !!!: remember in the app entry, set HybridTools.setAppContext(getApplicationContext());
    public static void setAppContext(Context appContext) {
        if (appContext != null)
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

    //NOTES:  because getSeeting will cause mis-understanding
    public static String getSavedSetting(Context mContext, String whichSp, String field) {
        SharedPreferences sp = mContext.getSharedPreferences(whichSp, Context.MODE_PRIVATE);
        String s = sp.getString(field, "");
        return s;
    }

    public static void saveSetting(Context mContext, String whichSp, String field, String value) {
        SharedPreferences sp = (SharedPreferences) mContext.getSharedPreferences(whichSp, Context.MODE_PRIVATE);
        if (null == value) value = "";//I want to store sth not null
        sp.edit().putString(field, value).commit();
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
        if (s == null || "".equals(s)) return null;
        try {
            return new JSONObject(s);
        } catch (Exception ex) {
            Log.v(LOGTAG, "failed to parse json=" + s);
            ex.printStackTrace();
        }
        return null;
    }

    public static String o2s(JSONObject o) {
        if (o == null) return null;
        return o.toString();
    }

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
    public static String readAssetInStr(Context c, String urlStr) {
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

    public static String readAssetInStr(String s) {
        return readAssetInStr(_appContext, s);
    }

    private static JSONObject _jAppConfig = new JSONObject();

    //init (replace the app config)
    public static void initAppConfig(JSONObject o) {
        _jAppConfig = o;
    }

    public static JSONObject wholeAppConfig() {
        return _jAppConfig;
    }

    public static void setAppConfig(String K, Object V) {
        try {
            _jAppConfig.put(K, V);
        } catch (JSONException e) {
            Log.d(LOGTAG, "setConfig " + K);
            e.printStackTrace();
        }
    }

    public static Object getAppConfig(String k) {
        return _jAppConfig.opt(k);
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
        AlertDialog.Builder b2;
        b2 = new AlertDialog.Builder(ctx);
        b2.setMessage(msg).setPositiveButton("Close", okListener)
                .setNegativeButton("cancel", cancelListener);
        b2.setCancelable(false);
        b2.create();
        b2.show();
    }

    //    public static JSONObject deepMerge(JSONObject source, JSONObject target) throws JSONException {
    //        for (String key: JSONObject.getNames(source)) {
    //            Object value = source.get(key);
    //            if (!target.has(key)) {
    //                target.put(key, value);
    //            } else {
    //                if (value instanceof JSONObject) {
    //                    JSONObject valueJson = (JSONObject)value;
    //                    deepMerge(valueJson, target.getJSONObject(key));
    //                } else {
    //                    target.put(key, value);
    //                }
    //            }
    //        }
    //        return target;
    //    }

    //shallow merge
    public static JSONObject basicMerge(JSONObject... jsonObjects) {
        JSONObject jsonObject = new JSONObject();
        for (JSONObject temp : jsonObjects) {
            if (temp == null) continue;
            Iterator<String> keys = temp.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    jsonObject.put(key, temp.get(key));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    public static void startUi(String name, String overrideParam_s, Activity caller) {
        Object uia = getAppConfig(UI_MAPPING);
        if (uia == null) {
            HybridTools.quickShowMsgMain("config.json error!!!");
            return;
        }
        JSONObject defaultParam = ((JSONObject) uia).optJSONObject(name);
        if (defaultParam == null) {
            HybridTools.quickShowMsgMain("config.json not found " + name + " !!!");
            return;
        }

        JSONObject overrideParam = s2o(overrideParam_s);
        JSONObject callParam = basicMerge(defaultParam, overrideParam);
        Log.v(LOGTAG, "param after merge=" + callParam);

        String clsName = callParam.optString("class");
        if (isEmptyString(clsName)) {
            HybridTools.quickShowMsgMain("config.json error!!! config not found for name=" + name);
            return;
        }
        Class targetClass = null;
        try {
            //reflection:
            targetClass = Class.forName(clsName);
            Log.v(LOGTAG, "class " + clsName + " found for name " + name);
        } catch (ClassNotFoundException e) {
            HybridTools.quickShowMsgMain("config.json error!!! class now found for " + clsName);
            return;
        }

        Intent intent = new Intent(caller, targetClass);

        try {
            if (!isEmptyString(name)) {
                callParam.put("name", name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String uiData_s = o2s(callParam);

        intent.putExtra("uiData", uiData_s);
        try {
            caller.startActivityForResult(intent, 1);//onActivityResult()
        } catch (Throwable t) {
            quickShowMsgMain("Error:" + t.getMessage());
        }
    }

    public static void bindWebViewApi(JsBridgeWebView wv, final HybridUi callerAct) {
        wv.registerHandler("_app_activity_close", new ICallBackHandler() {
            @Override
            public void handler(String data, ICallBackFunction cb) {
                Log.v(LOGTAG, "handler = _app_activity_close");
                callerAct.setCallBackFunction(cb);
                callerAct.onBackPressed();
            }
        });
        wv.registerHandler("_app_activity_open", new ICallBackHandler() {
                    @Override
                    public void handler(String data, ICallBackFunction cb) {
                        Log.v("_app_activity_open", data);

                        callerAct.setCallBackFunction(cb);

                        //TMP TEST:
                        //HybridTools.startUi("UiRoot", "{topbar:'N',address:'" + root_htm_s + "'}", _activity);

                        String uiName = "UiContent";//default
                        JSONObject data_o = HybridTools.s2o(data);
                        //try override by the callParam.name
                        if (data_o != null) {
                            String t = data_o.optString("name");
                            if (!HybridTools.isEmptyString(t)) {
                                uiName = t;
                            }
                        }
                        HybridTools.startUi(uiName, data, callerAct);
                    }

                }
        );
        //TODO get the binding info from config

//        Object uia = getAppConfig(UI_MAPPING);
//        if (uia == null) {
//            HybridTools.quickShowMsgMain("config.json error!!!");
//            return;
//        }
//        JSONObject defaultParam = ((JSONObject) uia).optJSONObject(name);
//        if (defaultParam == null) {
//            HybridTools.quickShowMsgMain("config.json not found " + name + " !!!");
//            return;
//        }
//
//        JSONObject overrideParam = s2o(overrideParam_s);
//        JSONObject callParam = basicMerge(defaultParam, overrideParam);
//        Log.v(LOGTAG, "param after merge=" + callParam);
//
//        String clsName = callParam.optString("class");
//        if (isEmptyString(clsName)) {
//            HybridTools.quickShowMsgMain("config.json error!!! config not found for name=" + name);
//            return;
//        }
//        Class targetClass = null;
//        try {
//            //reflection:
//            targetClass = Class.forName(clsName);
//            Log.v(LOGTAG, "class " + clsName + " found for name " + name);
//        } catch (ClassNotFoundException e) {
//            HybridTools.quickShowMsgMain("config.json error!!! class now found for " + clsName);
//            return;
//        }
//
//        Intent intent = new Intent(caller, targetClass);
//
//        try {
//            if (!isEmptyString(name)) {
//                callParam.put("name", name);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        String uiData_s = o2s(callParam);
//
//        intent.putExtra("uiData", uiData_s);
//        try {
//            caller.startActivityForResult(intent, 1);//onActivityResult()
//        } catch (Throwable t) {
//            quickShowMsgMain("Error:" + t.getMessage());
//        }
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

    //@ref http://stackoverflow.com/questions/8258725/strict-mode-in-android-2-2
    //StrictMode.ThreadPolicy was introduced since API Level 9 and the default thread policy had been changed since API Level 11, which in short, does not allow network operation (eg: HttpClient and HttpUrlConnection) get executed on UI thread. If you do this, you get NetworkOnMainThreadException.
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

    protected static Application _app = null;

    public static void setApplication(Application app) {
        if (app != null)
            _app = app;
    }

    public static Application getApplication() {
        return _app;
    }
}


