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


//NOTES: 长期运行或者大功率运作似乎 responseCallbacks 会因为清理不及时内存泄漏。

@SuppressLint("AddJavascriptInterface")
class MyJsCallbackObject extends Object {

    private String _callbackId = "";

    private int _status = 0;

    public MyJsCallbackObject(String cbId) {
        _callbackId = cbId;
    }

    @JavascriptInterface
    public String getCallbackId() {
        return _callbackId;
    }

    public void setStatus(int sts) {
        _status = sts;
    }

    @JavascriptInterface
    public int getStatus() {
        return _status;
    }

}


@SuppressLint("SetJavaScriptEnabled")
public class JsBridgeWebView extends WebView {

    private final String LOGTAG = "JsBridgeWebView";

    //final static String JSB1_OVERRIDE_SCHEMA = "jsb1://";//v1
    //final static String JSB1_RETURN_DATA = JSB1_OVERRIDE_SCHEMA + "return/";
    //final static String JSB1_FETCH_QUEUE = JSB1_RETURN_DATA + "_fetchQueue/";

    //final static String WEB_VIEW_JAVASCRIPT_BRIDGE = "WebViewJavascriptBridge";//v1
//    final static String JS_FETCH_QUEUE_FROM_JAVA = "javascript:" + WEB_VIEW_JAVASCRIPT_BRIDGE + "._fetchQueue();";

//    final static String JAVA_TO_JS = "javascript:WebViewJavascriptBridge._app2js";

//    final static String CALLBACK_ID_FORMAT = "JAVA_CB_%s";

    //    Map<String, HybridCallback> responseCallbacks = new HashMap<String, HybridCallback>();

    Map<String, HybridHandler> messageHandlers = new HashMap<String, HybridHandler>();

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
        //TODO hacking prompt...
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
//            JsBridgeWebView webView = JsBridgeWebView.this;

            //remove the old fashion
//            if (url.startsWith(JSB1_RETURN_DATA)) {
//                // app2js callback
//                webView.handlerReturnData(url);
//                return true;
//            } else if (url.startsWith(JSB1_OVERRIDE_SCHEMA)) {
//                // js2java call
//                //@ref WebViewJavascriptBridge.js  _js2java  __QUEUE_MESSAGE__
//                webView.flushMessageQueue();
//                return true;
//            } else {
//                return super.shouldOverrideUrlLoading(view, url);
//            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //inject
            webViewLoadLocalJs(view, "WebViewJavascriptBridge.js");

//            JsBridgeWebView webView = JsBridgeWebView.this;
//
//            if (webView.startupJsb1Msg != null) {
//                //if something is called before the page is loaded, do them now...
//                for (Jsb1Msg m : startupJsb1Msg) {
//                    webView.dispatchMessage(m);
//                }
//                //clear it
//                webView.startupJsb1Msg = null;
//            }
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

    //addJavascriptInterface

    //msg Q before page loaded.
//    public List<Jsb1Msg> startupJsb1Msg = new ArrayList<Jsb1Msg>();

//    //get the target function name.
//    public static String parseFunctionName(String jsUrl) {
//        //example
//        //javascript:jsb1://WebViewJavascriptBridge._fetchQueue(...);
//        // => _fetchQueue
//        return jsUrl.replace("javascript:" + WEB_VIEW_JAVASCRIPT_BRIDGE + ".", "").replaceAll("\\(.*\\);", "");
//    }

//    public static String getDataFromReturnUrl(String url) {
//        if (url.startsWith(JSB1_FETCH_QUEUE)) {
//            return url.replace(JSB1_FETCH_QUEUE, "");
//        }
//
//        String temp = url.replace(JSB1_RETURN_DATA, "");
//        String[] functionAndData = temp.split("/");
//
//        if (functionAndData.length >= 2) {
//            StringBuilder sb = new StringBuilder();
//            for (int i = 1; i < functionAndData.length; i++) {
//                sb.append(functionAndData[i]);
//            }
//            return sb.toString();
//        }
//        return null;
//    }
//
//    public static String getFunctionFromReturnUrl(String url) {
//        String temp = url.replace(JSB1_RETURN_DATA, "");
//        String[] functionAndData = temp.split("/");
//        if (functionAndData.length >= 1) {
//            return functionAndData[0];
//        }
//        return null;
//    }

