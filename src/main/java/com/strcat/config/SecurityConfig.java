package com.strcat.config;

import com.strcat.service.OAuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final OAuthUserService oAuthUserService;

    String[] WHITE_LIST = {
      "login/google",
      "login/kakao",
            "login/success",
    };

    @Autowired
    public SecurityConfig(OAuthUserService oAuthUserService) {
        this.oAuthUserService = oAuthUserService;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                .requestMatchers(WHITE_LIST).permitAll()
                .anyRequest().authenticated()
        ).oauth2Login((oauth) -> oauth
                .userInfoEndpoint((userInfo) -> userInfo
                        .userService(oAuthUserService))
                        .successHandler((request, response, authentication) -> {
                            System.out.println("login Success");
                            response.sendRedirect("http://localhost:8080/login/success");
                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("login failure");
                        })
                .permitAll()
        ).logout(LogoutConfigurer::permitAll)
                .sessionManagement((sessionManager) -> sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable);
//                .addFilterBefore(jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class)
        return http.build();
    }
}
