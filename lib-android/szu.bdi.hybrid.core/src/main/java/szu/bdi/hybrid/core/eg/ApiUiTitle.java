package szu.bdi.hybrid.core.eg;

//@doc https://szu-bdi.gitbooks.io/app-hybrid/content/


import java.util.Date;

import szu.bdi.hybrid.core.*;

import info.cmptech.JSO;

public class ApiUiTitle extends HybridApi {
    //    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();
    @Override
    public void handler(JSO jso, HybridCallback apiCallback) {

        jso.setChild("STS", JSO.s2o("TODO"));
        jso.setChild("pong", JSO.s2o("" + (new Date()).getTime()));
        apiCallback.onCallBack(jso);
        //handler(JSO.o2s(jso), cbFunc);
    }

}
