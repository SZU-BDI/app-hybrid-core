package szu.bdi.hybrid.core.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This just the v1 implementation,
 * soon will have a v2 version for a better protocol
 */

@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView {

    private final String TAG = "BridgeWebView";

    //NOTES:  TODO memory cleanup?
    Map<String, ICallBackFunction> responseCallbacks = new HashMap<String, ICallBackFunction>();
    Map<String, IBridgeHandler> messageHandlers = new HashMap<String, IBridgeHandler>();
    IBridgeHandler defaultHandler = new IBridgeHandler() {
        @Override
        public void handler(String data, ICallBackFunction function) {
            if (function != null) {
                function.onCallBack("Default handler response data");
            }
        }
    };

    private List<Jsb1Msg> startupJsb1Msg = new ArrayList<Jsb1Msg>();

    public List<Jsb1Msg> getStartupJsb1Msg() {
        return startupJsb1Msg;
    }

    public void setStartupJsb1Msg(List<Jsb1Msg> startupJsb1Msg) {
        this.startupJsb1Msg = startupJsb1Msg;
    }

    private long uniqueId = 0;

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BridgeWebView(Context context) {
        super(context);
        init();
    }

//    public void setDefaultHandler(IBridgeHandler handler) {
//        this.defaultHandler = handler;
//    }

    private void init() {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    url = URLDecoder.decode(url, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                BridgeWebView webView = BridgeWebView.this;

                if (url.startsWith(BridgeUtil.JSB1_RETURN_DATA)) {
                    webView.handlerReturnData(url);
                    return true;
                } else if (url.startsWith(BridgeUtil.JSB1_OVERRIDE_SCHEMA)) { //
                    webView.flushMessageQueue();
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                BridgeUtil.webViewLoadLocalJs(view, "WebViewJavascriptBridge.js");

                BridgeWebView webView = BridgeWebView.this;

                if (webView.getStartupJsb1Msg() != null) {
                    for (Jsb1Msg m : webView.getStartupJsb1Msg()) {
                        webView.dispatchMessage(m);
                    }
                    webView.setStartupJsb1Msg(null);
                }
            }
            //
            //    @Override
            //    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //        super.onReceivedError(view, errorCode, description, failingUrl);
            //    }
        });
    }

    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        ICallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    public void send(String data, ICallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    private void doSend(String handlerName, String data, ICallBackFunction responseCallback) {
        Jsb1Msg m = new Jsb1Msg();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + ("_" + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    private void queueMessage(Jsb1Msg m) {
        if (startupJsb1Msg != null) {
            startupJsb1Msg.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    void dispatchMessage(Jsb1Msg m) {
        String s = m.toJson();

        //escape special characters for json string
        s = s.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2")
                .replaceAll("(?<=[^\\\\])(\")", "\\\\\"");

        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, s);
        //run the js in the main thread of browser:
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new ICallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage
                    List<Jsb1Msg> list = null;
                    try {
                        list = Jsb1Msg.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Jsb1Msg m = list.get(i);
                        String responseId = m.getResponseId();

                        if (!TextUtils.isEmpty(responseId)) {
                            ICallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            ICallBackFunction responseFunction = null;
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new ICallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Jsb1Msg responseMsg = new Jsb1Msg();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new ICallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            IBridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null) {
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }

    public void loadUrl(String jsUrl, ICallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    public void registerHandler(String handlerName, IBridgeHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

//    public void callHandler(String handlerName, String data, ICallBackFunction callBack) {
//        doSend(handlerName, data, callBack);
//    }

}
