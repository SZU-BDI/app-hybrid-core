package szu.bdi.hybrid.core.eg;

import szu.bdi.hybrid.core.*;

public class ApiUiClose extends HybridApi {
    public HybridHandler getHandler() {
        return new HybridHandler() {
            @Override
            public void handler(String data, HybridCallback cb) {

                //fwd to below one
                handler(JSO.s2o(data), cb);
            }

            @Override
            public void handler(JSO jso, HybridCallback cbFunc) {
                HybridUi ui = getCallerUi();

                ui.closeUi(jso);
                if (null != cbFunc) cbFunc.onCallBack(jso);
            }
        };
    }
}
