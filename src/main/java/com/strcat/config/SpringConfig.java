package com.strcat.config;

import com.strcat.aop.TimeTraceAop;
import com.strcat.repository.OAuthUserRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.OAuthUserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@RequiredArgsConstructor
public class SpringConfig {
    private final UserRepository userRepository;
    private final OAuthUserRepository oAuthUserRepository;

    @Bean
    public TimeTraceAop timeTraceAop() {
        return new TimeTraceAop();
    }

    @Bean
    OAuthUserService oAuthUserService() {
        return new OAuthUserService(userRepository, oAuthUserRepository);
    }
}
