package com.strcat.oauth;

import com.strcat.domain.OAuthUser;
import com.strcat.repository.OAuthUserRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.OAuthProviderEnum;
import com.strcat.service.OAuthUserService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class OAuthUserServiceTest {
    private OAuthUserService oAuthUserService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OAuthUserRepository oAuthUserRepository;

    @BeforeEach
    public void before() {
        oAuthUserService = new OAuthUserService(userRepository, oAuthUserRepository);
    }

    @Test
    public void GoogleLogin성공테스트() {
        //given
        String oauthUserId = "123";
        String provider = "google";

        //when
        oAuthUserService.signIn(oauthUserId, provider);
        Optional<OAuthUser> result = oAuthUserRepository.findByOauthIdAndProvider("123", OAuthProviderEnum.toEnum("google"));

        //then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertTrue(userRepository.existsById(result.get().getUser().getId()));
    }

    @Test
    public void KakaoLogin성공테스트() {
        //given
        String oauthUserId = "123";
        String provider = "kakao";

        //when
        oAuthUserService.signIn(oauthUserId, provider);
        Optional<OAuthUser> result = oAuthUserRepository.findByOauthIdAndProvider("123", OAuthProviderEnum.toEnum("kakao"));

        //then
        Assertions.assertTrue(result.isPresent());
        Assertions.assertTrue(userRepository.existsById(result.get().getUser().getId()));
    }
}
