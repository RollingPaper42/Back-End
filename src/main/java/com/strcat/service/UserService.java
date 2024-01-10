package com.strcat.service;

import com.strcat.domain.User;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.UserRepository;
import com.strcat.util.JwtUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public User getUser(String token) {
        Long userId = jwtUtils.parseUserId(jwtUtils.removeBearerString(token));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotAcceptableException("유저가 존재하지 않습니다.");
        }
        return user.get();
    }

    public boolean isLogin(String token) {
        String tokenCode = jwtUtils.removeBearerString(token);
        if (!jwtUtils.isValidateToken(tokenCode)) {
            return false;
        }

        Long userId = jwtUtils.parseUserId(tokenCode);
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent();
    }

    public Optional<User> validate(String rawToken) {
        String token = jwtUtils.exportToken(rawToken);

        if (jwtUtils.isValidateToken(token)) {
            Long userId = jwtUtils.parseUserId(token);

            return userRepository.findById(userId);
        }

        return Optional.empty();
    }
}