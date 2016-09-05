package com.coulcod.oauth2.oauth;

import android.app.Activity;
import android.os.AsyncTask;

import com.coulcod.oauth2.callback.OnLoginCallback;
import com.coulcod.oauth2.callback.OnOAuthTokenCallback;
import com.coulcod.oauth2.model.SocialUser;
import com.coulcod.oauth2.view.ContentDialogApi10a;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;

public abstract class BaseOAuthApi10a {
    protected Activity activity;
    protected DefaultApi10a api;
    protected String getAccountUrl;
    protected String revokeTokenUrl;
    protected Verb revokeTokenVerb;

    protected BaseOAuthApi10a(
            Activity activity,
            DefaultApi10a api,
            String getAccountUrl,
            String revokeTokenUrl,
            Verb revokeTokenVerb
    ) {
        this.activity = activity;
        this.api = api;
        this.getAccountUrl = getAccountUrl;
        this.revokeTokenUrl = revokeTokenUrl;
        this.revokeTokenVerb = revokeTokenVerb;
    }

    protected abstract SocialUser toAccount(String json);

    private SocialUser getAccount(OAuth10aService service, OAuth1AccessToken accessToken) {
        try {
            OAuthRequest request = new OAuthRequest(Verb.GET, getAccountUrl, service);
            service.signRequest(accessToken, request);
            Response response = request.send();
            return toAccount(response.getBody());
        } catch (Exception e) {
            return null;
        }
    }

    public static class LoginOAuth implements ILoginOAuth {
        private static final String TAG = "LoginOAuth";

        protected BaseOAuthApi10a oAuth;
        protected String clientId;
        protected String clientSecret;
        protected String redirectUri;
        protected OnLoginCallback callback;

        protected LoginOAuth(BaseOAuthApi10a oAuth) {
            this.oAuth = oAuth;
        }

        @Override
        public LoginOAuth setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        @Override
        public LoginOAuth setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        @Override
        public LoginOAuth setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        @Override
        public LoginOAuth setCallback(OnLoginCallback callback) {
            this.callback = callback;
            return this;
        }

        @Override
        public void init() {
            final OAuth10aService service = new ServiceBuilder()
                    .apiKey(clientId)
                    .apiSecret(clientSecret)
                    .callback(redirectUri)
                    .build(oAuth.api);

            new RequestTokenTask() {
                @Override
                OAuth1RequestToken getRequestToken() throws IOException {
                    return service.getRequestToken();
                }

                @Override
                void openDialog(final OAuth1RequestToken oAuth1RequestToken) {
                    final String authUrl = service.getAuthorizationUrl(oAuth1RequestToken);
                    ContentDialogApi10a.newInstance(authUrl, redirectUri)
                            .setOnGetCodeCallback(new OnOAuthTokenCallback() {
                                @Override
                                public void onSuccess(final String OAuthToken, final String OAuthVerifier) {
                                    AsyncTask.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                final OAuth1AccessToken accessToken = service.getAccessToken(oAuth1RequestToken, OAuthVerifier);
                                                final SocialUser account = oAuth.getAccount(service,
                                                        accessToken);
                                                oAuth.activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        callback.onSuccess(accessToken.getToken(), account);
                                                    }
                                                });
                                            } catch (final IOException e) {
                                                oAuth.activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        callback.onError(e);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(Exception error) {
                                    callback.onError(error);
                                }
                            })
                            .show(oAuth.activity.getFragmentManager(), ContentDialogApi10a.class.getName());
                }

                @Override
                void onError(IOException error) {

                }
            }.execute();


        }

    }

    private static abstract class RequestTokenTask extends AsyncTask<Void,Void,OAuth1RequestToken> {

        private IOException error;

        @Override
        protected OAuth1RequestToken doInBackground(Void... params) {
            OAuth1RequestToken token = null;
            try {
                token = getRequestToken();
            } catch (IOException e) {
                error = e;
            }
            return token;
        }

        @Override
        protected void onPostExecute(OAuth1RequestToken oAuth1RequestToken) {
            if (error != null) {
                onError(error);
            } else {
                openDialog(oAuth1RequestToken);
            }
        }

        abstract OAuth1RequestToken getRequestToken() throws IOException;

        abstract void openDialog(OAuth1RequestToken oAuth1RequestToken);

        abstract void onError(IOException error);
    }

}
