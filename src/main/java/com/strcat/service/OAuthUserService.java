package com.strcat.service;

import com.strcat.domain.OAuthUser;
import com.strcat.domain.User;
import com.strcat.repository.OAuthUserRepository;
import com.strcat.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Transactional
public class OAuthUserService {
    private final UserRepository userRepository;
    private final OAuthUserRepository oAuthUserRepository;

    @Autowired
    public OAuthUserService(UserRepository userRepository, OAuthUserRepository oAuthUserRepository) {
        this.userRepository = userRepository;
        this.oAuthUserRepository = oAuthUserRepository;
    }

    public void signIn(String oAuthUserId, String provider) {
        int providerCode = OAuthProviderEnum.toEnum(provider);
        Optional<OAuthUser> signUser =
                oAuthUserRepository.findByOauthIdAndProvider(oAuthUserId, providerCode);

        if (signUser.isEmpty()) {
            User user = userRepository.save(new User(LocalDateTime.now()));
            oAuthUserRepository.save(new OAuthUser(user, providerCode, oAuthUserId));
        }
    }
}