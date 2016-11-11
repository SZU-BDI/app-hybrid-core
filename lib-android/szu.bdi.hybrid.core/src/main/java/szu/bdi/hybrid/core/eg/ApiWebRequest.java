package szu.bdi.hybrid.core.eg;

import android.util.Log;

import org.json.JSONObject;

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.ICallBackFunction;
import szu.bdi.hybrid.core.ICallBackHandler;
import szu.bdi.hybrid.core.JSO;

//just an example Api return {STS:TODO} only

public class ApiWebRequest extends HybridApi {
    public ICallBackHandler getHandler() {
        return new ICallBackHandler() {
            @Override
            public void handler(String dataStr, ICallBackFunction cb) {
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
                }else{
                    cb.onCallBack("{\"STS\":\"KO\"}");
                }
                /*
                Object v = data_o.getValue();
                if (v != null && v instanceof JSONObject) {
                    String url = ((JSONObject) data_o.getValue()).optString("url");
                    if (!HybridTools.isEmptyString(url)) {
                        String rt_s = HybridTools.webPost(url, "");
                        Log.v("ApiWebRequest", url + " => " + rt_s);
                        cb.onCallBack("{\"STS\":\"URL\",\"len\":\"" + HybridTools.getStrLen(rt_s) + "\"}");
                        return;
                    }
                }
                cb.onCallBack("{\"STS\":\"TODO\"}");

                */
            }
        };
    }
}
