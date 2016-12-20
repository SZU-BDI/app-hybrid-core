package szu.bdi.hybrid.core.eg;

import android.util.Log;

import szu.bdi.hybrid.core.*;
import info.cmptech.JSO;

public class ApiWebRequest extends HybridApi {
    //    final private static String LOGTAG = (((new Throwable()).getStackTrace())[0]).getClassName();
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
    }
}
