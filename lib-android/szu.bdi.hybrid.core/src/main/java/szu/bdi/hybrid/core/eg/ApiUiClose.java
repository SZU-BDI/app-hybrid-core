package szu.bdi.hybrid.core.eg;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.ICallBackFunction;
import szu.bdi.hybrid.core.ICallBackHandler;

public class ApiUiClose extends HybridApi {
    public ICallBackHandler getHandler() {
        return new ICallBackHandler() {
            @Override
            public void handler(String data, ICallBackFunction cb) {
                getCallerUi().close();
            }
        };
    }
}
