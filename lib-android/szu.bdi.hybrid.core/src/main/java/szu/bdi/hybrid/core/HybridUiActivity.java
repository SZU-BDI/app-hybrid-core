package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

//import android.view.WindowManager;

//TODO bugfix the jsbridge...

//@ref http://stackoverflow.com/questions/20138434/alternate-solution-for-setjavascriptenabledtrue
@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class HybridUiActivity extends Activity {

    //    final private static String LOGTAG = "HybridUiActivity";
    final private static String LOGTAG = "" + (new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName());

    private String mURL;

    private BridgeWebView mWebView;
//    private WebView mWebView;

    public class JavaScriptInterface {
        Context mContext1;

        JavaScriptInterface(Context c) {
            mContext1 = c;
        }
    }

    protected CallBackFunction _cb = null;

    //work with this.startActivityForResult() + popupActivity(setResult() + finish())
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOGTAG, "onActivityResult resultCode=" + resultCode);
        if (_cb != null && resultCode > 0) {
            Log.v(LOGTAG, "onCallBack OK");
            _cb.onCallBack(new Gson().toJson("OK"));//TODO
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, ".onCreate()");

        //Hide title bar
        if (false) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        } else {
//            requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//            requestWindowFeature(Window.FEATURE_LEFT_ICON);
            //setContentView(R.layout.); //or whatever layout is shows
//            setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.abc_btn_check_material);
            //getActionBar().setIcon(R.drawable.abc_btn_radio_material);
//            getActionBar().setIcon(R.drawable.back3x);
//            try {
////                getActionBar().setDisplayHomeAsUpEnabled(true);
//                //getActionBar().setdis
//            } catch (NullPointerException ex) {
//                ex.printStackTrace();
//            }
            setTitle("TODO setTitle()");
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//            }
            try {
                ActionBar actionBar = getActionBar();
                //setDisplayHomeAsUpEnabled make onOptionsItemSelected() work
                actionBar.setDisplayHomeAsUpEnabled(true);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            } catch (NoSuchMethodError ex) {
                ex.printStackTrace();
            }
        }

//Hide status bar.  todo by param
//        if(true)
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final Context _ctx = this;

//        mWebView = HybridService.BuildWebViewWithJsBridgeSupport(_ctx);//TODO

        //com.github.lzyzsd.jsbridge
        mWebView = HybridService.BuildOldJsBridge(_ctx);

        //NOTES: if not set, the js alert won't effect...(maybe the default return is true)
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                final String msg = message;
                AlertDialog.Builder b2;
                b2 = new AlertDialog.Builder(_ctx);
                b2.setMessage(msg).setPositiveButton("Close", new AlertDialog
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b2.create();
                b2.show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult jsrst) {
                final String msg = message;
                AlertDialog.Builder b2;
                b2 = new AlertDialog.Builder(_ctx);
                b2.setMessage(msg).setPositiveButton("Close", new AlertDialog
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsrst.confirm();
                    }
                });
                b2.setNegativeButton("cancel", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsrst.cancel();
                    }
                });
//                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }
        });

        setContentView(mWebView);

        mURL = "file:///android_asset/root.htm";//TODO
        mWebView.registerHandler("_app_activity_close", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                Log.v(LOGTAG, "handler = _app_activity_close");
                HybridUiActivity.this.onBackPressed();
            }
        });
        mWebView.registerHandler("_app_activity_open", new BridgeHandler() {

                    @Override
                    public void handler(String data, CallBackFunction cb) {
                        Log.v("_app_activity_open", data);

                        HybridUiActivity.this._cb = cb;

                        Intent intent = new Intent(HybridUiActivity.this, HybridUiActivity.class);
                        startActivityForResult(intent, 1);
                    }

                }

        );

        Log.v(LOGTAG, "load mURL=" + mURL);
        mWebView.loadUrl(mURL);

    }

    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        Log.v(LOGTAG, "onBackPressed set Result 1");
        setResult(1, new Intent());
        finish();
    }

    //in case old androids dont have onBackPress()
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(LOGTAG, "onKeyDown KEYCODE_BACK");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
