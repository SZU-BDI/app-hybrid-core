package szu.bdi.hybrid.core;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * This just the v1 implementation,
 * soon will have a v2 version for a better protocol
 * <p/>
 * design : Q + Bi-direction-call + Protocol(enc/dec)
 * <p/>
 * NOTES:
 * <p/>
 * 1, using Queue and make sure runing in a same ui-thread.
 * 2, registerHandler and callHandler is the only protocol
 */

@SuppressLint("SetJavaScriptEnabled")
public class JsBridgeWebView extends WebView {
    private final String TAG = "JsBridgeWebView";
    //    final private static String LOGTAG = "" + (new Object() {
//        public String getClassName() {
//            String clazzName = this.getClass().getName();
//            return clazzName.substring(0, clazzName.lastIndexOf('$'));
//        }
//    }.getClassName());

    final static String JSB1_OVERRIDE_SCHEMA = "jsb1://";//v1
    final static String JSB1_RETURN_DATA = JSB1_OVERRIDE_SCHEMA + "return/";
    final static String JSB1_FETCH_QUEUE = JSB1_RETURN_DATA + "_fetchQueue/";

    final static String WEB_VIEW_JAVASCRIPT_BRIDGE = "WebViewJavascriptBridge";//v1
    final static String JS_FETCH_QUEUE_FROM_JAVA = "javascript:" + WEB_VIEW_JAVASCRIPT_BRIDGE + "._fetchQueue();";
    final static String JAVA_TO_JS = "javascript:" + WEB_VIEW_JAVASCRIPT_BRIDGE + "._java2js";
    final static String CALLBACK_ID_FORMAT = "JAVA_CB_%s";

    Map<String, ICallBackFunction> responseCallbacks = new HashMap<String, ICallBackFunction>();
    Map<String, ICallBackHandler> messageHandlers = new HashMap<String, ICallBackHandler>();

    private List<Jsb1Msg> startupJsb1Msg = new ArrayList<Jsb1Msg>();

    public static String parseFunctionName(String jsUrl) {
        return jsUrl.replace("javascript:" + WEB_VIEW_JAVASCRIPT_BRIDGE + ".", "").replaceAll("\\(.*\\);", "");
    }

