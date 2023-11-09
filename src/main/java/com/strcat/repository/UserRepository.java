package com.strcat.repository;

import com.strcat.domain.OAuthUser;
import com.strcat.domain.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

public class UserRepository {
    private final EntityManager entityManager;

    @Autowired
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<User> save(User user) {
        Optional<User> result;

        try {
            entityManager.persist(user);

            result = Optional.of(user);
        } catch (Exception err) {
            result = Optional.empty();
        }

        return result;
    }

    public Optional<User> findById(long id) {
        Optional<User> user;

        try {
            user = Optional.of(entityManager.find(User.class, id));
        } catch (Exception err) {
            user = Optional.empty();
        }

        return user;
    }

    public Optional<OAuthUser> save(OAuthUser oAuthUser) {
        Optional<OAuthUser> result;

        try {
            entityManager.persist(oAuthUser);
            result = Optional.of(oAuthUser);
        } catch (Exception exception) {
            result = Optional.empty();
        }

        return result;
    }

    public Optional<OAuthUser> findByIdAndProvider(String id, int provider) {
        Optional<OAuthUser> result;

        try {
            String jpql = "SELECT u FROM OAuthUser u WHERE u.user.id = :id AND u.provider = :provider";

            result = Optional.of(entityManager.createQuery(jpql, OAuthUser.class).getSingleResult());
        } catch (Exception exception) {
            result = Optional.empty();
        }

        return result;
    }
}
