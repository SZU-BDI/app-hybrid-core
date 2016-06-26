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

import org.json.JSONException;
import org.json.JSONObject;

public class HybridUi extends Activity {
    private static String LOGTAG = "HybridUi";

    JSONObject _uiData;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent iin = getIntent();
        String s_uiData = iin.getStringExtra("uiData");
        initUiData(HybridTools.s2o(s_uiData));
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
            ActionBar actionBar = getActionBar();
            //NOTES: setDisplayHomeAsUpEnabled make onOptionsItemSelected() work
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ex) {
        } catch (NoSuchMethodError ex) {
        }
    }

    //NOTES: when user click the left-upper button on the top bar
    //@ref setDisplayHomeAsUpEnabled()
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    //in case old androids dont have onBackPress(), need onKeyDown() to do it
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.v(LOGTAG, "onKeyDown " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.v(LOGTAG, "onKeyDown KeyEvent.KEYCODE_BACK " + KeyEvent.KEYCODE_BACK);

            Boolean reallyClose = true;

//TODO not yet think up good solution to make a global decision.... TODO !!!
//            reallyClose = HybridTools.ifLastActPromptUser(this);
//            Application me = HybridTools.getApplication();
            //me.callFunction(...)

            if (reallyClose)
                onBackPressed();
            return reallyClose;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initUiData(JSONObject o) {
        if (o == null) return;
        _uiData = o;
//        Log.v(LOGTAG, "initUiData _uiData=" + _uiData);
    }

    public JSONObject wholeUiData() {
//        Log.v(LOGTAG, "wholeUiData _uiData=" + _uiData);
        return _uiData;
    }

    public void setUiData(String k, Object v) {
        try {
            _uiData.put(k, v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Object getUiData(String k) {
        if (null == _uiData) return null;
        Log.v(LOGTAG, " _uiData=" + _uiData);
        Log.v(LOGTAG, " getUiData " + k + "=>" + _uiData.opt(k));
        return _uiData.opt(k);
    }

    protected ICallBackFunction _cb = null;

    public void setCallBackFunction(ICallBackFunction cb) {
        _cb = cb;
    }

    //@ref this.startActivityForResult() + (setResult() + finish())
    protected void onActivityResult(int requestCode, int resultCode, Intent rtIntent) {
        Log.v(LOGTAG, "resultCode=" + resultCode);
        if (rtIntent != null) {
            Log.v(LOGTAG, "rtIntent.getStringExtra(rt)=" + rtIntent.getStringExtra("rt"));
            if (_cb != null && resultCode > 0) {
                Log.v(LOGTAG, "onCallBack OK");
                _cb.onCallBack(rtIntent.getStringExtra("rt"));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.v(LOGTAG, "onResume ");

    }
}
