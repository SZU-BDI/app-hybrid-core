package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;

//TODO bugfix the jsbridge...
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.DefaultHandler;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGTAG, ".onCreate()");

        mURL = "file:///android_asset/root.htm";

        //TODO
        //NOTES: the github.lzyzsd.jsbridge need to fix for few bugs, such as
        //1, the callback must be string, but we need more flexible json
        //2,
        mWebView = new BridgeWebView(this);

        setContentView(mWebView);

        mWebView.setDefaultHandler(new DefaultHandler());//TODO to improve...

        WebSettings mWebSettings = mWebView.getSettings();

        mWebSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= 11) {
            mWebSettings.setDisplayZoomControls(false);
        }

        mWebView.addJavascriptInterface(new JavaScriptInterface(this),
                "AndroidWebView");//TODO to improve

        mWebView.loadUrl(mURL);
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

