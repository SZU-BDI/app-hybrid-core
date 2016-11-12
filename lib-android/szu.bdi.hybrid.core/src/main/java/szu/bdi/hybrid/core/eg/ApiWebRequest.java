package szu.bdi.hybrid.core.eg;

import android.util.Log;

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.HybridCallback;
import szu.bdi.hybrid.core.HybridHandler;
import szu.bdi.hybrid.core.JSO;

//just an example Api return {STS:TODO} only

public class ApiWebRequest extends HybridApi {
    public HybridHandler getHandler() {
        return new HybridHandler() {
            @Override
            public void handler(String dataStr, HybridCallback cb) {
                //HybridUi callerAct = getCallerUi();

                Log.v("ApiWebRequest", dataStr);

                JSO data_o = JSO.s2o(dataStr);

                JSO urlJSO = data_o.getChild("url");

                String url = urlJSO.toString();
                if (!HybridTools.isEmptyString(url)) {
                    String rt_s = HybridTools.webPost(url, "");
                    Log.v("ApiWebRequest", url + " => " + rt_s);
                    cb.onCallBack("{\"STS\":\"URL\",\"len\":\"" + HybridTools.getStrLen(rt_s) + "\"}");
                    return;
                } else {
                    cb.onCallBack("{\"STS\":\"KO\"}");
                }
            }
        };
    }
}
