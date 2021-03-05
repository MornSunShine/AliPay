package morn.xposed.alipay.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setDisplayZoomControls(false);
        getSettings().setUseWideViewPort(false);
        getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        getSettings().setAllowFileAccess(true);
        getSettings().setJavaScriptEnabled(true);
        setWebViewClient(
                new WebViewClient() {
                    public void onPageFinished(WebView view, String url) {
                        if (url.endsWith(".txt"))
                            postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if (Thread.interrupted()) return;
                                            if (getContentHeight() == 0)
                                                postDelayed(this, 100);
                                            else
                                                scrollToBottom();
                                        }
                                    }, 500);
                    }
                });
    }

    public void scrollToBottom() {
        scrollTo(0, computeVerticalScrollRange());
    }
}