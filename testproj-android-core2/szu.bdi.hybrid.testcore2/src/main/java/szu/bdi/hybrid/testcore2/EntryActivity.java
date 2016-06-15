package szu.bdi.hybrid.testcore2;

import android.os.Bundle;

import szu.bdi.hybrid.core2.HybridTools;
import szu.bdi.hybrid.core2.WebViewUi;

public class EntryActivity extends szu.bdi.hybrid.core2.HybridUi {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setUiData();
        HybridTools.startUi("UiRoot", "{topbar:'N',url:'file:///android_asset/root.htm'}", this, WebViewUi.class);
        this.finish();
    }
}