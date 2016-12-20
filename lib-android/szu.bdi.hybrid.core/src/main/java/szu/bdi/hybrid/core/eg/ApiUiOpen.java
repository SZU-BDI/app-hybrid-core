package szu.bdi.hybrid.core.eg;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import szu.bdi.hybrid.core.*;
import info.cmptech.JSO;

public class ApiUiOpen extends HybridApi {
    final private static String LOGTAG = new Throwable().getStackTrace()[0].getClassName();

    @Override
    public void handler(JSO jso, final HybridCallback apiCallback) {

        String t = jso.getChild("name").toString();
        String uiName = (!HybridTools.isEmptyString(t)) ? t : "UiContent";//default to UiContent

        HybridTools.startUi(uiName, jso.toString(true), getCallerUi(), new HybridUiCallback() {
            @Override
            public void onCallBack(final HybridUi ui) {

                //listen "close" event
                ui.on("close", new HybridCallback() {

                    @Override
                    public void onCallBack(JSO jsoCallback) {

                        //manually close it
                        ui.finish();

                        //api callback
                        if (null != apiCallback)
                            apiCallback.onCallBack(jsoCallback);
                    }
                });
            }
        });
    }
}
