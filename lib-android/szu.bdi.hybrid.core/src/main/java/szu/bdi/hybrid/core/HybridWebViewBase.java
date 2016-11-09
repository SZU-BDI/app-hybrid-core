package szu.bdi.hybrid.core;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class HybridWebViewBase extends WebView {
    public HybridWebViewBase(final Context context) {
        super(context);
    }

    public HybridWebViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HybridWebViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void registerHandler(String v, ICallBackHandler handler) {
        //TODO
    }
}
