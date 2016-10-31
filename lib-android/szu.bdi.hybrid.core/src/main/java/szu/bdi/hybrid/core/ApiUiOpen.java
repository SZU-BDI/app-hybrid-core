package szu.bdi.hybrid.core;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import android.util.Log;

import org.json.JSONObject;

public class ApiUiOpen extends HybridApi {
    public ICallBackHandler getHandler() {
        return new ICallBackHandler() {
            @Override
            public void handler(String dataStr, ICallBackFunction cb) {
                HybridUi callerAct = getCallerUi();
                Log.v("_app_activity_open", dataStr);

                callerAct.setCallBackFunction(cb);

                String uiName = "UiContent";//default

                JSONObject data_o = HybridTools.s2o(dataStr);

                //try override by the callParam.name
                if (data_o != null) {
                    String t = data_o.optString("name");
                    if (!HybridTools.isEmptyString(t)) {
                        uiName = t;
                    }
                }
                HybridTools.startUi(uiName, dataStr, callerAct);
            }
        };
    }
}
