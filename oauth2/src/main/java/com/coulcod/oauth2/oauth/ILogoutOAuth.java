package com.coulcod.oauth2.oauth;

import com.coulcod.oauth2.callback.OnLogoutCallback;

public interface ILogoutOAuth<T> {

    T setToken(String token);

    T setCallback(OnLogoutCallback callback);

    void init();

}