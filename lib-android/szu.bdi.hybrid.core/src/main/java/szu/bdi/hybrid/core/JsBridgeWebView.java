package szu.bdi.hybrid.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
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
 * design : Q + Bi-direction-call + Protocol
 * <p/>
 * NOTES:
 * <p/>
 * 1, using Q to make sure runing in a same ui-thread.
 * 2, registerHandler() and callHandler() are the public protocol (default handler is removed)
 * 3, the _app2js(), _js2app(), _fetchQueue() are the hidden protocol
 */

@SuppressLint("SetJavaScriptEnabled")
public class JsBridgeWebView extends HybridWebView {

    private final String TAG = "JsBridgeWebView";

    final static String JSB1_OVERRIDE_SCHEMA = "jsb1://";//v1
    final static String JSB1_RETURN_DATA = JSB1_OVERRIDE_SCHEMA + "return/";
    final static String JSB1_FETCH_QUEUE = JSB1_RETURN_DATA + "_fetchQueue/";

    final static String WEB_VIEW_JAVASCRIPT_BRIDGE = "WebViewJavascriptBridge";//v1
    final static String JS_FETCH_QUEUE_FROM_JAVA = "javascript:" + WEB_VIEW_JAVASCRIPT_BRIDGE + "._fetchQueue();";
    final static String JAVA_TO_JS = "javascript:" + WEB_VIEW_JAVASCRIPT_BRIDGE + "._app2js";
//    final static String CALLBACK_ID_FORMAT = "JAVA_CB_%s";

