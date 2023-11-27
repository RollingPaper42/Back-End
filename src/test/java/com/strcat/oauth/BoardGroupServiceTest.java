package com.strcat.oauth;

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
import com.strcat.service.UserService;
import com.strcat.util.AesSecretUtils;
import com.strcat.util.JwtUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    private final BoardRepository boardRepository;
    private final AesSecretUtils aesSecretUtils;
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
        this.aesSecretUtils = new AesSecretUtils("MyTestCode-32CharacterTestAPIKey");
        this.jwtUtils = new JwtUtils("testtesttesttesttesttesttesttesttesttest");
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.contentRepository = contentRepository;
        UserService userService = new UserService(userRepository, jwtUtils);
        this.boardGroupService = new BoardGroupService(boardGroupRepository, userService, aesSecretUtils);
    }

    @BeforeEach
    public void beforeEach() {
        User user = new User();
        this.user = userRepository.save(user);
        this.token = "Bearer " + jwtUtils.createJwtToken(user.getId().toString());
    }

    @Test
    public void 생성성공() {
        //given
        CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");

        //when
        String encryptedId = boardGroupService.create(dto, token);
        BoardGroup result = boardGroupRepository.findById(aesSecretUtils.decrypt(encryptedId))
                .orElse(new BoardGroup("fail", user));

        //then
        Assertions.assertEquals(dto.getTitle(), result.getTitle());
    }

    @Test
    public void 조회성공() {
        //given
        CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");
        String encryptedId = boardGroupService.create(dto, token);
        BoardGroup boardGroup = boardGroupRepository.findById(aesSecretUtils.decrypt(encryptedId)).get();
        Board board = boardRepository.save(
                new Board(boardGroup, "testBoard", "Green", user));
        Content content = contentRepository.save(new Content("testWriter", "test", "example.org", board));

        boardGroup.getBoards().add(board);
        board.getContents().add(content);

        ReadBoardGroupResDto expect = new ReadBoardGroupResDto("testGroup", true, List.of(board));

        //when
        ReadBoardGroupResDto result = boardGroupService.readBoardGroup(encryptedId, token);

        //then
        Assertions.assertEquals(expect, result);
    }

    @Test
    public void 보드존재요약성공() {
        //given
        CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");
        String encryptedId = boardGroupService.create(dto, token);
        BoardGroup boardGroup = boardGroupRepository.findById(aesSecretUtils.decrypt(encryptedId)).get();
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
    public void 보드없이요약성공() {
        //given
        CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");
        String encryptedId = boardGroupService.create(dto, token);

        ReadBoardGroupSummaryResDto expect = new ReadBoardGroupSummaryResDto("testGroup", 0L, 0, 0L);

        //when
        ReadBoardGroupSummaryResDto result = boardGroupService.readSummary(encryptedId, token);

        //then
        Assertions.assertEquals(expect, result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "01", "012", "0123", "01234", "012345", "0123456", "01234567", "012345678"})
    public void 잘못된토큰생성실패(String invalidToken) {
        //given
        CreateBoardGroupReqDto dto = new CreateBoardGroupReqDto("testGroup");

        //when
        Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                //then
                boardGroupService.create(dto, invalidToken)
        );
        Assertions.assertEquals("잘못된 토큰 형식입니다.", exception.getMessage());
    }

    @Test
    public void 유저가없는경우생성실패() {
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
    public void 유저가없는경우조회실패() {
        //given
        String validNotExistToken = jwtUtils.createJwtToken("12345");
        String validEncryptedId = aesSecretUtils.encrypt(1L);

        //when
        Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                //then
                boardGroupService.readBoardGroup(validEncryptedId, "Bearer " + validNotExistToken)
        );
        Assertions.assertEquals("유저가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    public void 유저가없는경우요약실패() {
        //given
        String validNotExistToken = jwtUtils.createJwtToken("12345");
        String validEncryptedId = aesSecretUtils.encrypt(1L);

        //when
        Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                //then
                boardGroupService.readSummary(validEncryptedId, "Bearer " + validNotExistToken)
        );
        Assertions.assertEquals("유저가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    public void 보드그룹이존재하지않아조회실패() {
        //given
        String validNotExistEncryptedId = aesSecretUtils.encrypt(12345L);

        //when
        Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                //then
                boardGroupService.readBoardGroup(validNotExistEncryptedId, token)
        );
        Assertions.assertEquals("존재하지 않는 보드 그룹입니다.", exception.getMessage());
    }

    @Test
    public void 보드그룹이존재하지않아요약실패() {
        //given
        String validNotExistEncryptedId = aesSecretUtils.encrypt(12345L);

        //when
        Exception exception = Assertions.assertThrows(NotAcceptableException.class, () ->
                //then
                boardGroupService.readSummary(validNotExistEncryptedId, token)
        );
        Assertions.assertEquals("존재하지 않는 보드 그룹입니다.", exception.getMessage());
    }
}
