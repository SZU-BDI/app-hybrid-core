package szu.bdi.hybrid.core.eg;

//TODO 设计要改，应该是 HybridUi => (NativeUi + XXXWebViewUi)
//TODO 其中 RawWebViewUi 是指不支持 API 的 inAppBrowser
//TODO 其中 SimpleHybridWebViewUi 是指简单全屏（TopBar可控）的支持API的

//SimpleWebViewUi is a HybridUi with a full size "JBridgeWebView"

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.HybridUi;
import szu.bdi.hybrid.core.HybridWebView;

//@ref http://stackoverflow.com/questions/20138434/alternate-solution-for-setjavascriptenabledtrue
@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
public class SimpleWebViewUi extends HybridUi {

    final private static String LOGTAG = "SimpleWebViewUi";

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(LOGTAG, ".onCreate()");
        super.onCreate(savedInstanceState);

        //setTitle("setTitle()");

        final Context _ctx = this;

//        JsBridgeWebView _wv = new JsBridgeWebView(_ctx);
        HybridWebView _wv=new HybridWebView(_ctx);

        String address = HybridTools.optString(this.getUiData("address"));
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

        HybridTools.bindWebViewApi(_wv, this);

        setContentView(_wv);

        Log.v(LOGTAG, "load url=" + url);
        _wv.loadUrl(url);
    }
}
