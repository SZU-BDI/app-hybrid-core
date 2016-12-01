package szu.bdi.hybrid.core.eg;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import android.util.Log;

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.HybridCallback;
import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.HybridUi;
import szu.bdi.hybrid.core.HybridHandler;
import szu.bdi.hybrid.core.HybridUiCallback;
import info.cmptech.JSO;


/**
 *
 *
 */
public class ApiUiOpen extends HybridApi {
    public HybridHandler getHandler() {
        return new HybridHandler() {
            @Override
            public void handler(String dataStr, final HybridCallback apiCallback) {
                HybridUi callerUi = getCallerUi();
                Log.v("_app_activity_open", dataStr);

                String uiName = "UiContent";//default to UiContent

                JSO data_o = JSO.s2o(dataStr);

                String t = data_o.getChild("name").toString();
                if (!HybridTools.isEmptyString(t)) {
                    uiName = t;
                }

                HybridTools.startUi(uiName, dataStr, callerUi, new HybridUiCallback() {
                    @Override
                    public void onCallBack(final HybridUi ui) {
                        //listen "close" event
                        ui.on("close", new HybridCallback() {
                            @Override
                            public void onCallBack(String cbStr) {
                                //fwd
                                onCallBack(JSO.s2o(cbStr));
                            }

                            @Override
                            public void onCallBack(JSO jso) {
                                //manually close it
                                ui.finish();
                                //api callback
                                apiCallback.onCallBack(jso);
                            }
                        });
                    }
                });
            }

            @Override
            public void handler(JSO jso, HybridCallback cbFunc) {
                handler(JSO.o2s(jso), cbFunc);
            }
        };
    }
}
