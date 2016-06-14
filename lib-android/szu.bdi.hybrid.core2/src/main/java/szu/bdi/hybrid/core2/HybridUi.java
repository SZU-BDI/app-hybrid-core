package szu.bdi.hybrid.core2;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class HybridUi extends Activity {
    final private static String LOGTAG = "HybridUi";

    public JSONObject uiData = new JSONObject();

    public JSONObject initPageData(String s) {
        uiData = HybridTools.s2o(s);
        Log.v(LOGTAG, "uiData=" + uiData.toString());
        return uiData;
    }

    public void setUiData(String k, Object v) {
        try {
            uiData.put(k, v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Object getUiData(String k) {
        if (null == uiData) uiData = new JSONObject();
        return uiData.opt(k);
    }

    //    Intent _intent = null;
//    Context _ctx = null;

//    public void show(Context ctx) {
////        _ctx = ctx;
////        Intent intent = new Intent(_ctx, HybridUiActivity.class);
//////intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
////        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        _intent = intent;
////        _ctx.startActivity(intent);
//        _ctx = ctx;
//        Intent intent = new Intent(_ctx, this.getClass());
////intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        _intent = intent;
//        _ctx.startActivity(intent);
//    }
//
//    public void show() {
//        this.show(HybridTools.getAppContext());
//    }

}
