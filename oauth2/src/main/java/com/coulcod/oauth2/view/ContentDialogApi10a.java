package com.coulcod.oauth2.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coulcod.oauth2.callback.OnOAuthTokenCallback;

public class ContentDialogApi10a extends DialogFragment {
    private static final String EXTRA_AUTH_URL = "authUrl";
    private static final String EXTRA_REDIRECT_URL = "redirectUrl";

    private String authUrl;
    private String redirectUrl;
    private boolean success = false;
    private OnOAuthTokenCallback callback;

    public static ContentDialogApi10a newInstance(String authUrl, String redirectUrl) {
        Bundle args = new Bundle();
        args.putString(EXTRA_AUTH_URL, authUrl);
        args.putString(EXTRA_REDIRECT_URL, redirectUrl);
        ContentDialogApi10a frag = new ContentDialogApi10a();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authUrl = getArguments().getString(EXTRA_AUTH_URL);
        redirectUrl = getArguments().getString(EXTRA_REDIRECT_URL);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        KeyboardWebView webView = new KeyboardWebView(getActivity());
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.startsWith(redirectUrl) && url.contains("oauth_token") && url.contains("oauth_verifier")) {
                    getOAuthTokenAndVerifier(url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        webView.loadUrl(authUrl);

        return new AlertDialog.Builder(getActivity())
                .setView(webView)
                .create();
    }

    private void getOAuthTokenAndVerifier(String url) {
        Uri uri = Uri.parse(url);
        callback.onSuccess(uri.getQueryParameter("oauth_token"), uri.getQueryParameter("oauth_verifier"));
        success = true;
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!success) {
            callback.onError(new Exception(
                    "Dialog was dismissed without complete the authentication"));
        }
    }

    public ContentDialogApi10a setOnGetCodeCallback(OnOAuthTokenCallback callback) {
        this.callback = callback;
        return this;
    }

}