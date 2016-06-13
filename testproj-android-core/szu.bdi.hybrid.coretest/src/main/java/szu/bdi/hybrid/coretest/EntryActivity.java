package szu.bdi.hybrid.coretest;

import android.os.Bundle;

import szu.bdi.hybrid.core.HybridTools;
import szu.bdi.hybrid.core.WebViewUi;

public class EntryActivity extends szu.bdi.hybrid.core.HybridUi {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setUiData();
        HybridTools.startUi("UiRoot", "{topbar:'N',url:'file:///android_asset/root.htm'}", this, WebViewUi.class);
        this.finish();
    }
}