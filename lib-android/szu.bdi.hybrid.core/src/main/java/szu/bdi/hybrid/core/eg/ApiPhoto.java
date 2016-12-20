package szu.bdi.hybrid.core.eg;

import info.cmptech.JSO;
import szu.bdi.hybrid.core.*;

public class ApiPhoto extends HybridApi {
    @Override
    public void handler(JSO jso, final HybridCallback cbFunc) {
        final HybridUi ui = getCallerUi();

        HybridTools.startUi("UiPhoto", jso.toString(true), ui, new HybridUiCallback() {
            @Override
            public void onCallBack(final HybridUi ui) {

                //bind "close"
                ui.on("close", new HybridCallback() {

                    @Override
                    public void onCallBack(JSO jsoCallback) {

                        //manually handle finishing the ui
                        ui.finish();

                        //back to api caller
                        cbFunc.onCallBack(jsoCallback);
                    }
                });
            }

        });//startUi()
    }//handler()
}
