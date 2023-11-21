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
        Long userId = Long.parseLong(jwtUtils.parseUserId(token.substring(7)));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotAcceptableException();
        }
        return user.get();
    }

    public boolean isLogin(String token) {
        Long userId = Long.parseLong(jwtUtils.parseUserId(token.substring(7)));
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent();
    }
}
