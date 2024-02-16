package com.strcat.board;

import com.strcat.domain.Board;
import com.strcat.domain.Content;
import com.strcat.domain.User;
import com.strcat.dto.BoardResponse;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.ContentRepository;
import com.strcat.repository.HistoryRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.BoardService;
import com.strcat.usecase.RecordHistoryUseCase;
import com.strcat.util.JwtUtils;
import com.strcat.util.SecureDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
    private final SecureDataUtils secureDataUtils;
    private User user;

    @Autowired
    public BoardServiceTest(BoardRepository boardRepository, UserRepository userRepository,
                            ContentRepository contentRepository, HistoryRepository historyRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        JwtUtils jwtUtils = new JwtUtils("testtesttesttesttesttesttesttesttesttest");
        this.secureDataUtils = new SecureDataUtils("MyTestCode-32CharacterTestAPIKey");
        this.boardService = new BoardService(boardRepository, secureDataUtils, userRepository, new RecordHistoryUseCase(userRepository, boardRepository, historyRepository));
    }

    @BeforeEach
    public void beforeEach() {
        final User user = new User();
        this.user = userRepository.save(user);
    }

    @Nested
    class 성공 {
        @Test
        public void 생성() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto("가나다", "Green");

            //when
            String encryptedUrl = boardService.createBoard(dto, user.getId());
            Board board = boardRepository.findAll().get(0);

            //then
            Assertions.assertEquals(board.getId(), secureDataUtils.decrypt(encryptedUrl));
        }

        @Test
        public void 보드주인일때조회() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto("가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, user.getId());
            Board board = boardRepository.findAll().get(0);
            ReadBoardResDto expect = new ReadBoardResDto(true,
                    new BoardResponse(board.getEncryptedId(), board.getTitle(), board.getTheme(), board.getContents()));

            //when
            ReadBoardResDto result = boardService.readBoard(encryptedUrl, user.getId());

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 보드주인아닌조회() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto("가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, user.getId());
            Board board = boardRepository.findAll().get(0);
            User user2 = new User();
            userRepository.save(user2);

            ReadBoardResDto expect = new ReadBoardResDto(false,
                    new BoardResponse(board.getEncryptedId(), board.getTitle(), board.getTheme(), board.getContents()));

            //when
            ReadBoardResDto result = boardService.readBoard(encryptedUrl, user2.getId());

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 컨텐츠없는요약() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto("가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, user.getId());
            ReadBoardSummaryResDto expect = new ReadBoardSummaryResDto("가나다", "Green", 0, 0L);

            //when
            ReadBoardSummaryResDto result = boardService.readSummary(encryptedUrl);

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 컨텐츠존재요약() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto("가나다", "Green");
            String encryptedUrl = boardService.createBoard(dto, user.getId());
            ReadBoardResDto boardRes = boardService.readBoard(encryptedUrl, user.getId());
            Board board = boardRepository.findByEncryptedId(encryptedUrl).orElseThrow();
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
        public void 제목길이30자이상보드생성() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto("가나다가나다가나다가나다가나다가나다가나다가나다가나다가나다가", "Green");

            //when
            Throwable thrown = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                    //then
                    boardService.createBoard(dto, user.getId())
            );
            Assertions.assertTrue(thrown.getMessage().contains("Data too long"));
        }

        @Test
        public void 존재하지않는보드조회() {
            //given
            CreateBoardReqDto dto = new CreateBoardReqDto("가나다", "Green");
            boardService.createBoard(dto, user.getId());
            String validNotExistUrl = secureDataUtils.encrypt(Long.MAX_VALUE);

            //when
            Throwable thrown = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardService.readBoard(validNotExistUrl, user.getId())
            );
            Assertions.assertEquals("존재하지 않는 보드입니다.", thrown.getMessage());
        }
    }
}
