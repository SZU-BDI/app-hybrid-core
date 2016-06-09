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
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

//import android.view.WindowManager;

//TODO bugfix the jsbridge...

//@ref http://stackoverflow.com/questions/20138434/alternate-solution-for-setjavascriptenabledtrue
@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class HybridUi extends Activity {

    //    final private static String LOGTAG = "HybridUi";
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

        //fail for API < 11
//        https://www.processon.com/view/link/57597a42e4b080e40c822f16
        if (false)
            mWebView.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public String js2jv(String id, String handler_s, String param_s) {
                    Log.v(LOGTAG, "_js2js_cb =>" + id + "," + handler_s + "," + param_s);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        //TODO
                        jsonObject = new JSONObject("{t:111}");
                        jsonObject.putOpt("k", new JSONObject("{t1:222}"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    HybridUi.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //mWebView.loadUrl("javascript:alert(222);");
//                        }
//                    });
//                HybridUi.this.runOnUiThread();
//                new Handler().postDelayed(new Runnable() {
//                    public void run() {
//                        Log.v(LOGTAG, "=> js2jv_cb =>, TODO");
////                        wv.loadUrl("javascript:JsBridgeHybrid.js2jv_cb(" + id + "," + jsonObject.toString() + ");");
//                        //if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
//                          //  wv.loadUrl("javascript:alert(222);");
//                        //}
//                    }
//                }, 2000);
                    //if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                    //Log.v(LOGTAG, "=> js2jv_cb =>" + id + ", TODO");
                    //wv.loadUrl("javascript:JsBridgeHybrid.js2jv_cb(" + id + "," + jsonObject.toString() + ");");
                    //}
                    return "TODO js2jv";
                }

                @JavascriptInterface
                public String jv2js_cb(String id, String result_s) {
                    Log.v(LOGTAG, "_js2js_cb =>" + id + "," + result_s);
                    return "TODO";
                }

            }, "JsBridgeHybrid_");

        //if not set, the js alert won't effect...(maybe the default return is true)
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
//                       /* b2.setNegativeButton("cancel", new AlertDialog.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                result.cancel();
//                            }
//                        });*/
//                        /*b2.setCancelable(false);*/
                b2.create();
                b2.show();
                return true;
            }
        });

        setContentView(mWebView);

        mURL = "file:///android_asset/root.htm";
        if (true) { //TODO ...
            mWebView.registerHandler("_app_activity_close", new BridgeHandler() {

                @Override
                public void handler(String data, CallBackFunction function) {
                    Log.v(LOGTAG, "handler = _app_activity_close");
                    HybridUi.this.onBackPressed();
                }
            });
            mWebView.registerHandler("_app_activity_open", new BridgeHandler() {

                        @Override
                        public void handler(String data, CallBackFunction cb) {
                            Log.v("_app_activity_open", data);
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
//                            Handler handler = new Handler(Looper.getMainLooper());
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Intent intent = new Intent(HybridUi.this, TestActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                                    HybridUi.this.startActivity(intent);
//                                }
//                            });
//                            Handler handler = new Handler(Looper.getMainLooper());
//                            handler.post(new Runnable() {
//                                public void run() {
//                                    Intent intent = new Intent(CurrentActivity.this, MenuActivity.class);
//                                    CurrentActivity.this.startActivity(intent);
//                                }
//                            });

//                            HybridService hs = new HybridService(_ctx);
//                            HybridUi ui = hs.getHybridUi("file://android_asset/root.htm");

//                            Handler handler = new Handler(Looper.getMainLooper());
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                }
//                            });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            HybridUi.this._cb = cb;

                            Intent intent = new Intent(HybridUi.this, HybridUi.class);
                            startActivityForResult(intent, 1);
                        }

                    }

            );
        }

        Log.v(LOGTAG, "load mURL=" + mURL);
        mWebView.loadUrl(mURL);

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                Intent intent = new Intent(_ctx, TestActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
////                HybridService hs = new HybridService(_ctx);
////                HybridUi ui = hs.getHybridUi("file://android_asset/root.htm");
////                ui.show();TODO
////                HybridService.getHybridUi(_ctx, "file://android_asset/root.htm");
//            }
//        }, 5000);
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

//TODO Dynamic binding with the Service...

//        mWebView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
//                //if (isruning)
//                {
//                    AlertDialog.Builder b2;
//                    b2 = new AlertDialog.Builder(HybridUi.this);
//                    b2.setMessage(message)
//                            .setPositiveButton("ok", new AlertDialog.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    result.confirm();
//                                }
//                            });
//                       /* b2.setNegativeButton("cancel", new AlertDialog.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                result.cancel();
//                            }
//                        });*/
//
//                        /*b2.setCancelable(false);*/
//                    b2.create();
//                    b2.show();
//
//                }
//                return true;
//            }
//        });
//            mWebView.registerHandler("_app_activity_open", new BridgeHandler() {
//
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    Log.i("app_Activity_Open", data);
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(data);
//
//                        String Topbar = jsonObject.optString("topbar");
//                        String Mode = jsonObject.optString("mode");
//                        String Address = jsonObject.optString("address");
//
//                        Intent intent = new Intent(HybridUi.this, NewActivity.class);
//                        intent.putExtra("Topbar", Topbar);
//                        intent.putExtra("Mode", Mode);
//                        intent.putExtra("Address", Address);
//                        startActivity(intent);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    function.onCallBack("Activity opened");
//
//                    //  function.onCallBack(new Gson().toJson(p));
//
//                }
//
//            });

//            mWebView.registerHandler("_app_activity_close", new BridgeHandler() {
//
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    Log.i(LOGTAG, "handler = _app_activity_close");
//                    finish();
//                }
//
//            });
//
//            mWebView.registerHandler("_app_activity_set_title", new BridgeHandler() {
//
//                @Override
//                public void handler(String title, CallBackFunction function) {
//                    JSONObject jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(title);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    String tit = jsonObject.optString("title");
//                    //  title_TextView.setText(tit);
//                    //  Log.i(TAG, "handler = submitFromWeb, data from web = " + tit);
//                    function.onCallBack("_app_activity_set_title, response data from Java :" + tit);
//
//                }
//
//            });
//            mWebView.addJavascriptInterface(new Object() {
////                @JavascriptInterface
////                public void hello1() {
////                }
//            },"AndroidWebView");

