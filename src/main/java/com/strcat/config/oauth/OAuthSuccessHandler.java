package com.strcat.config.oauth;

import com.strcat.service.OAuthUserService;
import com.strcat.util.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtils jwtUtils;
    private final OAuthUserService oAuthUserService;

    @Autowired
    public OAuthSuccessHandler(JwtUtils jwtUtils, OAuthUserService oAuthUserService) {
        this.jwtUtils = jwtUtils;
        this.oAuthUserService = oAuthUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String provider = request.getRequestURI().split("/")[4];
        String token = jwtUtils.createJwtToken(authentication.getName());
        Cookie cookie = new Cookie("token", token);

        oAuthUserService.signIn(authentication.getName(), provider);
        cookie.setPath("/login/success");
        response.addCookie(cookie);
        response.sendRedirect("http://rolling-eb-env.eba-pppydmmc.ap-northeast-2.elasticbeanstalk.com/login/success");
    }
}
