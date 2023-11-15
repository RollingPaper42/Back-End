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
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Transactional
@Slf4j
public class OAuthUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final OAuthUserRepository oAuthUserRepository;

    @Autowired
    public OAuthUserService(UserRepository userRepository, OAuthUserRepository oAuthUserRepository) {
        this.userRepository = userRepository;
        this.oAuthUserRepository = oAuthUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("oauth login: " + userRequest.getClientRegistration().getRegistrationId());
        int providerCode = OAuthProviderEnum.toEnum(userRequest.getClientRegistration().getRegistrationId());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oAuthUserId = oAuth2User.getName();

        Optional<OAuthUser> signUser =
                oAuthUserRepository.findByOauthIdAndProvider(oAuthUserId, providerCode);

        if (signUser.isEmpty()) {
            User user = userRepository.save(new User(LocalDateTime.now()));
            OAuthUser oAuthUser = oAuthUserRepository.save(new OAuthUser(user, providerCode, oAuthUserId));

            log.info("oAuthUser: " + oAuthUser.getOauthId());
            log.info("회원가입...");
        }
        log.info("로그인....");
        return oAuth2User;
    }
}