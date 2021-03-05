package morn.xposed.alipay.ui;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import morn.xposed.alipay.R;

import java.io.File;
import java.io.IOException;

public class HtmlViewerActivity extends Activity {
    MyWebView mWebView;
    ProgressBar pgb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_viewer);
        this.mWebView = (MyWebView) findViewById(R.id.mwv_webview);
        this.pgb = (ProgressBar) findViewById(R.id.pgb_webview);

        mWebView.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int progress) {
                        pgb.setProgress(progress);
                        if (progress < 100) {
                            setTitle("Loading...");
                            pgb.setVisibility(View.VISIBLE);
                        } else {
                            setTitle(mWebView.getTitle());
                            pgb.setVisibility(View.GONE);
                        }
                    }
                });
        this.mWebView.loadUrl(getIntent().getData().toString());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "在浏览器打开");
        menu.add(0, 2, 0, "复制URL");
        menu.add(0, 3, 0, "滚动到顶部");
        menu.add(0, 4, 0, "滚动到底部");
        if (getIntent().getData().toString().startsWith("file://")) {
            menu.add(0, 5, 0, "清空");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.addCategory(Intent.CATEGORY_DEFAULT);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setDataAndType(getIntent().getData(), "text/html");
                startActivity(Intent.createChooser(it, "选择浏览器"));
                break;

            case 2:
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(mWebView.getUrl());
                Toast.makeText(this, "Copy success", Toast.LENGTH_SHORT).show();
                break;

            case 3:
                mWebView.scrollTo(0, 0);
                break;
            case 4:
                mWebView.scrollToBottom();
                break;
            case 5:
                File file = new File(getIntent().getData().toString().replace("file://", ""));
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mWebView.reload();
                break;
        }
        return true;
    }
}