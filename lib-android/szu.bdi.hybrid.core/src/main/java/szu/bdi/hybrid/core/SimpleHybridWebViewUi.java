package szu.bdi.hybrid.core;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

//@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class SimpleHybridWebViewUi extends HybridUi {

    final private static String LOGTAG = "SimpleHybridWebViewUi";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOGTAG, ".onCreate()");
        super.onCreate(savedInstanceState);

        //setTitle("setTitle()");

        final Context _ctx = this;

        JsBridgeWebView _wv = new JsBridgeWebView(_ctx);

        String address = HybridTools.optString(this.getUiData("address"));
        String url = "";
        if (address == null || "".equals(address)) {
            url = "file://" + HybridTools.getLocalWebRoot() + "error.htm";
        } else {
            if (address.matches("^\\w+://.*$")) {
                //if have schema already
                url = address;
            } else {
                //shuld be local?
                url = "file://" + HybridTools.getLocalWebRoot() + address;
            }
        }

        HybridTools.bindWebViewApi(_wv, this);

        setContentView(_wv);

        Log.v(LOGTAG, "load url=" + url);
        _wv.loadUrl(url);
    }
}
