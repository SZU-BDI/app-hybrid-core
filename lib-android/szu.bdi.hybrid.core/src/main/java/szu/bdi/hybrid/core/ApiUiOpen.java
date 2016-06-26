package szu.bdi.hybrid.core;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import android.util.Log;

import org.json.JSONObject;

public class ApiUiOpen extends HybridApi {
    public ICallBackHandler getHandler() {
        return new ICallBackHandler() {
            @Override
            public void handler(String data, ICallBackFunction cb) {
                HybridUi callerAct = getCallerUi();
                Log.v("_app_activity_open", data);

                callerAct.setCallBackFunction(cb);

                //TMP TEST:
                //HybridTools.startUi("UiRoot", "{topbar:'N',address:'" + root_htm_s + "'}", _activity);

                String uiName = "UiContent";//default
                JSONObject data_o = HybridTools.s2o(data);
                //try override by the callParam.name
                if (data_o != null) {
                    String t = data_o.optString("name");
                    if (!HybridTools.isEmptyString(t)) {
                        uiName = t;
                    }
                }
                HybridTools.startUi(uiName, data, callerAct);
            }
        };
    }
}
