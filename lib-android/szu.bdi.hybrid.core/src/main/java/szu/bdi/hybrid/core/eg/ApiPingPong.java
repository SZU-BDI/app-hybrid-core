package szu.bdi.hybrid.core.eg;

import android.util.Log;

import org.json.JSONObject;

import java.util.Date;

import szu.bdi.hybrid.core.HybridApi;
import szu.bdi.hybrid.core.ICallBackFunction;
import szu.bdi.hybrid.core.ICallBackHandler;
import szu.bdi.hybrid.core.JSO;

public class ApiPingPong extends HybridApi {
    public ICallBackHandler getHandler() {
        return new ICallBackHandler() {
            @Override
            public void handler(String dataStr, ICallBackFunction cb) {
                Log.v("ApiPingPong", dataStr);

                JSO data_o = JSO.s2o(dataStr);
                data_o.setChild("STS", JSO.s2o("TODO"));
                data_o.setChild("pong", JSO.s2o("" + (new Date()).getTime()));
                cb.onCallBack(data_o.toString());
            }
        };
    }
}
