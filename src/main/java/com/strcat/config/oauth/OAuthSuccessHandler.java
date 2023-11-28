package com.strcat.config.oauth;

import com.strcat.domain.User;
import com.strcat.service.OAuthUserService;
import com.strcat.util.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtils jwtUtils;
    private final OAuthUserService oAuthUserService;
    private final String REDIRECT_URI = "https://strcat.me/login/check";

    @Autowired
    public OAuthSuccessHandler(JwtUtils jwtUtils, OAuthUserService oAuthUserService) {
        this.jwtUtils = jwtUtils;
        this.oAuthUserService = oAuthUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String provider = request.getRequestURI().split("/")[4];
        User user = oAuthUserService.signIn(authentication.getName(), provider);
        String token = jwtUtils.createJwtToken(user.getId().toString());

        log.info("token: " + token);

        response.sendRedirect(String.format("%s?token=%s", REDIRECT_URI, token));
    }
}
