package com.strcat.config.oauth;

import com.strcat.domain.User;
import com.strcat.service.OAuthUserService;
import com.strcat.util.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtils jwtUtils;
    private final OAuthUserService oAuthUserService;
    private final String REDIRECT_URI;

    @Autowired
    public OAuthSuccessHandler(JwtUtils jwtUtils, OAuthUserService oAuthUserService,
                               @Value("${redirect_uri}") String REDIRECT_URI) {
        this.jwtUtils = jwtUtils;
        this.oAuthUserService = oAuthUserService;
        this.REDIRECT_URI = REDIRECT_URI;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String provider = request.getRequestURI().split("/")[4];
        User user = oAuthUserService.signIn(authentication.getName(), provider);
        String token = jwtUtils.createJwtToken(user.getId().toString());
        String cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(".strcat.me")
                .build()
                .toString();

        log.info("token: " + token);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        response.sendRedirect(String.format("%s?token=%s", REDIRECT_URI, token));
    }
}
