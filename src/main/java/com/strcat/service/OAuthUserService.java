package com.strcat.service;

import com.strcat.domain.OAuthUser;
import com.strcat.domain.User;
import com.strcat.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
public class OAuthUserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Autowired
    public OAuthUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("oauth login: " + userRequest.getClientRegistration().getRegistrationId());
        int providerCode = OAuthProviderEnum.changeCode(userRequest.getClientRegistration().getRegistrationId());
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String oAuthUserId = oAuth2User.getName();

        List<OAuthUser> signUser =
                userRepository.findByIdAndProvider(oAuthUserId, providerCode);

        if (signUser.isEmpty()) {
            User user = userRepository.save(new User(0, LocalDateTime.now()));
            OAuthUser oAuthUser = userRepository.save(new OAuthUser(0, user, providerCode, oAuthUserId));

            System.out.println("oAuthUser: " + oAuthUser.getOauthId());
            System.out.println("회원가입...");
        }
        System.out.println("로그인....");
        return oAuth2User;
    }
}