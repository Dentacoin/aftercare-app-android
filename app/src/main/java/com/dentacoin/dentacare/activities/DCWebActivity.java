package com.dentacoin.dentacare.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Atanas Chervarov on 10/2/17.
 */

public class DCWebActivity extends DCToolbarActivity {

    public static final String KEY_WEB_ADDRESS = "KEY_WEB_ADDRESS";
    public static final String KEY_TITLE = "KEY_TITLE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String address = (String) getIntent().getSerializableExtra(KEY_WEB_ADDRESS);
        String title = (String) getIntent().getSerializableExtra(KEY_TITLE);

        if (address == null) {
            finish();
            return;
        }

        if (title != null) {
            setActionBarTitle(title);
        }

        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.setWebViewClient(new WebViewClient());

        webview.loadUrl(address);
        addContentView(webview);
    }
}
