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

        JsBridgeWebView _wv = new JsBridgeWebView(_ctx);

        //NOTES: if not set, the js alert won't effect...(maybe the default return is true)
        _wv.setWebChromeClient(new WebChromeClient() {
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

        //IMPORTANT:
        HybridTools.bindWebViewApi(_wv, this);

        setContentView(_wv);

        Log.v(LOGTAG, "load url=" + url);
        _wv.loadUrl(url);

//        final JsBridgeWebView wv = _wv;
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setContentView(wv);
//            }
//        }, 111);
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
