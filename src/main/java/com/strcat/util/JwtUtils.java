package com.strcat.util;

import com.strcat.exception.NotAcceptableException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private final SecretKey secretKey;
    private final Long VALID_MINUTES = 300L; // TODO: 시간 변경 300 -> 60

    public String removeBearerString(String token) {
        if (token == null
                || token.isBlank()
                || token.length() < 7) {
            return "";
        }
        return token.substring(7);
    }

    public JwtUtils(@Value("${jwt.secret}") String secretKey) {
        byte[] encoded = Base64.getEncoder().encode(secretKey.getBytes());

        this.secretKey = Keys.hmacShaKeyFor(encoded);
    }

    public String exportToken(HttpServletRequest request) {
        String rawToken = request.getHeader("Authorization");

        if (rawToken == null
                || rawToken.isBlank()
                || rawToken.length() < 6) {
            return "";
        }

        String prefix = rawToken.substring(0, 6);
        String token = "";

        if (prefix.equals("Bearer")) {
            token = removeBearerString(rawToken);
        }
        return token;
    }

    public String parseUserId(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token).getPayload().getId();
        } catch (Exception e) {
            throw new NotAcceptableException("잘못된 토큰 형식입니다.");
        }
    }

    public boolean isValidateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload().getExpiration().after(new Date());
        } catch (Exception exception) {
            return false;
        }
    }

    public String createJwtToken(String userId) {
        return createJwt(userId);
    }

    private String createJwt(String userId) {
        long tokenValidTime = Duration.ofMinutes(VALID_MINUTES).toMillis();

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
