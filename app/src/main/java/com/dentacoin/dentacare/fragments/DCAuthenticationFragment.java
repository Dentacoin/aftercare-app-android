package com.dentacoin.dentacare.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.dentacoin.dentacare.R;
import com.dentacoin.dentacare.activities.DCAuthenticationActivity;
import com.dentacoin.dentacare.network.DCApiManager;
import com.dentacoin.dentacare.network.DCSession;
import com.dentacoin.dentacare.widgets.DCButton;
import com.dentacoin.dentacare.widgets.DCTextView;


/**
 * Created by Atanas Chervarov on 7/29/17.
 */

public class DCAuthenticationFragment extends DCFragment {

    private WebView webView;
    private ImageView ivCloseWebView;
    private DCButton btnCivic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        btnCivic = view.findViewById(R.id.btn_auth_civic);
        btnCivic.setOnClickListener(v -> onCivicLogin());
        btnCivic.setEnabled(false);

        view.findViewById(R.id.btn_auth_facebook).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onFacebookLogin());
        view.findViewById(R.id.btn_auth_google).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onGoogleLogin());
        view.findViewById(R.id.btn_auth_twitter).setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).onTwitterLogin());
        DCTextView tvAuthEmailLogin = view.findViewById(R.id.tv_auth_email_login);
        tvAuthEmailLogin.setOnClickListener(v -> ((DCAuthenticationActivity) getActivity()).showLoginFragment());
        tvAuthEmailLogin.setPaintFlags(tvAuthEmailLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        webView = view.findViewById(R.id.civic_web_view);
        ivCloseWebView = view.findViewById(R.id.iv_close);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent;
                if (url.contains("civic://") || url.contains("market://")) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    if (getActivity() != null) {
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                    return true;
                }
                return false;
            }


            public void onPageFinished(WebView wv, String url) {
                btnCivic.setEnabled(true);
            }

            public void onReceivedError(WebView wv, WebResourceRequest rq, WebResourceError error) {
                if (error != null) {
                    onError(error.toString());
                }
            }
        });

        webView.addJavascriptInterface(this, "DCCivicInterface");
        ivCloseWebView.setOnClickListener(v -> closeWebView());

        return view;
    }

    private void closeWebView() {
        webView.setVisibility(View.GONE);
        ivCloseWebView.setVisibility(View.GONE);
        btnCivic.setEnabled(false);
        webView.loadUrl(DCApiManager.getEndpointCivic());
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
        btnCivic.setEnabled(false);
        if (DCSession.getInstance().getCivicDeeplink() != null) {
            webView.loadUrl(DCSession.getInstance().getCivicDeeplink());
            DCSession.getInstance().setCivicDeeplink(null);
            showLoading();
        } else {
            webView.loadUrl(DCApiManager.getEndpointCivic());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @JavascriptInterface
    public void onSuccess(String json) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Log.d(TAG, "SUCCESS:" + json);
                closeWebView();
                hideLoading();

                //TODO: handle success request
            });
        }
    }

    @JavascriptInterface
    public void onError(String json) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Log.d(TAG, "ERROR:" + json);
                webView.setVisibility(View.INVISIBLE);
                closeWebView();
                hideLoading();

                //TODO: handle error
            });
        }
    }

    @JavascriptInterface
    public void onUserCancelled() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                Log.d(TAG, "CANCELED");
                webView.setVisibility(View.INVISIBLE);
                closeWebView();
                hideLoading();
            });
        }
    }

    public void onCivicLogin() {
        webView.setVisibility(View.VISIBLE);
        ivCloseWebView.setVisibility(View.VISIBLE);
        webView.loadUrl("javascript:startSignup()");
    }
}
