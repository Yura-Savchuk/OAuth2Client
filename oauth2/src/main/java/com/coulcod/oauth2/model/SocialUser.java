package com.coulcod.oauth2.model;

import com.coulcod.oauth2.oauth.OAuthProvider;

import java.io.Serializable;

public class SocialUser implements Serializable {
    private String id;
    private String name;
    private String email;
    private String pictureUrl;
    private String coverUrl;
    private OAuthProvider provider;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public OAuthProvider getProvider() {
        return provider;
    }

    public void setProvider(OAuthProvider provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return String.format(
                "[id: %s, name: %s, email: %s, pictureUrl: %s, coverUrl: %s, provider: %s]",
                id, name, email, pictureUrl, coverUrl, provider);
    }
}