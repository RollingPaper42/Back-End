package com.strcat.board;

import com.strcat.domain.Board;
import com.strcat.domain.BoardGroup;
import com.strcat.domain.Content;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardGroupReqDto;
import com.strcat.dto.ReadBoardGroupResDto;
import com.strcat.dto.ReadBoardGroupSummaryResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardGroupRepository;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.ContentRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.BoardGroupService;
import com.strcat.service.BoardService;
import com.strcat.service.UserService;
import com.strcat.util.JwtUtils;
import com.strcat.util.SecureDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@SuppressWarnings("NonAsciiCharacters")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BoardGroupServiceTest {
    private final BoardGroupService boardGroupService;
    private final BoardGroupRepository boardGroupRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final SecureDataUtils secureDataUtils;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private String token;
    private User user;

    @Autowired
    public BoardGroupServiceTest(BoardGroupRepository boardGroupRepository,
                                 UserRepository userRepository,
                                 BoardRepository boardRepository,
                                 ContentRepository contentRepository) {
        this.boardGroupRepository = boardGroupRepository;
        this.secureDataUtils = new SecureDataUtils("MyTestCode-32CharacterTestAPIKey");
        this.jwtUtils = new JwtUtils("testtesttesttesttesttesttesttesttesttest");
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.contentRepository = contentRepository;
        UserService userService = new UserService(userRepository, jwtUtils);
        this.boardService = new BoardService(boardRepository, boardGroupRepository, secureDataUtils, userService,
                jwtUtils);
        this.boardGroupService = new BoardGroupService(boardGroupRepository, boardService, userService, secureDataUtils,
                jwtUtils);
    }

    @BeforeEach
    public void beforeEach() {
        User user = new User();
        this.user = userRepository.save(user);
        this.token = "Bearer " + jwtUtils.createJwtToken(user.getId().toString());
    }

    @Nested
    class 성공 {
        @Test
        public void 생성() {
            //given
            CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");

            //when
            String encryptedId = boardGroupService.create(dto, token);
            BoardGroup result = boardGroupRepository.findById(secureDataUtils.decrypt(encryptedId))
                    .orElse(new BoardGroup("fail", user));

            //then
            Assertions.assertEquals(dto.getTitle(), result.getTitle());
        }

        @Test
        public void 그룹주인일때조회() {
            //given
            CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");
            String encryptedId = boardGroupService.create(dto, token);
            BoardGroup boardGroup = boardGroupRepository.findById(secureDataUtils.decrypt(encryptedId)).get();
            Board board = boardRepository.save(
                    new Board(boardGroup, "testBoard", "Green", user));
            Content content = contentRepository.save(new Content("testWriter", "test",
                    "example.org", board));

            boardGroup.getBoards().add(board);
            board.getContents().add(content);

            ReadBoardGroupResDto expect = new ReadBoardGroupResDto("testGroup", boardGroup.getEncryptedId(),
                    true, boardService.fetchBoardResponses(boardGroup.getBoards()));

            //when
            ReadBoardGroupResDto result = boardGroupService.readBoardGroup(encryptedId, token);

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 그룹주인아닌조회() {
            //given
            CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");
            String encryptedId = boardGroupService.create(dto, token);
            BoardGroup boardGroup = boardGroupRepository.findById(secureDataUtils.decrypt(encryptedId)).get();
            Board board = boardRepository.save(
                    new Board(boardGroup, "testBoard", "Green", user));
            Content content = contentRepository.save(new Content("testWriter", "test",
                    "example.org", board));
            boardGroup.getBoards().add(board);
            board.getContents().add(content);

            User user2 = new User();
            userRepository.save(user2);
            String token2 = "Bearer " + jwtUtils.createJwtToken(user2.getId().toString());

            ReadBoardGroupResDto expect = new ReadBoardGroupResDto("testGroup", boardGroup.getEncryptedId(),
                    false, boardService.fetchBoardResponses(boardGroup.getBoards()));

            //when
            ReadBoardGroupResDto result = boardGroupService.readBoardGroup(encryptedId, token2);

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 보드존재요약() {
            //given
            CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");
            String encryptedId = boardGroupService.create(dto, token);
            BoardGroup boardGroup = boardGroupRepository.findById(secureDataUtils.decrypt(encryptedId)).get();
            Board board = boardRepository.save(
                    new Board(boardGroup, "testBoard", "Green", user));
            Content content = contentRepository.save(new Content("testWriter", "test", "example.org", board));

            boardGroup.getBoards().add(board);
            board.getContents().add(content);

            ReadBoardGroupSummaryResDto expect = new ReadBoardGroupSummaryResDto("testGroup", 1L, 1, 4L);

            //when
            ReadBoardGroupSummaryResDto result = boardGroupService.readSummary(encryptedId, token);

            //then
            Assertions.assertEquals(expect, result);
        }

        @Test
        public void 보드없이요약() {
            //given
            CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");
            String encryptedId = boardGroupService.create(dto, token);

            ReadBoardGroupSummaryResDto expect = new ReadBoardGroupSummaryResDto("testGroup", 0L, 0, 0L);

            //when
            ReadBoardGroupSummaryResDto result = boardGroupService.readSummary(encryptedId, token);

            //then
            Assertions.assertEquals(expect, result);
        }
    }

    @Nested
    class 잘못된토큰인경우실패 {
        @ParameterizedTest
        @MethodSource("com.strcat.utils.TestUtils#generateInvalidToken")
        public void 생성(String invalidToken) {
            //given
            CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");

            //when
            Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardGroupService.create(dto, invalidToken)
            );
            Assertions.assertEquals("잘못된 토큰 형식입니다.", exception.getMessage());
        }

        @ParameterizedTest
        @MethodSource("com.strcat.utils.TestUtils#generateInvalidToken")
        public void 요약(String invalidToken) {
            //given
            String validEncryptedId = secureDataUtils.encrypt(1L);

            //when
            Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardGroupService.readSummary(validEncryptedId, invalidToken)
            );
            Assertions.assertEquals("잘못된 토큰 형식입니다.", exception.getMessage());
        }
    }

    @Nested
    class 존재하지않는유저인경우실패 {
        @Test
        public void 생성() {
            //given
            CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");
            String validNotExistToken = jwtUtils.createJwtToken("12345");

            //when
            Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardGroupService.create(dto, "Bearer " + validNotExistToken)
            );
            Assertions.assertEquals("유저가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        public void 요약() {
            //given
            String validNotExistToken = jwtUtils.createJwtToken("12345");
            String validEncryptedId = secureDataUtils.encrypt(1L);

            //when
            Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardGroupService.readSummary(validEncryptedId, "Bearer " + validNotExistToken)
            );
            Assertions.assertEquals("유저가 존재하지 않습니다.", exception.getMessage());
        }
    }

    @Nested
    class 보드그룹이존재하지않는경우실패 {
        @Test
        public void 조회() {
            //given
            String validNotExistEncryptedId = secureDataUtils.encrypt(12345L);

            //when
            Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardGroupService.readBoardGroup(validNotExistEncryptedId, token)
            );
            Assertions.assertEquals("존재하지 않는 보드 그룹입니다.", exception.getMessage());
        }

        @Test
        public void 요약() {
            //given
            String validNotExistEncryptedId = secureDataUtils.encrypt(12345L);

            //when
            Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                    //then
                    boardGroupService.readSummary(validNotExistEncryptedId, token)
            );
            Assertions.assertEquals("존재하지 않는 보드 그룹입니다.", exception.getMessage());
        }
    }
}
