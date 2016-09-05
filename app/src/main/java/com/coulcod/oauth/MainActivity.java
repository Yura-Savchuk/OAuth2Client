package com.coulcod.oauth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.coulcod.oauth2.callback.OnLoginCallback;
import com.coulcod.oauth2.model.SocialUser;
import com.coulcod.oauth2.oauth.FacebookOAuth;
import com.coulcod.oauth2.oauth.GoogleOAuth;
import com.coulcod.oauth2.oauth.TwitterOAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnFacebookClick(View view) {
        FacebookOAuth.login(this)
                .setClientId(OAuthCredential.FACEBOOK_CLIENT_ID)
                .setClientSecret(OAuthCredential.FACEBOOK_CLIENT_SECRET)
                .setRedirectUri(OAuthCredential.FACEBOOK_REDIRECT_URI)
                .setCallback(new OnLoginCallback() {
                    @Override
                    public void onSuccess(String token, SocialUser user) {
                        Toast.makeText(MainActivity.this, "Receive Facebook token: " + token, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception error) {
                        Toast.makeText(MainActivity.this, "Fail authorization with Facebook message: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .init();
    }

    public void onBtnGooglePlusClick(View view) {
        GoogleOAuth.login(this)
                .setClientId(OAuthCredential.GOOGLE_CLIENT_ID)
                .setClientSecret(OAuthCredential.GOOGLE_CLIENT_SECRET)
                .setRedirectUri(OAuthCredential.GOOGLE_REDIRECT_URI)
                .setCallback(new OnLoginCallback() {
                    @Override
                    public void onSuccess(String token, SocialUser user) {
                        Toast.makeText(MainActivity.this, "Receive GooglePlus token: " + token, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception error) {
                        Toast.makeText(MainActivity.this, "Fail authorization with GooglePlus message: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .init();
    }

    public void onBtnTwitterClick(View view) {
        TwitterOAuth.login(this)
                .setClientId(OAuthCredential.TWITTER_CLIENT_ID)
                .setClientSecret(OAuthCredential.TWITTER_CLIENT_SECRET)
                .setRedirectUri(OAuthCredential.TWITTER_REDIRECT_URI)
                .setCallback(new OnLoginCallback() {
                    @Override
                    public void onSuccess(String token, SocialUser user) {
                        Toast.makeText(MainActivity.this, "Receive GooglePlus token: " + token, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Exception error) {
                        Toast.makeText(MainActivity.this, "Fail authorization with GooglePlus message: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .init();
    }
}
