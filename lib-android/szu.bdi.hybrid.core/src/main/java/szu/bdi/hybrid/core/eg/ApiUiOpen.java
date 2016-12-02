package szu.bdi.hybrid.core.eg;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import szu.bdi.hybrid.core.*;
import info.cmptech.JSO;


public class ApiUiOpen extends HybridApi {
    //    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();
    public HybridHandler getHandler() {
        return new HybridHandler() {
//            @Override
//            public void handler(String dataStr, final HybridCallback apiCallback) {
//                HybridUi callerUi = getCallerUi();
//
//                JSO data_o = JSO.s2o(dataStr);
//
//                String t = data_o.getChild("name").toString();
//                String uiName = (!HybridTools.isEmptyString(t)) ? t : "UiContent";//default to UiContent
//
//                HybridTools.startUi(uiName, dataStr, callerUi, new HybridUiCallback() {
//                    @Override
//                    public void onCallBack(final HybridUi ui) {
//
//                        //listen "close" event
//                        ui.on("close", new HybridCallback() {
//                            @Override
//                            public void onCallBack(String cbStr) {
//
//                                //fwd
//                                onCallBack(JSO.s2o(cbStr));
//                            }
//
//                            @Override
//                            public void onCallBack(JSO jso) {
//
//                                //manually close it
//                                ui.finish();
//
//                                //api callback
//                                apiCallback.onCallBack(jso);
//                            }
//                        });
//                    }
//                });
//            }

            @Override
            public void handler(JSO jso, final HybridCallback apiCallback) {
                HybridUi callerUi = getCallerUi();

                //JSO data_o = JSO.s2o(dataStr);

                String t = jso.getChild("name").toString();
                String uiName = (!HybridTools.isEmptyString(t)) ? t : "UiContent";//default to UiContent

                HybridTools.startUi(uiName, jso.toString(true), callerUi, new HybridUiCallback() {
                    @Override
                    public void onCallBack(final HybridUi ui) {

                        //listen "close" event
                        ui.on("close", new HybridCallback() {

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
        };
    }
}
