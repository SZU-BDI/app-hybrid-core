package szu.bdi.hybrid.core;

import android.util.Log;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import szu.bdi.hybrid.core.HybridUi;

import org.json.JSONException;
import org.json.JSONObject;


public class HybridService {
    public static HybridUi getHybridUi(String uiName) {
//
//        //        mWebView.registerHandler("_app_activity_close", new BridgeHandler() {
//
//        @Override
//        public void handler(String data, CallBackFunction function) {
//            Log.i(TAG, "handler = _app_activity_close");
//            finish();
//        }
//
//    });
//
//    mWebView.registerHandler("_app_activity_set_title", new BridgeHandler() {
//
//        @Override
//        public void handler(String title, CallBackFunction function) {
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(title);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
    return null;
    }

    public static HybridApi getHybridApi(String uiName) {
        //HybridUi ui=new HybridUi();
        return null;
    }
}
