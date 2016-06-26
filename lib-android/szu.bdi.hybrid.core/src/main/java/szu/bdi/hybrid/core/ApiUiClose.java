package szu.bdi.hybrid.core;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/

public class ApiUiClose extends HybridApi {
    public ICallBackHandler getHandler() {
        return new ICallBackHandler() {
            @Override
            public void handler(String data, ICallBackFunction cb) {
                HybridUi __callerUi = getCallerUi();
                __callerUi.setCallBackFunction(cb);
                __callerUi.onBackPressed();
            }
        };
    }
}
