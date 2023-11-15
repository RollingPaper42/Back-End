package com.strcat.domain;

import jakarta.persistence.Column;
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
    Long id;

    @Column(nullable = false)
    Integer provider;

    @Column(nullable = false)
    String oauthId;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    public OAuthUser(User user, int provider, String oauthId) {
        this.user = user;
        this.provider = provider;
        this.oauthId = oauthId;
    }
}
