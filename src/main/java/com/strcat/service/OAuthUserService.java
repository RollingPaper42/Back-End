package com.strcat.service;

import com.strcat.domain.OAuthUser;
import com.strcat.domain.User;
import com.strcat.repository.OAuthUserRepository;
import com.strcat.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
@Slf4j
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
            User user = userRepository.save(new User());
            oAuthUserRepository.save(new OAuthUser(user, providerCode, oAuthUserId));
            log.info("회원가입....");
        }
        log.info("로그인....");
    }
}