    public static String getDataFromReturnUrl(String url) {
        if (url.startsWith(JSB1_FETCH_QUEUE)) {
            return url.replace(JSB1_FETCH_QUEUE, "");
        }

        String temp = url.replace(JSB1_RETURN_DATA, "");
        String[] functionAndData = temp.split("/");

        if (functionAndData.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < functionAndData.length; i++) {
                sb.append(functionAndData[i]);
            }
            return sb.toString();
        }
        return null;
    }

    public static String getFunctionFromReturnUrl(String url) {
        String temp = url.replace(JSB1_RETURN_DATA, "");
        String[] functionAndData = temp.split("/");
        if (functionAndData.length >= 1) {
            return functionAndData[0];
        }
        return null;
    }

    public static void webViewLoadLocalJs(WebView view, String path) {
        String jsContent = readJs(view.getContext(), path);
        view.loadUrl("javascript:" + jsContent);
    }

    public static String readJs(Context c, String urlStr) {
        InputStream in = null;
        try {
            in = c.getAssets().open(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                //NOTES:  skip comments //....
                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();
            in.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public List<Jsb1Msg> getStartupJsb1Msg() {
        return startupJsb1Msg;
    }

    public void setStartupJsb1Msg(List<Jsb1Msg> startupJsb1Msg) {
        this.startupJsb1Msg = startupJsb1Msg;
    }

    private long uniqueId = 0;

    public JsBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JsBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public JsBridgeWebView(Context context) {
        super(context);
        init();
    }

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
                JsBridgeWebView webView = JsBridgeWebView.this;

                if (url.startsWith(JSB1_RETURN_DATA)) {
                    // java2js callback
                    webView.handlerReturnData(url);
                    return true;
                } else if (url.startsWith(JSB1_OVERRIDE_SCHEMA)) {
                    // js2java call
                    //@ref WebViewJavascriptBridge.js  _js2java  __QUEUE_MESSAGE__
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

                webViewLoadLocalJs(view, "WebViewJavascriptBridge.js");

                JsBridgeWebView webView = JsBridgeWebView.this;

                if (webView.getStartupJsb1Msg() != null) {
                    for (Jsb1Msg m : webView.getStartupJsb1Msg()) {
                        webView.dispatchMessage(m);
                    }
                    webView.setStartupJsb1Msg(null);
                }
            }
        });
    }

    void handlerReturnData(String url) {
        String functionName = getFunctionFromReturnUrl(url);
        ICallBackFunction f = responseCallbacks.get(functionName);
        String data = getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }

    private void _java2js(String handlerName, String data, ICallBackFunction responseCallback) {
        Jsb1Msg m = new Jsb1Msg();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(CALLBACK_ID_FORMAT, ++uniqueId + ("_" + SystemClock.currentThreadTimeMillis()));
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

        //NOTES: run the js in the main thread of browser:
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(JAVA_TO_JS + "(" + s + ");");
        }
    }

    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {

            //take the Q from javascript and handle in java
            loadUrl(JS_FETCH_QUEUE_FROM_JAVA, new ICallBackFunction() {

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
                            ICallBackHandler handler = null;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
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
        responseCallbacks.put(parseFunctionName(jsUrl), returnCallback);
        this.loadUrl(jsUrl);
    }

    public void registerHandler(String handlerName, ICallBackHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    //    from java call js
    public void callHandler(String handlerName, String data, ICallBackFunction cb) {
        _java2js(handlerName, data, cb);
    }

    //prototol(java,js)
    public static class Jsb1Msg {

        private String callbackId; //callbackId
        private String responseId; //responseId
        private String responseData; //responseData
        private String data; //data of message
        private String handlerName; //name of handler

        private final static String CALLBACK_ID_STR = "callbackId";
        private final static String RESPONSE_ID_STR = "responseId";
        private final static String RESPONSE_DATA_STR = "responseData";
        private final static String DATA_STR = "data";
        private final static String HANDLER_NAME_STR = "handlerName";

        public String getResponseId() {
            return responseId;
        }

        public void setResponseId(String responseId) {
            this.responseId = responseId;
        }

        public String getResponseData() {
            return responseData;
        }

        public void setResponseData(String responseData) {
            this.responseData = responseData;
        }

        public String getCallbackId() {
            return callbackId;
        }

        public void setCallbackId(String callbackId) {
            this.callbackId = callbackId;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getHandlerName() {
            return handlerName;
        }

        public void setHandlerName(String handlerName) {
            this.handlerName = handlerName;
        }

        public String toJson() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(CALLBACK_ID_STR, getCallbackId());
                jsonObject.put(DATA_STR, getData());
                jsonObject.put(HANDLER_NAME_STR, getHandlerName());
                jsonObject.put(RESPONSE_DATA_STR, getResponseData());
                jsonObject.put(RESPONSE_ID_STR, getResponseId());
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static List<Jsb1Msg> toArrayList(String jsonStr) {
            List<Jsb1Msg> list = new ArrayList<Jsb1Msg>();
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Jsb1Msg m = new Jsb1Msg();
                    m.setHandlerName(jsonObject.has(HANDLER_NAME_STR) ? jsonObject.getString(HANDLER_NAME_STR) : null);
                    m.setCallbackId(jsonObject.has(CALLBACK_ID_STR) ? jsonObject.getString(CALLBACK_ID_STR) : null);
                    m.setResponseData(jsonObject.has(RESPONSE_DATA_STR) ? jsonObject.getString(RESPONSE_DATA_STR) : null);
                    m.setResponseId(jsonObject.has(RESPONSE_ID_STR) ? jsonObject.getString(RESPONSE_ID_STR) : null);
                    m.setData(jsonObject.has(DATA_STR) ? jsonObject.getString(DATA_STR) : null);
                    list.add(m);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }
    }
}
