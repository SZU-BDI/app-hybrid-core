package szu.bdi.hybrid.core.eg;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import android.os.Looper;
import android.util.Log;

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.HybridCallback;
import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.HybridUi;
import szu.bdi.hybrid.core.HybridHandler;
import szu.bdi.hybrid.core.JSO;

public class ApiUiOpen extends HybridApi {
    public HybridHandler getHandler() {
        return new HybridHandler() {
            @Override
            public void handler(String dataStr, HybridCallback cb) {
                HybridUi callerAct = getCallerUi();
                Log.v("_app_activity_open", dataStr);

                //callerAct.setCallBackFunction(cb);

                String uiName = "UiContent";//default

                JSO data_o = JSO.s2o(dataStr);

                String t = data_o.getChild("name").toString();
                if (!HybridTools.isEmptyString(t)) {
                    uiName = t;
                }
                //try override by the callParam.name
//                if (data_o != null) {
//                    String t = data_o.optString("name");
//                    if (!HybridTools.isEmptyString(t)) {
//                        uiName = t;
//                    }
//                }
                Looper.prepare();
                HybridTools.startUi(uiName, dataStr, callerAct, cb);
            }

            @Override
            public void handler(JSO jso, HybridCallback cbFunc) {
                handler(JSO.o2s(jso), cbFunc);
            }
        };
    }
}
