package szu.bdi.hybrid.core;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class HybridUi extends Activity {
    private static String LOGTAG = "HybridUi";

    JSONObject _uiData;

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
        Log.v(LOGTAG, "save _uiData=" + _uiData);
        Log.v(LOGTAG, "save get " + k + "=>" + _uiData.opt(k));
        return _uiData.opt(k);
    }

}
