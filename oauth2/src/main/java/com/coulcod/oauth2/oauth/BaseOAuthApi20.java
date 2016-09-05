package com.coulcod.oauth2.oauth;

import android.app.Activity;
import android.os.AsyncTask;

import com.coulcod.oauth2.callback.OnGetCodeCallback;
import com.coulcod.oauth2.callback.OnLoginCallback;
import com.coulcod.oauth2.callback.OnLogoutCallback;
import com.coulcod.oauth2.model.SocialUser;
import com.coulcod.oauth2.view.ContentDialogApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.studioidan.httpagent.HttpAgent;
import com.studioidan.httpagent.SuccessCallback;

import java.util.UUID;

public abstract class BaseOAuthApi20 {
    protected Activity activity;
    protected DefaultApi20 api;
    protected String scope;
    protected String getAccountUrl;
    protected String revokeTokenUrl;
    protected Verb revokeTokenVerb;

    protected BaseOAuthApi20(Activity activity, DefaultApi20 api, String scope, String getAccountUrl,
                        String revokeTokenUrl, Verb revokeTokenVerb) {
        this.activity = activity;
        this.api = api;
        this.scope = scope;
        this.getAccountUrl = getAccountUrl;
        this.revokeTokenUrl = revokeTokenUrl;
        this.revokeTokenVerb = revokeTokenVerb;
    }

    protected abstract SocialUser toAccount(String json);

    private SocialUser getAccount(OAuth20Service service, OAuth2AccessToken accessToken) {
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
        protected BaseOAuthApi20 oAuth;

        protected String clientId;
        protected String clientSecret;
        protected String redirectUri;
        protected OnLoginCallback callback;

        protected LoginOAuth(BaseOAuthApi20 oAuth) {
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
            // Generating a complex state for better security
            // http://twobotechnologies.com/blog/2014/02/importance-of-state-in-oauth2.html
            final String state = UUID.randomUUID().toString();

            final OAuth20Service service = new ServiceBuilder()
                    .apiKey(clientId)
                    .apiSecret(clientSecret)
                    .callback(redirectUri)
                    .state(state)
                    .scope(oAuth.scope)
                    .build(oAuth.api);
            final String authUrl = service.getAuthorizationUrl();

            ContentDialogApi20.newInstance(authUrl, state)
                    .setOnGetCodeCallback(new OnGetCodeCallback() {
                        @Override
                        public void onSuccess(final String code) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final OAuth2AccessToken accessToken = service
                                                .getAccessToken(code);
                                        final SocialUser account = oAuth.getAccount(service,
                                                accessToken);
                                        oAuth.activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                callback.onSuccess(accessToken.getAccessToken(), account);
                                            }
                                        });
                                    } catch (final Exception e) {
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
                    .show(oAuth.activity.getFragmentManager(), ContentDialogApi20.class.getName());
        }

    }

    public static class LogoutOAuth implements ILogoutOAuth {
        protected BaseOAuthApi20 oAuth;

        protected String token;
        protected OnLogoutCallback callback;

        protected LogoutOAuth(BaseOAuthApi20 oAuth) {
            this.oAuth = oAuth;
        }

        @Override
        public LogoutOAuth setToken(String token) {
            this.token = token;
            return this;
        }

        @Override
        public LogoutOAuth setCallback(OnLogoutCallback callback) {
            this.callback = callback;
            return this;
        }

        @Override
        public void init() {
            final String url = String.format(oAuth.revokeTokenUrl, token);
            final SuccessCallback requestCallback = new SuccessCallback() {
                @Override
                protected void onDone(final boolean success) {
                    oAuth.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (success) {
                                callback.onSuccess();
                            } else {
                                callback.onError(new Exception(getErrorMessage()));
                            }
                        }
                    });
                }
            };
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    switch (oAuth.revokeTokenVerb) {
                        case GET:
                            HttpAgent.get(url)
                                    .go(requestCallback);
                            break;
                        case DELETE:
                            HttpAgent.delete(url)
                                    .go(requestCallback);
                            break;
                        default:
                            oAuth.activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError(new Exception("HTTP Verb not implemented: " + oAuth.revokeTokenVerb));
                                }
                            });
                    }
                }
            });
        }

    }
}
