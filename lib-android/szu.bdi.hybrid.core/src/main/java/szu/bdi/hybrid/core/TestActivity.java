package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

//TODO bugfix the jsbridge...

//@ref http://stackoverflow.com/questions/20138434/alternate-solution-for-setjavascriptenabledtrue
@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class TestActivity extends Activity {

    //    final private static String LOGTAG = "HybridUi";
    final private static String LOGTAG = "" + (new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName());

    private String mURL;

    private BridgeWebView mWebView;

    public class JavaScriptInterface {
        Context mContext1;

        JavaScriptInterface(Context c) {
            mContext1 = c;
        }
    }

    protected CallBackFunction _cb = null;//TODO !!!

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOGTAG, "onActivityResult !!!! " + resultCode);
        if (_cb != null && resultCode > 0) {
            Log.v(LOGTAG, "onActivityResult ???? " + resultCode);
            _cb.onCallBack(new Gson().toJson("BBBB"));
        }
    }

    protected void temp2(CallBackFunction cb) {
        this._cb = cb;
        Log.v(LOGTAG, "temp2 !!!!");

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TestActivity.this, TestActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                TestActivity.this.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, ".onCreate()");

//        final Context _ctx = getApplicationContext();

        final Context _ctx = this;

        //mWebView = HybridService.BuildWebViewWithJsBridgeSupport(_ctx);//TODO

        mWebView = HybridService.BuildOldJsBridge(_ctx);
        setContentView(mWebView);

        //if not set, the js alert won't effect...(maybe the default return is true)
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Log.v(LOGTAG, "TODO onJsAlert " + url + "," + message);
                return false;
//                final String msg = message;
////                new Handler().postDelayed(new Runnable() {
////                    public void run() {
//                AlertDialog.Builder b2;
//                b2 = new AlertDialog.Builder(_ctx);
//                b2.setMessage(msg).setPositiveButton("Close", new AlertDialog
//                        .OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        result.confirm();
//                    }
//                });
//                       /* b2.setNegativeButton("cancel", new AlertDialog.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                result.cancel();
//                            }
//                        });*/
//                        /*b2.setCancelable(false);*/
//                b2.create();
//                b2.show();
////                    }
////                }, 500);
//                return true;
            }
        });

        mWebView.registerHandler("_app_activity_open", new BridgeHandler() {

                    @Override
                    public void handler(String data, CallBackFunction cb) {
                        Log.v("app_Activity_Open", data);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(data);
////
////                    String Topbar = jsonObject.optString("topbar");
////                    String Mode = jsonObject.optString("mode");
////                    String Address = jsonObject.optString("address");
////
//                            Intent intent = new Intent(_ctx, HybridUi.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//
////                    intent.putExtra("Topbar", Topbar);
////                    intent.putExtra("Mode", Mode);
////                    intent.putExtra("Address", Address);
//                            startActivity(intent);
////                    HybridService.getHybridUi(HybridUi.this, "file://android_asset/root.htm");

//                            Intent intent = new Intent(getApplicationContext(), UiContent.class);
//                            //TODO
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                            startActivity(intent);

//                            HybridService hs = new HybridService(_ctx);
//                            HybridUi ui = hs.getHybridUi("file://android_asset/root.htm");
//                            Intent intent = new Intent(getApplicationContext(), TestActivity.class);
////                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                            startActivity(intent);

                            TestActivity.this.temp2(cb);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                function.onCallBack("Activity opened");
//                        function.onCallBack(new Gson().toJson("BBBB"));//TODO
                    }

                }

        );
        mURL = "file:///android_asset/content.htm";
        Log.v(LOGTAG, "load mURL=" + mURL);
        mWebView.loadUrl(mURL);
    }

    @Override
    public void onBackPressed() {
        Log.v(LOGTAG, "onBackPressed=" + 1);

//        Bundle bundle = new Bundle();
//        bundle.putString(FIELD_A, mA.getText().toString());

        Intent mIntent = new Intent();
//        mIntent.putExtras(bundle);
        setResult(1, mIntent);
        super.onBackPressed();
    }
}
