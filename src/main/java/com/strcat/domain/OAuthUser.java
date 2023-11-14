package com.strcat.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class OAuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
    int provider;
    String oauthId;

    public OAuthUser(User user, int provider, String oauthId) {
        this.user = user;
        this.provider = provider;
        this.oauthId = oauthId;
    }
}
