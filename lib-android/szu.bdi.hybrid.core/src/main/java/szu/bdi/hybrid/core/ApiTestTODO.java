package szu.bdi.hybrid.core;

import android.util.Log;

import org.json.JSONObject;

//just an example Api return {STS:TODO} only

public class ApiTestTODO extends HybridApi {
    public ICallBackHandler getHandler() {
        return new ICallBackHandler() {
            @Override
            public void handler(String dataStr, ICallBackFunction cb) {
                //HybridUi callerAct = getCallerUi();

                Log.v("_app_web", dataStr);

                JSONObject data_o = HybridTools.s2o(dataStr);

                if (data_o != null) {
                    String url = data_o.optString("url");
                    if (!HybridTools.isEmptyString(url)) {
                        cb.onCallBack("{\"STS\":\"URL\"}");
                        return;
                    }
                }
                cb.onCallBack("{\"STS\":\"TODO\"}");
            }
        };
    }
}
