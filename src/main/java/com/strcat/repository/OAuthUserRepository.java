package com.strcat.repository;

import com.strcat.domain.OAuthUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthUserRepository extends JpaRepository<OAuthUser, Long> {
    @Override
    <S extends OAuthUser> S save(S entity);

    @Override
    Optional<OAuthUser> findById(Long aLong);

    @Override
    boolean existsById(Long aLong);

    @Override
    long count();

    @Override
    void deleteById(Long aLong);

    @Override
    void delete(OAuthUser entity);

    @Override
    void deleteAllById(Iterable<? extends Long> longs);

    @Override
    void deleteAll(Iterable<? extends OAuthUser> entities);

    @Override
    void deleteAll();

    Optional<OAuthUser> findByOauthIdAndProvider(String oauthId, int provider);
}
