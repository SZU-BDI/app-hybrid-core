package szu.bdi.hybrid.core.eg;

import android.util.Log;

import org.json.JSONObject;

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.ICallBackFunction;
import szu.bdi.hybrid.core.ICallBackHandler;
import szu.bdi.hybrid.core.JSO;

//just an example Api return {STS:TODO} only

public class ApiTestTODO extends HybridApi {
    public ICallBackHandler getHandler() {
        return new ICallBackHandler() {
            @Override
            public void handler(String dataStr, ICallBackFunction cb) {
                //HybridUi callerAct = getCallerUi();

                Log.v("_app_test_todo", dataStr);

                JSO data_o = JSO.s2o(dataStr);

//                if (data_o != null) {
//                    String url = ((JSONObject) data_o.getValue()).optString("url");
//                    if (!HybridTools.isEmptyString(url)) {
//                        String rt_s = HybridTools.webPost(url, "");
//                        Log.v("_app_test_todo", url + " => " + rt_s);
//                        cb.onCallBack("{\"STS\":\"URL\",\"len\":\"" + HybridTools.getStrLen(rt_s) + "\"}");
//                        return;
//                    }
//                }
                cb.onCallBack("{\"STS\":\"TODO\"}");
            }
        };
    }
}
