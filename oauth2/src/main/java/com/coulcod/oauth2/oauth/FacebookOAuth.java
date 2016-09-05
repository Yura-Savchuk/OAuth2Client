package com.coulcod.oauth2.oauth;

import android.app.Activity;

import com.coulcod.oauth2.model.SocialUser;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.model.Verb;

import org.json.JSONObject;

public final class FacebookOAuth extends BaseOAuthApi20 {
    private static final String SCOPE = "public_profile email";
    private static final String GET_ACCOUNT_URL = "https://graph.facebook.com/v2.6/me?fields=name,email,picture,cover";
    private static final String REVOKE_TOKEN_URL = "https://graph.facebook.com/v2.6/me/permissions?access_token=%s";
    private static final Verb REVOKE_TOKEN_VERB = Verb.DELETE;

    private FacebookOAuth(Activity activity) {
        super(activity, FacebookApi.instance(), SCOPE, GET_ACCOUNT_URL, REVOKE_TOKEN_URL,
                REVOKE_TOKEN_VERB);
    }

    public static ILoginOAuth login(Activity activity) {
        return new LoginOAuth(new FacebookOAuth(activity));
    }

    public static LogoutOAuth logout(Activity activity) {
        return new LogoutOAuth(new FacebookOAuth(activity));
    }

    @Override
    protected SocialUser toAccount(String json) {
        try {
            JSONObject accountJson = new JSONObject(json);
            SocialUser account = new SocialUser();
            account.setId(accountJson.getString("id"));
            account.setName(accountJson.getString("name"));
            if (accountJson.has("email")) {
                account.setEmail(accountJson.getString("email"));
            }
            if (accountJson.has("picture")) {
                account.setPictureUrl(accountJson
                        .getJSONObject("picture")
                        .getJSONObject("data")
                        .getString("url"));
            }
            if (accountJson.has("cover")) {
                account.setCoverUrl(accountJson
                        .getJSONObject("cover")
                        .getString("source"));
            }
            account.setProvider(OAuthProvider.FACEBOOK);
            return account;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}