    public static void webViewLoadLocalJs(WebView view, String path) {
        String jsContent = HybridTools.readAssetInStr(view.getContext(), path);
//        String jsContent = readJs(view.getContext(), path);
        view.loadUrl("javascript:" + jsContent);
//        view.evaluateJavascript(jsContent);
    }

//    public static String readJs(Context c, String urlStr) {
//        InputStream in = null;
//        try {
//            in = c.getAssets().open(urlStr);
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
//            String line = null;
//            StringBuilder sb = new StringBuilder();
//            do {
//                line = bufferedReader.readLine();
//                //NOTES:  skip comments //....
//                if (line != null && !line.matches("^\\s*\\/\\/.*")) {
//                    sb.append(line);
//                }
//            } while (line != null);
//
//            bufferedReader.close();
//            in.close();
//
//            return sb.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return null;
//    }
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

//    @SuppressLint("JavascriptInterface")
//    @Override
//    public void addJavascriptInterface(Object object, String name) {
//        if (object == null) {
//            return;
//        }
//        super.addJavascriptInterface(object, name);
//        WebViewCore.JSInterfaceData arg = new WebViewCore.JSInterfaceData();
//        arg.mObject = object;
//        arg.mInterfaceName = name;
//        mWebViewCore.sendMessage(EventHub.ADD_JS_INTERFACE, arg);
//    }


    public JsBridgeWebView(final Context context) {
        super(context);
        init(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            this.addJavascriptInterface(new Object() {
                @JavascriptInterface
                public Object js2app(final String callBackId, String handlerName, final String param_s) {

                    final MyJsCallbackObject rtObj=new MyJsCallbackObject(callBackId);

                    final HybridCallback responseFunction = new HybridCallback() {
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
                                    Log.v(LOGTAG, "js2app s ==> " + s);
                                    //暂时找了一大圈，看完了 JNI/CPP 层的代码，都没有什么好的方法。这个已经是暂时。。。最好的了。。。
                                    rtObj.setStatus(1);//research only..don't use
                                    //rtObj.setResultJsonStr(s);//not good...the js side need to decode it... give up..
                                    loadUrl("javascript:WebViewJavascriptBridge._app2js(" + s + ");");

                                    //loadUrl(JAVA_TO_JS + "(" + s + ");");
                                    //loadUrl("javascript:console.log(o2s("+s+"));");
                                }
                            });
                        }
                    };
                    final HybridHandler handler = messageHandlers.get(handlerName);

                    //TODO 这里要有个 auth-mapping (whitelist) check?
                    if (handler != null) {
                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.handler(param_s, responseFunction);
                            }
                        })).start();
                    }
                    return rtObj;
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

//    void handlerReturnData(String url) {
//        String functionName = getFunctionFromReturnUrl(url);
//        HybridCallback f = responseCallbacks.get(functionName);
//        String data = getDataFromReturnUrl(url);
//        if (f != null) {
//            f.onCallBack(data);
//            responseCallbacks.remove(functionName);
//            return;
//        }
//    }

//    private void _app2js(String handlerName, String data, HybridCallback responseCallback) {
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

//    private void queueMessage(Jsb1Msg m) {
//        if (startupJsb1Msg != null) {
//            startupJsb1Msg.add(m);
//        } else {
//            dispatchMessage(m);
//        }
//    }

//    void dispatchMessage(Jsb1Msg m) {
//        String s = m.toJson();
//
//        //quick hack
//        if ("".equals(s) || s == null) s = "null";
//
//        //NOTES: run the js in the main thread of browser:
//        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
//            loadUrl(JAVA_TO_JS + "(" + s + ");");
//        }
//    }

