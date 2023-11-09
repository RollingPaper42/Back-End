package com.strcat.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtUtils {
    private final SecretKey secretKey;

    public JwtUtils(@Value("${jwt.secret}") String secretKey) {
        byte[] encoded = Base64.getEncoder().encode(secretKey.getBytes());

        this.secretKey = Keys.hmacShaKeyFor(encoded);
    }

    public String exportToken(HttpServletRequest request) {
        String rawToken = request.getHeader("Authorization");

        if (rawToken == null) {
            return "";
        }

        String bearer = rawToken.substring(0, 6);
        String token = "";

        if (bearer.equals("Bearer")) {
            token = rawToken.substring(7);
        }
        return token;
    }

    public String parseUserId(String token) {
       return Jwts.parser()
               .verifyWith(secretKey)
               .build()
               .parseSignedClaims(token).getPayload().getId();
    }

    public boolean isValidateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().getExpiration().after(new Date());
    }

    public String createJwtToken(String userId) {
        return createJwt(userId);
    }

    private String createJwt(String userId) {
        // 30분
        long tokenValidTime = Duration.ofMinutes(30).toMillis();

        return Jwts.builder()
                .header()
                .keyId("access")
                .and()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenValidTime))
                .id(userId)
                .signWith(secretKey)
                .compact();
    }
}
