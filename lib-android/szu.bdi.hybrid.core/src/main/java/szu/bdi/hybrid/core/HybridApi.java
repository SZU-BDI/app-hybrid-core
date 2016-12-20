package szu.bdi.hybrid.core;

abstract public class HybridApi {
    private HybridUi __callerUi = null;

    public void setCallerUi(HybridUi callerUi) {
        __callerUi = callerUi;
    }

    public HybridUi getCallerUi() {
        return __callerUi;
    }

    abstract public void handler(info.cmptech.JSO jso, HybridCallback cbFunc);
}
