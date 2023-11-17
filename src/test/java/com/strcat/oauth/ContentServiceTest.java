package com.strcat.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import com.strcat.domain.Board;
import com.strcat.domain.Content;
import com.strcat.domain.User;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.ContentRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.ContentService;
import com.strcat.util.AesSecretUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class ContentServiceTest {
    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private AesSecretUtils aesSecretUtils;
    private ContentService contentService;

    @Autowired
    public ContentServiceTest(ContentRepository contentRepository,
                              BoardRepository boardRepository,
                              UserRepository userRepository
    ) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.aesSecretUtils = new AesSecretUtils("MyTestCode-32CharacterTestAPIKey");
        this.contentService = new ContentService(contentRepository, boardRepository, aesSecretUtils);
    }

    @BeforeEach
    public void beforeEach() {
        User user = new User();
        userRepository.save(user);
        boardRepository.save(new Board("홍길동", user));
    }

    @Test
    public void content저장테스트() throws Exception {
        //given
        String encryptedBoardId = aesSecretUtils.encrypt(1L);
        CreateContentReqDto dto = new CreateContentReqDto("철수", "안녕! 만나서 반가워. 행복하길 바래~", "photo");

        //when
        Content result = contentService.create(dto, encryptedBoardId);

        //then
        assertThat(result.getBoard().getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("안녕! 만나서 반가워. 행복하길 바래~");
        assertThat(result.getWriter()).isEqualTo("철수");
    }
}
