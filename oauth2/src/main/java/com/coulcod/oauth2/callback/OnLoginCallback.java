package com.coulcod.oauth2.callback;

import com.coulcod.oauth2.model.SocialUser;

public interface OnLoginCallback {

    void onSuccess(String token, SocialUser user);

    void onError(Exception error);

}