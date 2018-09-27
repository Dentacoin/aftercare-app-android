package com.dentacoin.dentacare.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dentacoin.dentacare.utils.DCTutorialManager;

/**
 * Created by Atanas Chervarov on 19.09.18.
 */
public class DCCivicAuthenticationActivity extends DCToolbarActivity {

    WebView webview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent;
                if (url.contains("civic://")) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        webview.addJavascriptInterface(this, "Android");
        webview.loadUrl("https://blocked.dentacoin.com/blockscript/civic-sign-in.html");
        addContentView(webview);
    }

    @Override
    public void onResume() {
        super.onResume();
        webview.onResume();
        if (DCTutorialManager.getInstance().test != null) {
            webview.loadUrl(DCTutorialManager.getInstance().test);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        webview.onPause();
    }

    @JavascriptInterface
    public void returnResult(String json) {
        Log.d(null, "result: " + json);
    }
}
