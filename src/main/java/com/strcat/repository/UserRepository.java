package com.strcat.repository;

import com.strcat.domain.User;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @Override
    <S extends User> S save(S entity);

    @Override
    Optional<User> findById(Long aLong);

    @Override
    boolean existsById(Long aLong);

    @Override
    long count();

    @Override
    void deleteById(Long aLong);

    @Override
    void delete(User entity);

    @Override
    void deleteAllById(Iterable<? extends Long> longs);

    @Override
    void deleteAll(Iterable<? extends User> entities);

    @Override
    void deleteAll();
}

//public class UserRepository {
//    private final EntityManager entityManager;
//
//    @Autowired
//    public UserRepository(EntityManager entityManager) {
//        this.entityManager = entityManager;
//    }
//
//    public User save(User user) {
//        entityManager.persist(user);
//        return user;
//    }
//
//    public Optional<User> findById(long id) {
//        User user = entityManager.find(User.class, id);
//
//        return Optional.ofNullable(user);
//    }
//
//    public OAuthUser save(OAuthUser oAuthUser) {
//        entityManager.persist(oAuthUser);
//
//        return oAuthUser;
//    }
//
//    public List<OAuthUser> findByIdAndProvider(String id, int provider) {
//        String jpql = "SELECT u FROM OAuthUser u WHERE u.oauthId = :id AND u.provider = :provider";
//
//        List<OAuthUser> users = entityManager.createQuery(jpql, OAuthUser.class)
//                .setParameter("id", id)
//                .setParameter("provider", provider)
//                .getResultList();
//
//        return users;
//    }
//}
