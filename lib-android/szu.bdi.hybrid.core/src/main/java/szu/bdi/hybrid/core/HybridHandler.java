package szu.bdi.hybrid.core;

import info.cmptech.JSO;

public interface HybridHandler {
    //NOTES: call by JSB
    //void handler(String json_s, HybridCallback cbFunc);

    //need to fwd to above.
    //for future, maybe just by...
    void handler(JSO jso, HybridCallback cbFunc);
}

