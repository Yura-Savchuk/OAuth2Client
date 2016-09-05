package com.coulcod.oauth2.callback;

public interface OnOAuthTokenCallback {

    void onSuccess(String OAuthToken, String OAuthVerifier);

    void onError(Exception error);

}
