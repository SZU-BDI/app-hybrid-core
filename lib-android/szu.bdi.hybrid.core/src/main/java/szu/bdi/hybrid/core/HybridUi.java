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

import org.json.JSONException;
import org.json.JSONObject;

public class HybridUi extends Activity {
    private static String LOGTAG = "HybridUi";

    JSONObject _uiData;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            onBackPressed();
            return true;
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

    protected JsBridgeWebView.ICallBackFunction _cb = null;

    //@ref this.startActivityForResult() + (setResult() + finish())
    protected void onActivityResult(int requestCode, int resultCode, Intent rtIntent) {
        Log.v(LOGTAG, "resultCode=" + resultCode);
        if (rtIntent != null) {
            Log.v(LOGTAG, "rtIntent.getStringExtra(rt)=" + rtIntent.getStringExtra("rt"));
            if (_cb != null && resultCode > 0) {
                Log.v(LOGTAG, "onCallBack OK");
//            _cb.onCallBack("{\"STS\":\"OK\"}");//OK
//            _cb.onCallBack("{STS:\"OK\"}");//OK
                //_cb.onCallBack("What the hell");//KO, proves the result need the JSON format
                _cb.onCallBack(rtIntent.getStringExtra("rt"));
//            _cb.onCallBack("{STS:\"TODO\"}");//TODO return the param of current Ui?
            }
        }
    }
}
