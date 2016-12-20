package szu.bdi.hybrid.core.eg;

import info.cmptech.JSO;

import szu.bdi.hybrid.core.*;

public class ApiUiClose extends HybridApi {
    @Override
    public void handler(JSO jso, HybridCallback cbFunc) {
        HybridUi ui = getCallerUi();

        ui.closeUi(jso);

        if (null != cbFunc) cbFunc.onCallBack(jso);
    }
}
