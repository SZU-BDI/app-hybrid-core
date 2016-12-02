package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import info.cmptech.JSO;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class JsBridgeWebView extends WebView {

    private final static String RESPONSE_ID_STR = "responseId";
    private final static String RESPONSE_DATA_STR = "responseData";

//    private final static String CALLBACK_ID_STR = "callbackId";
//    private final static String DATA_STR = "data";
//    private final static String HANDLER_NAME_STR = "handlerName";

    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();

    Map<String, HybridHandler> messageHandlers = new HashMap<String, HybridHandler>();

    public JsBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JsBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @SuppressLint("AddJavascriptInterface")
    public JsBridgeWebView(Context context) {
        super(context);
        init(context);

//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        this.addJavascriptInterface(new nativejsb(context), "nativejsb");
        //} else {
        // TODO limit for some security...
        //    //HybridTools.quickShowMsg(context, "Your android is too low version");
        //}
    }

    private void init(Context context) {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //  WebView.setWebContentsDebuggingEnabled(true);
        //}
        this.setWebViewClient(new MyWebViewClient(context));
        this.setWebChromeClient(new MyWebChromeClient(context));
    }

    public void registerHandler(String handlerName, HybridHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    class nativejsb {
        private Context _context;

        public nativejsb(Context context) {
            _context = context;
        }

        @JavascriptInterface
        public String getVersion() {
            return "20161119";
        }

        //NOTES: for native object injected into the webview, the parameters must be primitive.
        @JavascriptInterface
        public String js2app(final String callBackId, String handlerName, final String param_s) {

            final String uiName = ((HybridUi) _context).getUiData("name").toString();

            Log.v(LOGTAG, " js2app handlerName " + handlerName + " uiName " + uiName);

            //TODO !!!! 这里要有个 auth-mapping (url-regexp) check!!!!

            final HybridCallback responseFunction = new HybridCallback() {
//                @Override
//                public void onCallBack(final String data_s) {
//                    ((Activity) _context).runOnUiThread(new Runnable() {
//                        @TargetApi(Build.VERSION_CODES.KITKAT)
//                        @Override
//                        public void run() {
//                            JSO msg = new JSO();
//                            msg.setChild(RESPONSE_ID_STR, callBackId);
//                            msg.setChild(RESPONSE_DATA_STR, data_s);
//                            String s = msg.toString(true);
//                            if ("".equals(s) || s == null) s = "null";
//                            Log.v(LOGTAG, "js2app s ==> " + s);
//                            evaluateJavascript("WebViewJavascriptBridge._app2js(" + s + ");", new ValueCallback<String>() {
//                                @Override
//                                public void onReceiveValue(String value) {
//                                    Log.v(LOGTAG, " onReceiveValue " + value);
//                                }
//                            });
//                        }
//                    });
//                }

                @Override
                public void onCallBack(final JSO jso) {
                    ((Activity) _context).runOnUiThread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        @Override
                        public void run() {
                            JSO msg = new JSO();
                            msg.setChild(RESPONSE_ID_STR, callBackId);
                            //msg.setChild(RESPONSE_DATA_STR, jso.toString(true));
                            msg.setChild(RESPONSE_DATA_STR, jso);
                            String s = msg.toString(true);
                            if ("".equals(s) || s == null) s = "null";
                            Log.v(LOGTAG, "js2app s ==> " + s);
                            evaluateJavascript("WebViewJavascriptBridge._app2js(" + s + ");", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    Log.v(LOGTAG, " onReceiveValue " + value);
                                }
                            });
                        }
                    });
                    //onCallBack(JSO.o2s(jso));
                }

            };
            final HybridHandler handler = messageHandlers.get(handlerName);

            if (handler != null) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        handler.handler(JSO.s2o(param_s), responseFunction);
                    }
                })).start();
            } else {
                Log.v(LOGTAG, " not found registered handlerName " + handlerName + " uiName " + uiName);
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
            try {
                HybridTools.appAlert(_ctx, message, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
            } catch (Throwable th) {
                th.printStackTrace();
                result.confirm();
            }
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
        //TODO design a loading % bar in future
//        @Override
//        public void onProgressChanged(WebView view, int progress) {
//            super.onProgressChanged(view,progress);
//            // Do something cool here
//        }
    }

    class MyWebViewClient extends WebViewClient {
        Context _ctx = null;

        public MyWebViewClient(Context context) {
            this._ctx = context;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            notifyPollingInject(view, url);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            notifyPollingInject(view, url);
            super.onPageStarted(view, url, favicon);
        }

        public void notifyPollingInject(WebView view, String url) {
            //inject
            String jsContent = HybridTools.readAssetInStr("WebViewJavascriptBridge.js", true);

            //NOTES: no need to runOnUiThread() here...because called by onPageXXXX
            view.loadUrl("javascript:" + jsContent);
        }


        //NOTES
        //for <input type=file/> we suggest to give it up. using api to invoke activity to handle it...
        //which means the page need to call the jsb for the api by yourself ;)
    }

}
