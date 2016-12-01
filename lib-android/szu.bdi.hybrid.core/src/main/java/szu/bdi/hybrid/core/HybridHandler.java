package szu.bdi.hybrid.core;

import info.cmptech.JSO;

public interface HybridHandler {
    void handler(String json_s, HybridCallback cbFunc);

    void handler(JSO jso, HybridCallback cbFunc);
}

