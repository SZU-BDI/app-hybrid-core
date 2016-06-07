package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    public class JavaScriptInterface {
        Context mContext1;

        JavaScriptInterface(Context c) {
            mContext1 = c;
        }
    }

    protected void temp1(CallBackFunction cb) {
//        final CallBackFunction _cb = cb;
        this._cb = cb;
        Log.v(LOGTAG, "temp1!!!!");

//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
                Intent intent = new Intent(HybridUi.this, TestActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
//            }
//        });
    }

    protected CallBackFunction _cb = null;//TODO !!!

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOGTAG, "onActivityResult !!!! " + resultCode);
        if (_cb != null && resultCode > 0) {
            Log.v(LOGTAG, "onActivityResult ???? " + resultCode);
            _cb.onCallBack(new Gson().toJson("AAAA"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, ".onCreate()");

        final Context _ctx = this;

        //mWebView = HybridService.BuildWebViewWithJsBridgeSupport(_ctx);//TODO

        mWebView = HybridService.BuildOldJsBridge(_ctx);

        //if not set, the js alert won't effect...(maybe the default return is true)
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Log.v(LOGTAG, "TODO onJsAlert " + url + "," + message);
                return false;
//                final String msg = message;
////                new Handler().postDelayed(new Runnable() {
////                    public void run() {
//                        AlertDialog.Builder b2;
//                        b2 = new AlertDialog.Builder(_ctx);
//                        b2.setMessage(msg).setPositiveButton("Close", new AlertDialog
//                                .OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                result.confirm();
//                            }
//                        });
//                       /* b2.setNegativeButton("cancel", new AlertDialog.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                result.cancel();
//                            }
//                        });*/
//                        /*b2.setCancelable(false);*/
//                        b2.create();
//                        b2.show();
////                    }
////                }, 500);
//                return true;
            }
        });

        setContentView(mWebView);

        mURL = "file:///android_asset/root.htm";
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
                        HybridUi.this.temp1(cb);
//                function.onCallBack("Activity opened");
                        //cb.onCallBack(new Gson().toJson("AAAA"));//TODO
                    }

                }

        );
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

}

//TODO Dynamic binding with the Srevice...

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

