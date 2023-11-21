package com.strcat.oauth;

import com.strcat.domain.Board;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.BoardService;
import com.strcat.service.UserService;
import com.strcat.util.AesSecretUtils;
import com.strcat.util.JwtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
    private final JwtUtils jwtUtils;
    private final AesSecretUtils aesSecretUtils;
    private String token;

    @Autowired
    public BoardServiceTest(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.jwtUtils = new JwtUtils("testtesttesttesttesttesttesttesttesttest");
        this.aesSecretUtils = new AesSecretUtils("MyTestCode-32CharacterTestAPIKey");
        UserService userService = new UserService(userRepository, jwtUtils);
        this.boardService = new BoardService(boardRepository, aesSecretUtils, userService);
    }

    @BeforeEach
    public void beforeEach() {
        final User user = new User();
        userRepository.save(user);
        token = "Bearer " + jwtUtils.createJwtToken(user.getId().toString());
    }

    @Test
    public void 보드생성성공() {
        //given
        CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");

        //when
        String encryptedUrl = boardService.createBoard(dto, token);
        Board board = boardRepository.findAll().get(0);

        //then
        Assertions.assertEquals(board.getId(), aesSecretUtils.decrypt(encryptedUrl));
    }

    @Test
    public void 제목길이가30자이상일때보드생성실패() {
        //given
        CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다가나다가나다가나다가나다가나다가나다가나다가나다가나다가", "Green");

        //when, then
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> boardService.createBoard(dto, token));
    }

    @Test
    public void 존재하지않는유저일때보드생성실패() {
        //given
        CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");

        //when, then
        Assertions.assertThrowsExactly(NotAcceptableException.class,
                () -> boardService.createBoard(dto, "invalid token"), "유저가 존재하지 않습니다.");
    }

    @Test
    public void 보드읽기성공() {
        //given
        CreateBoardReqDto dto = new CreateBoardReqDto(null, "가나다", "Green");
        String encryptedUrl = boardService.createBoard(dto, token);
        Board actual = boardRepository.findAll().get(0);

        //when
        Board result = boardService.readBoard(encryptedUrl);

        //then
        Assertions.assertEquals(actual, result);
    }
}
