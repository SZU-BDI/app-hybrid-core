package szu.bdi.hybrid.core;

/**
 * Created by wjc on 6/6/16.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
// import com.google.android.gms.appindexing.Action;
// import com.google.android.gms.appindexing.AppIndex;
// import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by Muhammad Azhar on 6/5/2016.
 */
public class HybridUi extends Activity {
    final private static String LOGTAG = "UIRoot";
//    final private static String LOGTAG = "" + (new Object() {
//        public String getClassName() {
//            String clazzName = this.getClass().getName();
//            return clazzName.substring(0, clazzName.lastIndexOf('$'));
//        }
//    }.getClassName());

    private long exitTime = 0;

//    public String printJson;
    private String mURL;
//    private String text;
//    private Handler handler;

    private BridgeWebView mWebView;

    private ProgressDialog progressDialog;
    private Activity selfActivity = this;
    private Handler mHandler;

    public static boolean isruning = false;//用于判断当前activity是否是active的。因为遇到异常android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@41e29c18

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    // is not valid; is your activity running?
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, " UiRoot onCreate()");

        setContentView(R.layout.activity_uiroot);


//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.set_activity_tltle_style);
/*
        Button title_btn_back = (Button) findViewById(R.id.title_btn);
        final TextView title_TextView = (TextView) findViewById(R.id.title_textView);
        text = getIntent().getStringExtra(AppHelper.Extra_openwebsite_key);
        title_TextView.setText(text);
  */
/*

        title_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        */

        // mURL = getIntent().getData().toString();

        mURL = "file:///android_asset/root.htm";

        progressDialog = new ProgressDialog(this);


        //   progressDialog.setTitle(getText(R.string.Login_reminder_information));
        //  progressDialog.setMessage(getText(R.string.text_progressdialog_open_website));

        progressDialog.setCanceledOnTouchOutside(false);

        //TODO 动态生成绑定！！
        mWebView = (BridgeWebView) findViewById(R.id.webView);

        mWebView.setDefaultHandler(new DefaultHandler());

        mWebView.getSettings().setJavaScriptEnabled(true);

        WebSettings mWebSettings = mWebView.getSettings();


        //设置了缓存也看不出变化。。。
       /* mWebSettings.setAppCacheEnabled(true);// 设置启动缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 设置缓存模式*/
//        mWebSettings.setSupportZoom(true);//default true
        //mWebSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        //mWebSettings.setBuiltInZoomControls(true);//设置出现缩放工具

        /*mWebSettings.setDomStorageEnabled(true);*/

        if (Build.VERSION.SDK_INT >= 11) {
            mWebSettings.setDisplayZoomControls(false);
        }

//        mWebSettings.setUseWideViewPort(true);

            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                    //if (isruning)
                    {
                        AlertDialog.Builder b2;
                        b2 = new AlertDialog.Builder(HybridUi.this);
                        b2.setMessage(message)
                                .setPositiveButton("ok", new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                });
                       /* b2.setNegativeButton("cacel", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });*/

                        /*b2.setCancelable(false);*/
                        b2.create();
                        b2.show();

                    }
                    return true;
                }
            });

            mWebView.loadUrl(mURL);

            mWebView.registerHandler("_app_activity_open", new BridgeHandler() {

                @Override
                public void handler(String data, CallBackFunction function) {
                    Log.i("app_Activity_Open", data);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(data);

                        String Topbar = jsonObject.optString("topbar");
                        String Mode = jsonObject.optString("mode");
                        String Address = jsonObject.optString("address");

                        Intent intent = new Intent(HybridUi.this, NewActivity.class);
                        intent.putExtra("Topbar", Topbar);
                        intent.putExtra("Mode", Mode);
                        intent.putExtra("Address", Address);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    function.onCallBack("Activity opened");

                    //  function.onCallBack(new Gson().toJson(p));

                }

            });

            mWebView.registerHandler("_app_activity_close", new BridgeHandler() {

                @Override
                public void handler(String data, CallBackFunction function) {
                    Log.i(LOGTAG, "handler = _app_activity_close");
                    finish();
                }

            });

            mWebView.registerHandler("_app_activity_set_title", new BridgeHandler() {

                @Override
                public void handler(String title, CallBackFunction function) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(title);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String tit = jsonObject.optString("title");
                    //  title_TextView.setText(tit);
                    //  Log.i(TAG, "handler = submitFromWeb, data from web = " + tit);
                    function.onCallBack("_app_activity_set_title, response data from Java :" + tit);

                }

            });
            mWebView.addJavascriptInterface(new JavaScriptInterface(this), "AndroidWebView");//TODO

    }

    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isruning = true;
    }

    public void onPause() {
        super.onPause();
        isruning = false;
    }

    public void onStop() {
        super.onStop();
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "OpenWebsite Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.hybrid.core/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        Log.d(TAG, "—————————————____________OpenWebView close_______________-----------------");
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.disconnect();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), getText(R.string.WebActivity_finishi), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * This method is designed to hide how Javascript is injected into
     * the WebView.
     * <p/>
     * In KitKat the new evaluateJavascript method has the ability to
     * give you access to any return values via the ValueCallback object.
     * <p/>
     * The String passed into onReceiveValue() is a JSON string, so if you
     * execute a javascript method which return a javascript object, you can
     * parse it as valid JSON. If the method returns a primitive value, it
     * will be a valid JSON object, but you should use the setLenient method
     * to true and then you can use peek() to test what kind of object it is,
     *
     * @param javascript
     */

    public void loadJavascript(String javascript) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // In KitKat+ you should use the evaluateJavascript method
            mWebView.evaluateJavascript(javascript, new ValueCallback<String>() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onReceiveValue(String s) {
                    JsonReader reader = new JsonReader(new StringReader(s));

                    // Must set lenient to parse single values
                    reader.setLenient(true);

                    try {
                        if (reader.peek() != JsonToken.NULL) {
                            if (reader.peek() == JsonToken.STRING) {
                                String msg = reader.nextString();
                                if (msg != null) {
                                    Toast.makeText(getApplicationContext(),
                                            msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } catch (IOException e) {
                        Log.e("TAG", "MainActivity: IOException", e);
                    } finally {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            });
            Log.d("javascript", "_>=19_________________________________________________________________");
        } else {
            /**
             * For pre-KitKat+ you should use loadUrl("javascript:<JS Code Here>");
             * To then call back to Java you would need to use addJavascriptInterface()
             * and have your JS call the interface
             **/
            Log.d("javascript", "____<19______________________________________________________________");
            mWebView.loadUrl("javascript:" + javascript);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOGTAG, "UiRoot.onStart");

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "OpenWebsite Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://com.hybrid.core/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    public class JavaScriptInterface {
        Context mContext1;

        // Instantiate the interface and set the context
        JavaScriptInterface(Context c) {
            mContext1 = c;
        }

        @JavascriptInterface
        public void _close_activity() {
            HybridUi.this.finish();
        }

    }

//    class MHandler extends Handler {
//        WeakReference<UiRoot> mActivity;
//
//        MHandler(UiRoot activity) {
//            mActivity = new WeakReference<UiRoot>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//
//            switch (msg.what) {
//
//            }
//        }
//
//    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
////        WorkService.delHandler(mHandler);
//    }

}
