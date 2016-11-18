package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressLint("SetJavaScriptEnabled")
public class JsBridgeWebView extends WebView {

    private final static String RESPONSE_ID_STR = "responseId";
    private final static String RESPONSE_DATA_STR = "responseData";

//    private final static String CALLBACK_ID_STR = "callbackId";
//    private final static String DATA_STR = "data";
//    private final static String HANDLER_NAME_STR = "handlerName";

    private final String LOGTAG = "JsBridgeWebView";

    Map<String, HybridHandler> messageHandlers = new HashMap<String, HybridHandler>();

    class nativejsb {
        private Context _context;

        public nativejsb(Context context) {
            _context = context;
        }

        @JavascriptInterface
        public String getVersion(){
            return "20161116";
        }
        @JavascriptInterface
        public String js2app(final String callBackId, String handlerName, final String param_s) {


            //TODO 这里要有个 auth-mapping (whitelist) check!!!!


            final HybridCallback responseFunction = new HybridCallback() {
                @Override
                public void onCallBack(final String data_s) {
                    ((Activity) _context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSO msg = new JSO();
                            msg.setChild(RESPONSE_ID_STR, callBackId);
                            msg.setChild(RESPONSE_DATA_STR, data_s);
                            String s = msg.toString();
                            if ("".equals(s) || s == null) s = "null";
                            Log.v(LOGTAG, "js2app s ==> " + s);
                            loadUrl("javascript:setTimeout(function(){WebViewJavascriptBridge._app2js(" + s + ");},1);");
                        }
                    });
                }

                @Override
                public void onCallBack(JSO jso) {
                    onCallBack(JSO.o2s(jso));
                }

            };
            final HybridHandler handler = messageHandlers.get(handlerName);

            if (handler != null) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.handler(param_s, responseFunction);
                    }
                })).start();
            }
            return "OK";
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        Context _ctx = null;

        public MyWebChromeClient(Context context) {
            this._ctx = context;
        }

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

    }

    class MyWebViewClient extends WebViewClient {
        Context _ctx = null;

        public MyWebViewClient(Context context) {
            this._ctx = context;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //inject
            String jsContent = HybridTools.readAssetInStr(view.getContext(), "WebViewJavascriptBridge.js");

            //NOTES: no need to runOnUiThread() here...
            view.loadUrl("javascript:" + jsContent);

            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //TODO keep checking if XXX is ready to inject the jsb?
        }
        //NOTES
        //for <input type=file/> we suggest to give it up. using api to invoke activity to handle it...
        //which means the page need to call the jsb for the api by yourself ;)
    }

    public JsBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JsBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public JsBridgeWebView(Context context) {
        super(context);
        init(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.addJavascriptInterface(new nativejsb(context), "nativejsb");
        } else {
            HybridTools.quickShowMsg(context, "Your android is too low version");
        }
    }

    private void init(Context context) {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
        this.setWebViewClient(new MyWebViewClient(context));
        this.setWebChromeClient(new MyWebChromeClient(context));
    }

    public void registerHandler(String handlerName, HybridHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

}
