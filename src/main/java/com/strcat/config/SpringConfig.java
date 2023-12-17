package com.strcat.config;

import com.strcat.aop.TimeTraceAop;
import com.strcat.repository.OAuthUserRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.OAuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
