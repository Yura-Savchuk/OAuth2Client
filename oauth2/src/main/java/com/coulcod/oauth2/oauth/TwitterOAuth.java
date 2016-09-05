package com.coulcod.oauth2.oauth;

import android.app.Activity;

import com.coulcod.oauth2.model.SocialUser;
import com.github.scribejava.apis.TwitterApi;

import org.json.JSONObject;

public class TwitterOAuth extends BaseOAuthApi10a {
    private static final String GET_ACCOUNT_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";

    private TwitterOAuth(Activity activity) {
        super(activity, TwitterApi.instance(), GET_ACCOUNT_URL, null, null);
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
            account.setProvider(OAuthProvider.TWITTER);
            return account;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ILoginOAuth login(Activity activity) {
        return new LoginOAuth(new TwitterOAuth(activity));
    }

}
