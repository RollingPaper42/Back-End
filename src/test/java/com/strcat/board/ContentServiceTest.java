package com.strcat.board;

import static org.assertj.core.api.Assertions.assertThat;

import com.strcat.domain.Board;
import com.strcat.domain.Content;
import com.strcat.domain.User;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.dto.DeleteContentReqDto;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.ContentRepository;
import com.strcat.repository.PictureRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.ContentService;
import com.strcat.util.SecureDataUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class ContentServiceTest {
    private BoardRepository boardRepository;
    private UserRepository userRepository;
    private SecureDataUtils secureDataUtils;
    private ContentService contentService;
    private ContentRepository contentRepository;

    @Autowired
    public ContentServiceTest(ContentRepository contentRepository,
                              BoardRepository boardRepository,
                              UserRepository userRepository
    ) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.secureDataUtils = new SecureDataUtils("MyTestCode-32CharacterTestAPIKey");
        this.contentService = new ContentService(contentRepository, boardRepository, new PictureRepository("s3"),
                secureDataUtils);
    }

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        User user = new User();
        userRepository.save(user);
        boardRepository.save(new Board("홍길동", "dsfds", user));
    }

    @Test
    @DisplayName("처음 컨텐츠에 저장한 데이터대로 저장 되지 않으면 테스트가 실패합니다.")
    public void content저장테스트() throws Exception {
        //given
        Board board = boardRepository.findAll().get(0);
        String encryptedBoardId = secureDataUtils.encrypt(board.getId());
        CreateContentReqDto dto = new CreateContentReqDto("철수", "안녕! 만나서 반가워. 행복하길 바래~", "photo");

        //when
        Long id = contentService.create(dto, encryptedBoardId);
        Content result = contentRepository.findById(id).get();

        //then
        assertThat(result.getBoard().getId()).isEqualTo(board.getId());
        assertThat(result.getText()).isEqualTo("안녕! 만나서 반가워. 행복하길 바래~");
        assertThat(result.getWriter()).isEqualTo("철수");
    }

    @Test
    @DisplayName("컨텐츠 저장 시 writer 길이가 초과하면 예외가 발생합니다.")
    public void content_writer_길이_테스트() throws Exception {
        //given
        Board board = boardRepository.findAll().get(0);
        String encryptedBoardId = secureDataUtils.encrypt(board.getId());
        CreateContentReqDto dto = new CreateContentReqDto(
                "0123456789012345678901234567890", "안녕! 만나서 반가워. 행복하길 바래~", "photo");

        //then
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            contentService.create(dto, encryptedBoardId);
        });
    }

    @Test
    @DisplayName("컨텐츠 삭제 성공")
    public void content_삭제_성공() {
        //given
        User user = userRepository.findAll().get(0);
        Board board = boardRepository.save(new Board("asd", "asd", user));
        String encryptedBoardId = secureDataUtils.encrypt(board.getId());
        CreateContentReqDto dto = new CreateContentReqDto(
                "12345", "안녕! 만나서 반가워. 행복하길 바래~", "photo");
        Content content = contentRepository.save(new Content(dto.getWriter(), dto.getText(), dto.getPhotoUrl(), board));

        //when
        Board result = contentService.deleteContent(encryptedBoardId, new DeleteContentReqDto(List.of(content.getId())),
                user);

        //then
        Assertions.assertEquals(
                List.of(),
                result.getContents());
    }

}
