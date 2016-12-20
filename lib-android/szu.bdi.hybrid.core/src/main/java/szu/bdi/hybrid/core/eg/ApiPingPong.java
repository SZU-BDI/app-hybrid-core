package szu.bdi.hybrid.core.eg;

import java.util.Date;

import info.cmptech.JSO;
import szu.bdi.hybrid.core.*;

public class ApiPingPong extends HybridApi {
    @Override
    public void handler(JSO jso, HybridCallback cbFunc) {

        jso.setChild("STS", JSO.s2o("TODO"));
        jso.setChild("pong", JSO.s2o("" + (new Date()).getTime()));
        cbFunc.onCallBack(jso);
    }

}
