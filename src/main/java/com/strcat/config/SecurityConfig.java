package com.strcat.config;

import com.strcat.config.oauth.JwtAuthFilter;
import com.strcat.config.oauth.JwtAuthenticationEntryPoint;
import com.strcat.config.oauth.OAuthFailureHandler;
import com.strcat.config.oauth.OAuthSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityConfig {
    private final OAuthSuccessHandler oAuthSuccessHandler;
    private final OAuthFailureHandler oAuthFailureHandler;
    private final WebConfig webConfig;
    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    String[] WHITE_LIST = {
            "login/*",
            "swagger-ui/index.html",
            "swagger-resources",
            "swagger-resources/**",
            "favicon.ico",
            "v3/api-docs/**",
            "swagger-ui/**",
            "boards/*",
            "boards/*/contents",
            "boards/*/contents/pictures",
            "board-groups/*"
    };

    @Autowired
    public SecurityConfig(OAuthSuccessHandler oAuthSuccessHandler,
                          OAuthFailureHandler oAuthFailureHandler, WebConfig webConfig,
                          JwtAuthFilter jwtAuthFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.oAuthSuccessHandler = oAuthSuccessHandler;
        this.oAuthFailureHandler = oAuthFailureHandler;
        this.webConfig = webConfig;
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
                ).oauth2Login((oauth) -> oauth
                        .userInfoEndpoint((userInfo) -> userInfo
                                .userService(new DefaultOAuth2UserService()))
                        .successHandler(oAuthSuccessHandler)
                        .failureHandler(oAuthFailureHandler)
                        .permitAll()
                ).logout(LogoutConfigurer::permitAll)
                .sessionManagement((sessionManager) -> sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilter(webConfig.corsFilter())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((httpSecurityExceptionHandlingConfigurer) -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );
        return http.build();
    }
}
