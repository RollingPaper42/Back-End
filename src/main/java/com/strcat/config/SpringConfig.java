package com.strcat.config;

import com.strcat.repository.OAuthUserRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.OAuthUserService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
public class SpringConfig {
    private final UserRepository userRepository;
    private final OAuthUserRepository oAuthUserRepository;

    @Autowired
    public SpringConfig(UserRepository userRepository, OAuthUserRepository oAuthUserRepository) {
        this.userRepository = userRepository;
        this.oAuthUserRepository = oAuthUserRepository;
    }

    @Bean
    OAuthUserService oAuthUserService() {
        return new OAuthUserService(userRepository, oAuthUserRepository);
    }
}
