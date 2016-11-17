package szu.bdi.hybrid.core;

public interface HybridHandler {
    void handler(String json_s, HybridCallback cbFunc);

    void handler(JSO jso, HybridCallback cbFunc);
}

