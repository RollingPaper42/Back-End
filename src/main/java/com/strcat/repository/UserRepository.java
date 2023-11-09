package com.strcat.repository;

import com.strcat.domain.OAuthUser;
import com.strcat.domain.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final EntityManager entityManager;

    @Autowired
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    public Optional<User> findById(long id) {
        User user = entityManager.find(User.class, id);

        return Optional.ofNullable(user);
    }

    public OAuthUser save(OAuthUser oAuthUser) {
        entityManager.persist(oAuthUser);

        return oAuthUser;
    }

    public List<OAuthUser> findByIdAndProvider(String id, int provider) {
        String jpql = "SELECT u FROM OAuthUser u WHERE u.oauthId = :id AND u.provider = :provider";

        List<OAuthUser> users = entityManager.createQuery(jpql, OAuthUser.class)
                .setParameter("id", id)
                .setParameter("provider", provider)
                .getResultList();

        return users;
    }
}
