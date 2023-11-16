package com.strcat.repository;

import com.strcat.domain.OAuthUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    Optional<OAuthUser> findByOauthIdAndProvider(String oauthId, int provider);
}
