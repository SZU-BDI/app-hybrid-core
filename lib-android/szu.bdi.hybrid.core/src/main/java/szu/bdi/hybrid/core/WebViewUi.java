package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

//TODO rewrite the jsbridge...

//@ref http://stackoverflow.com/questions/20138434/alternate-solution-for-setjavascriptenabledtrue
@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class WebViewUi extends HybridUi {

    final private static String LOGTAG = "WebViewUi";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOGTAG, ".onCreate()");
        super.onCreate(savedInstanceState);

        setTitle("TODO setTitle()");

        final Context _ctx = this;
        final Activity _activity = this;

        JsBridgeWebView mWebView;

        mWebView = HybridTools.BuildJsBridge(_ctx);

        //NOTES: if not set, the js alert won't effect...(maybe the default return is true)
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                HybridTools.appAlert(_ctx, message, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult jsrst) {
                HybridTools.appConfirm(_ctx, message, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsrst.confirm();
                    }
                }, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        jsrst.cancel();
                    }
                });
                return true;
            }
        });

        String address = HybridTools.optString(getUiData("address"));
        String url = "";
        if (address == null || "".equals(address)) {
            url = "file://" + HybridTools.localWebRoot + "error.htm";
        } else {
            if (address.matches("^\\w+://.*$")) {
                //if have schema already
                url = address;
            } else {
                //shuld be local?
                url = "file://" + HybridTools.localWebRoot + address;
            }
        }

        mWebView.registerHandler("_app_activity_close", new ICallBackHandler() {

            @Override
            public void handler(String data, ICallBackFunction cb) {
                Log.v(LOGTAG, "handler = _app_activity_close");
                //WebViewUi.this._cb = cb;
                WebViewUi.this.onBackPressed();
            }
        });
        mWebView.registerHandler("_app_activity_open", new ICallBackHandler() {

                    @Override
                    public void handler(String data, ICallBackFunction cb) {
                        Log.v("_app_activity_open", data);

                        WebViewUi.this._cb = cb;//store the cb for later callback, TODO any better way?
//                        JSONObject dataJSONObject = HybridTools.s2o(data);
//                        try {
//                            dataJSONObject.put("url", "file://" + HybridTools.localWebRoot + "root.htm");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
////                        HybridTools.startUi("UiContent", dataJSONObject.toString(), _activity, WebViewUi.class);

                        String root_htm_s = "root.htm";
                        Log.v(LOGTAG, "root_htm_s=" + root_htm_s);
                        //HybridTools.startUi("UiRoot", "{topbar:'N',address:'" + root_htm_s + "'}", _activity);
                        //if no name then name = UiContent...
                        String uiName = "UiContent";//default;
                        JSONObject data_o = HybridTools.s2o(data);
                        if (data_o != null) {
                            String t = data_o.optString("name");
                            if (!HybridTools.isEmptyString(t)) {
                                uiName = t;
                            }
                        }

                        HybridTools.startUi(uiName, data, _activity);
                    }

                }
        );

        Log.v(LOGTAG, "load url=" + url);
        mWebView.loadUrl(url);

        setContentView(mWebView);

    }

    @Override
    public void onBackPressed() {
        Log.v(LOGTAG, "onBackPressed set Result 1");

        //{name: $name, address: adress}
        JSONObject o = new JSONObject();
        try {
            o.put("name", getUiData("name"));
            o.put("address", getUiData("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent rtIntent = new Intent();
        rtIntent.putExtra("rt", o.toString());
        setResult(1, rtIntent);//@ref onActivityResult()
        finish();
    }

}
