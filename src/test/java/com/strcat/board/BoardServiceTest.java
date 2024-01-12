package com.strcat.board;

import com.strcat.domain.Board;
import com.strcat.domain.Content;
import com.strcat.domain.User;
import com.strcat.dto.BoardResponse;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardGroupRepository;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.ContentRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.BoardService;
import com.strcat.service.UserService;
import com.strcat.util.JwtUtils;
import com.strcat.util.SecureDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@SuppressWarnings("NonAsciiCharacters")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class BoardServiceTest {
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final JwtUtils jwtUtils;
    private final SecureDataUtils secureDataUtils;
    private String token;

    @Autowired
    public BoardServiceTest(BoardRepository boardRepository, UserRepository userRepository,
                            ContentRepository contentRepository, BoardGroupRepository boardGroupRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.jwtUtils = new JwtUtils("testtesttesttesttesttesttesttesttesttest");
        this.secureDataUtils = new SecureDataUtils("MyTestCode-32CharacterTestAPIKey");
        UserService userService = new UserService(userRepository, jwtUtils);
        this.boardService = new BoardService(boardRepository, boardGroupRepository, secureDataUtils, userService,
                jwtUtils);
    }

    @BeforeEach
    public void beforeEach() {
        final User user = new User();
        userRepository.save(user);
        token = "Bearer " + jwtUtils.createJwtToken(user.getId().toString());
    }

    @Nested
    class 성공 {
        @Test
        public void 생성() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");

            //when
            String encryptedUrl = boardService.createBoard(dto, token);
            Board board = boardRepository.findAll().get(0);

            //then
            Assertions.assertEquals(board.getId(), secureDataUtils.decrypt(encryptedUrl));
        }

        @Test
        public void 조회_isOwner_true() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, token);
            Board board = boardRepository.findAll().get(0);
            ReadBoardResDto expect = new ReadBoardResDto(true,
                    new BoardResponse(board.getEncryptedId(), board.getTitle(), board.getTheme(), board.getContents()));

            //when
            ReadBoardResDto result = boardService.readBoard(encryptedUrl, token);

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 조회_isOwner_false() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, token);
            Board board = boardRepository.findAll().get(0);
            User user2 = new User();
            userRepository.save(user2);
            String token2 = "Bearer " + jwtUtils.createJwtToken(user2.getId().toString());

            ReadBoardResDto expect = new ReadBoardResDto(false,
                    new BoardResponse(board.getEncryptedId(), board.getTitle(), board.getTheme(), board.getContents()));

            //when
            ReadBoardResDto result = boardService.readBoard(encryptedUrl, token2);

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 컨텐츠없는경우요약조회() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, token);
            ReadBoardSummaryResDto expect = new ReadBoardSummaryResDto("가나다", "Green", 0, 0L);

            //when
            ReadBoardSummaryResDto result = boardService.readSummary(encryptedUrl);

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 컨텐츠존재하는경우요약조회() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, token);
            ReadBoardResDto boardRes = boardService.readBoard(encryptedUrl, token);
            Board board = boardService.getBoard(encryptedUrl);
            Content content = new Content("test", "test", "test.jpg", board);
            contentRepository.save(content);
            boardRes.getBoard().getContents().add(content); // contents에 자동으로 content 추가가 안됨...
            ReadBoardSummaryResDto expect = new ReadBoardSummaryResDto("가나다", "Green", 1, 4L);

            //when
            ReadBoardSummaryResDto result = boardService.readSummary(encryptedUrl);

            //then
            Assertions.assertEquals(expect, result);
        }
    }

    @Nested
    class 실패 {
        @Test
        public void 제목길이가30자이상일때보드생성실패() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다가나다가나다가나다가나다가나다가나다가나다가나다가나다가", "Green");

            //when
            Throwable thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                    //then
                    boardService.createBoard(dto, token)
            );
            Assertions.assertTrue(thrown.getMessage().contains("Data too long"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "1", "12", "123", "1234", "12345", "123456", "1234567", "1234567890"})
        public void 잘못된토큰일때보드생성실패(String invalidToken) {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");

            //when
            Throwable thrown = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardService.createBoard(dto, invalidToken)
            );
            Assertions.assertEquals("잘못된 토큰 형식입니다.", thrown.getMessage());
        }

        @Test
        public void 잘못된URL보드읽기실패() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, token);
            String invalidUrl = encryptedUrl.substring(4);

            //when
            Throwable thrown = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardService.readBoard(invalidUrl, token)
            );
            Assertions.assertEquals("복호화에 실패했습니다.", thrown.getMessage());
        }

        @Test
        public void 존재하지않는보드읽기실패() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");
            boardService.createBoard(dto, token);
            String validNotExistUrl = secureDataUtils.encrypt(Long.MAX_VALUE);

            //when
            Throwable thrown = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardService.readBoard(validNotExistUrl, token)
            );
            Assertions.assertEquals("존재하지 않는 보드입니다.", thrown.getMessage());
        }
    }
}
