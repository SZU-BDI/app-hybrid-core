package szu.bdi.hybrid.core;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.Map;

//TODO change the behavior to on/trigger.  and let the caller to do the decision later
public class HybridUi extends Activity {

    //    public static HybridUi tmpUiForLink = null;

    //TODO TMP UGLY SOLUTION...TO IMPROVE LATER !!!
    public static HybridUiCallback tmpUiCallback = null;

    private static String LOGTAG = "HybridUi";

    JSO _uiData;
    JSO _responseData;


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent iin = getIntent();
        String s_uiData = iin.getStringExtra("uiData");

        initUiData(JSO.s2o(s_uiData));

        Log.v(LOGTAG, "HybridUi onCreate() try push ");

        //Very ugly tmp solution. but it should working well, because app is very low thread conflict
        //for the open new UI.
        if (tmpUiCallback != null) {
            try {
                tmpUiCallback.onCallBack(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            HybridUi.tmpUiCallback = null;
        }
//        tmpUiForLink = this;

//        Activity parent = getParent();
////        if(parent instanceof)
//        HybridTools.addHybridUi(this);
//
//        //TODO debug now...
//        HybridTools.debugHybridUis();

        //getParent().notifyChild($id)

        //TODO 下面的搬去 setTopBar()

        //N: FullScreen + top status, Y: Have Bar + top status, M: only bar - top status, F: full screen - top status
        String topbar = HybridTools.optString(getUiData("topbar"));

        switch (topbar) {
            case "F":
                //F: full screen w- top status
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case "M":
                //M: only top bar w- top status
                requestWindowFeature(Window.FEATURE_ACTION_BAR);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case "N":
                //N: FullScreen w+ top status
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                break;
            case "Y":
            default:
                //Y: top bar w+ top status (default)
                requestWindowFeature(Window.FEATURE_ACTION_BAR);
                break;
        }

        try {
            //for some model of android
            ActionBar actionBar = getActionBar();
            //NOTES: setDisplayHomeAsUpEnabled make onOptionsItemSelected() work
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ex) {
        } catch (NoSuchMethodError ex) {
        }
    }


    public boolean closeUi(JSO resultJSO) {
        if (null != resultJSO) setResponseData(resultJSO);
        return closeUi();
    }

    public boolean closeUi() {

        JSO o = _responseData;
        if (_responseData == null) {
            //if not responseData default return {name: $name, address: adress} for caller reference only
            o = new JSO();
            o.setChild("name", getUiData("name"));
            o.setChild("address", getUiData("address"));
        }
        if (false == trigger("close", o)) {

            //if no handler from trigger, i need to close by self.
            finish();
            return true;//real closed at this call
        }
        return false;//didn't real close at this call
    }

    Map<String, HybridCallback> _cba = new HashMap<String, HybridCallback>();

    public void on(String eventName, HybridCallback cb) {
        Log.v(LOGTAG, "Hybrid.on( " + eventName + ")");
        _cba.remove(eventName);
        _cba.put(eventName, cb);
    }

    public boolean trigger(String eventName, JSO o) {
        HybridCallback cb = _cba.get(eventName);
        if (cb == null) {
            Log.v(LOGTAG, "trigger() found no handler for " + eventName);
            return false;//have no handler
        } else {
            cb.onCallBack(o);
            return true;//have handler...
        }
    }

    //NOTES: when user click the left-upper button on the top bar, then regard as closeUi...
    //@ref setDisplayHomeAsUpEnabled()
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.closeUi();
                break;
        }
        return true;
    }

    //in case old androids dont have onBackPress(), need onKeyDown() to do it
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.v(LOGTAG, "onKeyDown " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Boolean reallyClose = true;
//TODO
//            reallyClose = HybridTools.ifLastActThenPromptUser(this);

            if (reallyClose)
                this.closeUi();
            return reallyClose;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initUiData(JSO o) {
        if (o == null) return;
        _uiData = o;
        this.setUiData("_init_time_", JSO.s2o(HybridTools.isoDateTime()));
    }

    public JSO wholeUiData() {
        return _uiData;
    }

    public void setUiData(String k, JSO v) {
        _uiData.setChild(k, v);
//        try {
//            _uiData.put(k, v);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    public JSO getUiData(String k) {
        if (null == _uiData) return null;
        //Log.v(LOGTAG, "getUiData()  _uiData=" + _uiData);
//        Log.v(LOGTAG, " getUiData " + k + "=>" + _uiData.opt(k));
//        return _uiData.opt(k);
        return _uiData.getChild(k);
    }

    public void setResponseData(JSO jso) {
        _responseData = jso;
    }

    public JSO getResponseData() {
        return _responseData;
    }

    //deprecated, use trigger !!
    //protected HybridCallback _cb = null;
    //public void setCallBackFunction(HybridCallback cb) {
//        _cb = cb;
//    }

    //@ref this.startActivityForResult() + (setResult() + finish())
//    protected void onActivityResult(int requestCode, int resultCode, Intent rtIntent) {
//        Log.v(LOGTAG, "resultCode=" + resultCode);
//        if (rtIntent != null) {
//            Log.v(LOGTAG, "rtIntent.getStringExtra(rt)=" + rtIntent.getStringExtra("rt"));
//            if (_cb != null && resultCode > 0) {
//                Log.v(LOGTAG, "onCallBack OK");
//                _cb.onCallBack(rtIntent.getStringExtra("rt"));
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        this.closeUi();
    }

}
