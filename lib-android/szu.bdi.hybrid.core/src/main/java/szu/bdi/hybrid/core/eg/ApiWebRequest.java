package szu.bdi.hybrid.core.eg;

import android.util.Log;

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.HybridCallback;
import szu.bdi.hybrid.core.HybridHandler;
import info.cmptech.JSO;

//just an example Api return {STS:TODO} only

public class ApiWebRequest extends HybridApi {
    public HybridHandler getHandler() {
        return new HybridHandler() {
//            @Override
//            public void handler(String dataStr, HybridCallback cb) {
//                //HybridUi callerAct = getCallerUi();
//
//                Log.v("ApiWebRequest", dataStr);
//
//                JSO data_o = JSO.s2o(dataStr);
//
//                JSO urlJSO = data_o.getChild("url");
//
//                String url = urlJSO.toString();
//                if (!HybridTools.isEmptyString(url)) {
//                    String rt_s = HybridTools.webPost(url, "");
//                    Log.v("ApiWebRequest", url + " => " + rt_s);
//                    cb.onCallBack("{\"STS\":\"URL\",\"len\":\"" + HybridTools.getStrLen(rt_s) + "\"}");
//                    return;
//                } else {
//                    cb.onCallBack("{\"STS\":\"KO\"}");
//                }
//            }

            @Override
            public void handler(JSO data_o, HybridCallback apiCallback) {

                JSO rt = new JSO();

                JSO urlJSO = data_o.getChild("url");

                String url = urlJSO.toString();
                if (!HybridTools.isEmptyString(url)) {
                    String rt_s = HybridTools.webPost(url, "");
                    Log.v("ApiWebRequest", url + " => " + rt_s);
                    rt.setChild("STS", "OK");
                    rt.setChild("len", "HybridTools.getStrLen(rt_s)");
                    rt.setChild("s", rt_s);
                    apiCallback.onCallBack(rt);
                    return;
                } else {
                    rt.setChild("STS", "KO");
                    apiCallback.onCallBack(rt);
                }
                //handler(JSO.o2s(jso), cbFunc);
            }
        };
    }
}
