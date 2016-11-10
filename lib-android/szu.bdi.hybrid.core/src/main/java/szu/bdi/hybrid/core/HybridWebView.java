package szu.bdi.hybrid.core;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class HybridWebView extends WebView {
    public HybridWebView(final Context context) {
        super(context);
    }

    public HybridWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HybridWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void registerHandler(String v, ICallBackHandler handler) {
        //TODO
    }
}
