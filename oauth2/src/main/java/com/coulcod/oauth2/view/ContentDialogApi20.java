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

import com.coulcod.oauth2.callback.OnGetCodeCallback;

public class ContentDialogApi20 extends DialogFragment {
    private static final String EXTRA_AUTH_URL = "authUrl";
    private static final String EXTRA_STATE = "originalState";

    private String authUrl;
    private String originalState;
    private String code;
    private OnGetCodeCallback callback;

    public static ContentDialogApi20 newInstance(String authUrl, String state) {
        Bundle args = new Bundle();
        args.putString(EXTRA_AUTH_URL, authUrl);
        args.putString(EXTRA_STATE, state);
        ContentDialogApi20 frag = new ContentDialogApi20();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authUrl = getArguments().getString(EXTRA_AUTH_URL);
        originalState = getArguments().getString(EXTRA_STATE);
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
                if (url.contains("code=")) {
                    getCode(url);
                } else if(view.getTitle().contains("code=")){
                    // Creating a valid URI to extract parameters easily
                    getCode(view.getTitle().replace("Success ", "http://oauth?"));
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

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (code == null || code.isEmpty()) {
            callback.onError(new Exception(
                    "Dialog was dismissed without complete the authentication"));
        }
    }

    public ContentDialogApi20 setOnGetCodeCallback(OnGetCodeCallback callback) {
        this.callback = callback;
        return this;
    }

    private void getCode(String url) {
        Uri uri = Uri.parse(url);
        String receivedState = uri.getQueryParameter("state");
        code = uri.getQueryParameter("code");
        if (code != null && !code.isEmpty() && originalState.equals(receivedState)) {
            callback.onSuccess(code);
        } else {
            String error = String.format("Wrong state: %s (original) != %s (received)",
                    originalState, receivedState);
            callback.onError(new Exception(error));
        }
        dismiss();
    }

}