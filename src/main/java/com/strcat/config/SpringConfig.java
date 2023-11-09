package com.strcat.config;

import com.strcat.repository.UserRepository;
import com.strcat.service.OAuthUserService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    EntityManager entityManager;

    @Autowired
    public SpringConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    OAuthUserService oAuthUserService() {
        return new OAuthUserService(userRepository());
    }

    @Bean
    UserRepository userRepository() {
        return new UserRepository(entityManager);
    }

}
