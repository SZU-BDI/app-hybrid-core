package szu.bdi.hybrid.core;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

public class HybridUi extends Activity {
    public JSONObject pageData = new JSONObject();

    public JSONObject initPageData(String s) {
        pageData = HybridTools.s2o(s);
        return pageData;
    }

    public void setUiData(String k, Object v) {
        try {
            pageData.put(k, v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Object getUiData(String k) {
        return pageData.opt(k);
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