    //NOTES: 长期运行或者大功率运作似乎 responseCallbacks 会因为清理不及时内存泄漏。
    Map<String, ICallBackFunction> responseCallbacks = new HashMap<String, ICallBackFunction>();
    Map<String, ICallBackHandler> messageHandlers = new HashMap<String, ICallBackHandler>();

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
        //TODO hacking prompt.
//        @Override
//        public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result) {
//            // Unlike the @JavascriptInterface bridge, this method is always called on the UI thread.
//            String handledRet = parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
//            if (handledRet != null) {
//                result.confirm(handledRet);
//            } else {
//                dialogsHelper.showPrompt(message, defaultValue, new CordovaDialogsHelper.Result() {
//                    @Override
//                    public void gotResult(boolean success, String value) {
//                        if (success) {
//                            result.confirm(value);
//                        } else {
//                            result.cancel();
//                        }
//                    }
//                });
//            }
//            return true;
//        }
    }

    class MyWebViewClient extends WebViewClient {
        Context _ctx = null;

        public MyWebViewClient(Context context) {
            this._ctx = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            JsBridgeWebView webView = JsBridgeWebView.this;

            if (url.startsWith(JSB1_RETURN_DATA)) {
                // app2js callback
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
        public void onPageFinished(WebView view, String url) {
            webViewLoadLocalJs(view, "WebViewJavascriptBridge.js");

            JsBridgeWebView webView = JsBridgeWebView.this;

            if (webView.startupJsb1Msg != null) {
                //if something is called before the page is loaded, do them now...
                for (Jsb1Msg m : startupJsb1Msg) {
                    webView.dispatchMessage(m);
                }
                //clear it
                webView.startupJsb1Msg = null;
            }
            super.onPageFinished(view, url);
        }


        // <input type=file> support:
        // openFileChooser() is for pre KitKat and in KitKat mr1 (it's known broken in KitKat).
        // For Lollipop, we use onShowFileChooser().
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            this.openFileChooser(uploadMsg, "*/*");
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            this.openFileChooser(uploadMsg, acceptType, null);
        }

        public void openFileChooser(final ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
//                parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
//                    @Override
//                    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//                        Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
//                        Log.d(LOG_TAG, "Receive file chooser URL: " + result);
//                        uploadMsg.onReceiveValue(result);
//                    }
//                }, intent, FILECHOOSER_RESULTCODE);
        }

        //@Override //??
        //TODO
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
            Intent intent = fileChooserParams.createIntent();
//                try {
//                    parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
//                        @Override
//                        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//                            Uri[] result = WebChromeClient.FileChooserParams.parseResult(resultCode, intent);
//                            Log.d(LOG_TAG, "Receive file chooser URL: " + result);
//                            filePathsCallback.onReceiveValue(result);
//                        }
//                    }, intent, FILECHOOSER_RESULTCODE);
//                } catch (ActivityNotFoundException e) {
//                    Log.w("No activity found to handle file chooser intent.", e);
//                    filePathsCallback.onReceiveValue(null);
//                }
            return true;
        }

    }

    //msg Q before page loaded.
    public List<Jsb1Msg> startupJsb1Msg = new ArrayList<Jsb1Msg>();

    //get the target function name.
    public static String parseFunctionName(String jsUrl) {
        //example
        //javascript:jsb1://WebViewJavascriptBridge._fetchQueue(...);
        // => _fetchQueue
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
//
//    public List<Jsb1Msg> getStartupJsb1Msg() {
//        return startupJsb1Msg;
//    }

//    public void setStartupJsb1Msg(List<Jsb1Msg> startupJsb1Msg) {
//        this.startupJsb1Msg = startupJsb1Msg;
//    }

    private long uniqueId = 0;

    public JsBridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JsBridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    class androidjsb {

        @JavascriptInterface
        public String getVersion(String p1) {
            return p1;
        }
    }

    public JsBridgeWebView(final Context context) {
        super(context);
        init(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //try this new way
            this.addJavascriptInterface(new Object() {

                @JavascriptInterface
                public String js2app(final String callBackId, String handlerName, final String param_s) {

                    final ICallBackFunction responseFunction = new ICallBackFunction() {
                        @Override
                        public void onCallBack(final String data_s) {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Jsb1Msg responseMsg = new Jsb1Msg();
                                    responseMsg.setResponseId(callBackId);
                                    responseMsg.setResponseData(data_s);
                                    String s = responseMsg.toJson();
                                    //quick hack
                                    if ("".equals(s) || s == null) s = "null";
                                    Log.v(TAG, "s ==> " + s);
                                    loadUrl(JAVA_TO_JS + "(" + s + ");");
                                    //loadUrl("javascript:console.log(o2s("+s+"));");
                                }
                            });
                        }
                    };
                    final ICallBackHandler handler = messageHandlers.get(handlerName);

                    //TODO 这里要有个 auth-mapping (whitelist) check?
                    if (handler != null) {
                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.handler(param_s, responseFunction);
                            }
                        })).start();
                    }
                    return callBackId;
                }
            }, "nativejsb");
        }
    }

    private void init(Context context) {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        this.setWebViewClient(new MyWebViewClient(context));
        this.setWebChromeClient(new MyWebChromeClient(context));
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

//    private void _app2js(String handlerName, String data, ICallBackFunction responseCallback) {
//        Jsb1Msg m = new Jsb1Msg();
//        if (!TextUtils.isEmpty(data)) {
//            m.setDataStr(data);
//        }
//        if (responseCallback != null) {
//            String callbackStr = String.format(CALLBACK_ID_FORMAT, ++uniqueId + ("_" + SystemClock.currentThreadTimeMillis()));
//            responseCallbacks.put(callbackStr, responseCallback);
//            m.setCallbackId(callbackStr);
//        }
//        if (!TextUtils.isEmpty(handlerName)) {
//            m.setHandlerName(handlerName);
//        }
//        queueMessage(m);
//    }

    private void queueMessage(Jsb1Msg m) {
        if (startupJsb1Msg != null) {
            startupJsb1Msg.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    void dispatchMessage(Jsb1Msg m) {
        String s = m.toJson();

        //quick hack
        if ("".equals(s) || s == null) s = "null";

        //NOTES: run the js in the main thread of browser:
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(JAVA_TO_JS + "(" + s + ");");
        }
    }

    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {

            //call the _fetchQueue()
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
                        if (m == null) {
                            Log.w(TAG, "??? m==null ???");
                            continue;
                        }
                        String responseId = m.getResponseId();

                        if (!TextUtils.isEmpty(responseId)) {
                            //if has reponseId, find the callback
                            ICallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            //after call, clean it up
                            responseCallbacks.remove(responseId);
                        } else {
                            //new call or a callback from js
                            ICallBackFunction responseFunction = null;
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                //it is a callback from js
                                responseFunction = new ICallBackFunction() {
                                    @Override
                                    public void onCallBack(String data_s) {
                                        Jsb1Msg responseMsg = new Jsb1Msg();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data_s);
                                        queueMessage(responseMsg);
                                    }
                                };
                            }
                            ICallBackHandler handler = null;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                                //TODO 这里要有个 auth-mapping (whitelist) check?
                                if (handler != null) {
                                    handler.handler(m.getDataStr(), responseFunction);
                                }
                            } else {
                                Log.w(TAG, "??? what the hell m= " + m.toString());
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

    //from java call js...
//    public void callHandler(String handlerName, String data_s, ICallBackFunction cb) {
//        _app2js(handlerName, data_s, cb);
//    }

    //prototol(java<=>js)
    public static class Jsb1Msg {

        private String callbackId; //callbackId
        private String responseId; //responseId
        private String responseData; //responseData
        private String dataStr; //dataStr of message
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

        public String getDataStr() {
            return dataStr;
        }

        public void setDataStr(String data) {
            this.dataStr = data;
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
                jsonObject.put(DATA_STR, getDataStr());
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
                    m.setDataStr(jsonObject.has(DATA_STR) ? jsonObject.getString(DATA_STR) : null);
                    list.add(m);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }
    }
}
