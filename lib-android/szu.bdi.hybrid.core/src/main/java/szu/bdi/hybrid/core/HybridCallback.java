package szu.bdi.hybrid.core;

import info.cmptech.JSO;

public interface HybridCallback {

    void onCallBack(String json_s);

    void onCallBack(JSO jso);
}
