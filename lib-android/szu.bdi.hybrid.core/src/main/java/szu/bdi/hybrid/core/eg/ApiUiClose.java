package szu.bdi.hybrid.core.eg;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.HybridCallback;
import szu.bdi.hybrid.core.HybridHandler;
import szu.bdi.hybrid.core.JSO;

public class ApiUiClose extends HybridApi {
    public HybridHandler getHandler() {
        return new HybridHandler() {
            @Override
            public void handler(String data, HybridCallback cb) {
                getCallerUi().close();
            }

            @Override
            public void handler(JSO jso, HybridCallback cbFunc) {
                handler(JSO.o2s(jso), cbFunc);
            }
        };
    }
}