//    void flushMessageQueue() {
//        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
//
//            //call the _fetchQueue()
//            loadUrl(JS_FETCH_QUEUE_FROM_JAVA, new HybridCallback() {
//
//                @Override
//                public void onCallBack(String data) {
//                    // deserializeMessage
//                    List<Jsb1Msg> list = null;
//                    try {
//                        list = Jsb1Msg.toArrayList(data);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                    if (list == null || list.size() == 0) {
//                        return;
//                    }
//                    for (int i = 0; i < list.size(); i++) {
//                        Jsb1Msg m = list.get(i);
//                        if (m == null) {
//                            Log.w(LOGTAG, "??? m==null ???");
//                            continue;
//                        }
//                        String responseId = m.getResponseId();
//
//                        if (!TextUtils.isEmpty(responseId)) {
//                            //if has reponseId, find the callback
//                            HybridCallback function = responseCallbacks.get(responseId);
//                            String responseData = m.getResponseData();
//                            function.onCallBack(responseData);
//                            //after call, clean it up
//                            responseCallbacks.remove(responseId);
//                        } else {
//                            //new call or a callback from js
//                            HybridCallback responseFunction = null;
//                            final String callbackId = m.getCallbackId();
//                            if (!TextUtils.isEmpty(callbackId)) {
//                                //it is a callback from js
//                                responseFunction = new HybridCallback() {
//                                    @Override
//                                    public void onCallBack(String data_s) {
//                                        Jsb1Msg responseMsg = new Jsb1Msg();
//                                        responseMsg.setResponseId(callbackId);
//                                        responseMsg.setResponseData(data_s);
//                                        //queueMessage(responseMsg);
//                                        dispatchMessage(responseMsg);
//                                    }
//                                };
//                            }
//                            HybridHandler handler = null;
//                            if (!TextUtils.isEmpty(m.getHandlerName())) {
//                                handler = messageHandlers.get(m.getHandlerName());
//                                //TODO 这里要有个 auth-mapping (whitelist) check?
//                                if (handler != null) {
//                                    handler.handler(m.getDataStr(), responseFunction);
//                                }
//                            } else {
//                                Log.w(LOGTAG, "??? what the hell m= " + m.toString());
//                            }
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    public void loadUrl(String jsUrl, HybridCallback returnCallback) {
//        responseCallbacks.put(parseFunctionName(jsUrl), returnCallback);
//        this.loadUrl(jsUrl);
//    }

    public void registerHandler(String handlerName, HybridHandler handler) {
        if (handler != null) {
            messageHandlers.put(handlerName, handler);
        }
    }

    //from java call js...
    public void callHandler(String handlerName, String data_s, HybridCallback cb) {
        //_app2js(handlerName, data_s, cb);
        //TODO make msg_s = o2s({msg.handlerName, ....})
        //loadUrl("javascript:WebViewJavascriptBridge._app2js(" + s + ");");
    }

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
            JSO jso = new JSO();
            jso.setChild(CALLBACK_ID_STR, getCallbackId());
            jso.setChild(DATA_STR, getDataStr());
            jso.setChild(HANDLER_NAME_STR, getHandlerName());
            jso.setChild(RESPONSE_DATA_STR, getResponseData());
            jso.setChild(RESPONSE_ID_STR, getResponseId());

            return jso.toString();
        }

//        public static List<Jsb1Msg> toArrayList(String jsonStr) {
//            List<Jsb1Msg> list = new ArrayList<Jsb1Msg>();
//            //TODO jso.arrayAdd(JSO);
////            try {
////                JSO jso = new JSO(jsonStr);
////                for (int i = 0; i < jsonArray.length(); i++) {
////                    JSONObject jsonObject = jsonArray.getJSONObject(i);
////                    Jsb1Msg m = new Jsb1Msg();
////                    m.setHandlerName(jsonObject.has(HANDLER_NAME_STR) ? jsonObject.getString(HANDLER_NAME_STR) : null);
////                    m.setCallbackId(jsonObject.has(CALLBACK_ID_STR) ? jsonObject.getString(CALLBACK_ID_STR) : null);
////                    m.setResponseData(jsonObject.has(RESPONSE_DATA_STR) ? jsonObject.getString(RESPONSE_DATA_STR) : null);
////                    m.setResponseId(jsonObject.has(RESPONSE_ID_STR) ? jsonObject.getString(RESPONSE_ID_STR) : null);
////                    m.setDataStr(jsonObject.has(DATA_STR) ? jsonObject.getString(DATA_STR) : null);
////                    list.add(m);
////                }
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//            return list;
//        }
    }
}
