package szu.bdi.hybrid.core.eg;

import android.Manifest;

import info.cmptech.JSO;
import szu.bdi.hybrid.core.*;

public class ApiPhoto extends HybridApi {
    public HybridHandler getHandler() {
        return new HybridHandler() {

            @Override
            public void handler(JSO jso, final HybridCallback cbFunc) {
                final HybridUi ui = getCallerUi();

//                String from = jso.getChild("from").toString();
//                if ("camera".equals(from)
//                        || "album".equals(from)
//                        ) {

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

//                } else {
//                    JSO rt = new JSO();
//                    rt.setChild("STS", "KO");
//                    rt.setChild("errmsg", "Need param 'from'");
//                    cbFunc.onCallBack(rt);
//                }
            }//handler()
        };
    }
}
