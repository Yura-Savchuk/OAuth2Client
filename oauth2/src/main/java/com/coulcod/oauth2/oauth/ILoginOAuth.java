package com.coulcod.oauth2.oauth;

import com.coulcod.oauth2.callback.OnLoginCallback;

public interface ILoginOAuth {

    ILoginOAuth setClientId(String clientId);

    ILoginOAuth setClientSecret(String clientSecret);

    ILoginOAuth setRedirectUri(String redirectUri);

    ILoginOAuth setCallback(OnLoginCallback callback);

    void init();

}