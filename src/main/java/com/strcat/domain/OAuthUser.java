package com.strcat.domain;

import jakarta.persistence.*;

@Entity
public class OAuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
    int provider;
    String oauthId;

    public OAuthUser(long id, User user, int provider, String oauthId) {
        this.id = id;
        this.user = user;
        this.provider = provider;
        this.oauthId = oauthId;
    }

    public OAuthUser() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getProvider() {
        return provider;
    }

    public void setProvider(int provider) {
        this.provider = provider;
    }

    public String getOauthId() {
        return oauthId;
    }

    public void setOauthId(String oauthId) {
        this.oauthId = oauthId;
    }
}
