    package com.hybrid.core;

    import android.annotation.SuppressLint;
    import android.app.Activity;
    import android.app.AlertDialog;
    import android.app.ProgressDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Message;
    import android.util.Log;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.webkit.JavascriptInterface;
    import android.webkit.JsResult;
    import android.webkit.WebChromeClient;
    import android.webkit.WebSettings;
    import android.webkit.WebView;
    import android.widget.Button;

    import com.github.lzyzsd.jsbridge.BridgeHandler;
    import com.github.lzyzsd.jsbridge.BridgeWebView;
    import com.github.lzyzsd.jsbridge.CallBackFunction;
    import com.github.lzyzsd.jsbridge.DefaultHandler;
   // import com.google.android.gms.appindexing.Action;
   // import com.google.android.gms.appindexing.AppIndex;
   // import com.google.android.gms.common.api.GoogleApiClient;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.lang.ref.WeakReference;

    /**
     * Created by Muhammad Azhar on 6/5/2016.
     */
    public class NewActivity extends Activity{
        public String printJson;
        private String mURL;
        private String text;
        private Handler handler;
        private BridgeWebView mWebView;
        private String TAG = "sdf";
        private long exitTime = 0;
        private ProgressDialog progressDialog;
        private Activity selfActivity = this;
        private Handler mHandler;

        public static boolean isruning = false;//用于判断当前activity是否是active的。因为遇到异常android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@41e29c18
        /**
         * ATTENTION: This was auto-generated to implement the App Indexing API.
         * See https://g.co/AppIndexing/AndroidStudio for more information.
         */
       // private GoogleApiClient client;
        // is not valid; is your activity running?



        @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            String Topbar,Mode,Address;
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    Topbar= null;
                    Mode= null;
                    Address= null;
                } else {
                    Topbar= extras.getString("Topbar");
                    Mode= extras.getString("Mode");
                    Address= extras.getString("Address");
                }
            } else {
                Topbar= (String) savedInstanceState.getSerializable("Topbar");
                Mode= (String) savedInstanceState.getSerializable("Mode");
                Address= (String) savedInstanceState.getSerializable("Address");
            }


            if(Topbar.equals("N"))
            {
                this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }


            setContentView(R.layout.activity_newactivity);




            mWebView = (BridgeWebView) findViewById(R.id.webView1);

       if(!Mode.equals("Native")) {
//add all webview code here from UIRoot activity
           mURL= Address;





           Log.d(TAG, "----------url-------=" + mURL);
           progressDialog = new ProgressDialog(this);
        //   progressDialog.setTitle(getText(R.string.Login_reminder_information));
         //  progressDialog.setMessage(getText(R.string.text_progressdialog_open_website));
           progressDialog.setCanceledOnTouchOutside(false);





           mWebView.setDefaultHandler(new DefaultHandler());


           mWebView.getSettings().setJavaScriptEnabled(true);
           WebSettings mWebSettings = mWebView.getSettings();
           //设置了缓存也看不出变化。。。
       /* mWebSettings.setAppCacheEnabled(true);// 设置启动缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 设置缓存模式*/
           mWebSettings.setSupportZoom(true);//设置支持缩放
           mWebSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
           mWebSettings.setBuiltInZoomControls(true);//设置出现缩放工具
        /*mWebSettings.setDomStorageEnabled(true);*/

           //https://developer.android.com/guide/topics/manifest/uses-sdk-element.html#ApiLevels
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {//11
               mWebSettings.setDisplayZoomControls(false);
           }
           mWebSettings.setUseWideViewPort(true);//支持任意比例缩放


           if (mWebView != null) {

               mWebView.setWebChromeClient(new WebChromeClient() {
                   @Override
                   public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                       //if (isruning)
                       {
                           AlertDialog.Builder b2 = new AlertDialog.Builder(NewActivity.this);
                           b2.setMessage(message)
                                   .setPositiveButton("ok", new AlertDialog.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           result.confirm();
                                       }
                                   });
                       /* b2.setNegativeButton("cacel", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        });*/

                        /*b2.setCancelable(false);*/
                           b2.create();
                           b2.show();

                       }
                       return true;
                   }
               });


               mWebView.loadUrl(mURL);

               mWebView.addJavascriptInterface(new JavaScriptInterface(this), "AndroidWebView");
           }



           mWebView.registerHandler("_app_activity_close", new BridgeHandler() {

               @Override
               public void handler(String data, CallBackFunction function) {

                   Log.i(TAG, "handler = _app_activity_close");
                   finish();
               }

           });



           // ATTENTION: This was auto-generated to implement the App Indexing API.
           // See https://g.co/AppIndexing/AndroidStudio for more information.
         //  client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();





       }
            else
       {
           mWebView.setVisibility(View.INVISIBLE);
       }




        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data){
/*
            if (resultCode==1){
                finish();
            }
            */
        }

        @Override
        public void onStart() {
            super.onStart();
/*
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
           // client.connect();
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "OpenWebsite Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app deep link URI is correct.
                    Uri.parse("android-app://com.hybrid.core/http/host/path")
            );
            */
           // AppIndex.AppIndexApi.start(client, viewAction);
        }


        public class JavaScriptInterface {
            Context mContext1;

            // Instantiate the interface and set the context
            JavaScriptInterface(Context c) {
                mContext1 = c;
            }

            @JavascriptInterface
            public void _close_activity() {
                NewActivity.this.finish();
            }

        }

        class MHandler extends Handler {
            WeakReference<NewActivity> mActivity;

            MHandler(NewActivity activity) {
                mActivity = new WeakReference<NewActivity>(activity);
            }

            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {


                }
            }


        }

        @Override
        public void onDestroy() {
            super.onDestroy();
//        WorkService.delHandler(mHandler);
            mHandler = null;
        }


    }
