package com.coulcod.oauth2.callback;

public interface OnGetCodeCallback {

    void onSuccess(String code);

    void onError(Exception error);

}