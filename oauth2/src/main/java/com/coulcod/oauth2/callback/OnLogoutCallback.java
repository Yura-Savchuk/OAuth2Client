package com.coulcod.oauth2.callback;

public interface OnLogoutCallback {

    void onSuccess();

    void onError(Exception error);

}