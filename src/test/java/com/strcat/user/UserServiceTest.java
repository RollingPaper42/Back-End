package com.strcat.user;

import com.strcat.domain.Board;
import com.strcat.domain.History;
import com.strcat.domain.User;
import com.strcat.dto.HistoryDto;
import com.strcat.dto.HistoryItem;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.HistoryRepository;
import com.strcat.repository.UserRepository;
import com.strcat.service.UserService;
import com.strcat.util.JwtUtils;
import com.strcat.util.SecureDataUtils;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@SuppressWarnings("NonAsciiCharacters")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
@Transactional
public class UserServiceTest {
    private final UserService userService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final HistoryRepository historyRepository;
    private final SecureDataUtils secureDataUtils;
    private User me;
    private List<Board> boards;

    @Autowired
    public UserServiceTest(UserRepository userRepository, BoardRepository boardRepository,
                           HistoryRepository historyRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.secureDataUtils = new SecureDataUtils("testtesttesttesttesttesttesttest");
        JwtUtils jwtUtils = new JwtUtils("testtesttesttesttesttesttesttesttesttest");
        this.userService = new UserService(userRepository, boardRepository, historyRepository, jwtUtils);
    }

    @BeforeEach
    public void beforeEach() {
        User me = new User();
        User other = new User();

        this.me = userRepository.save(me);
        userRepository.save(other);

        for (int i = 1; i <= 10; ++i) {
            boardRepository.save(new Board("test_board" + i, "green", other, false));
        }

        this.boards = boardRepository.findAll();

        for (int i = 0; i < 10; ++i) {
            Board board = boards.get(i);
            board.setEncryptedId(secureDataUtils.encrypt(board.getId()));
        }
    }

    @Test
    public void 최근기록저장_성공() {
        //given
        List<HistoryItem> history = boards.stream()
                .map((board) -> new HistoryItem(board.getEncryptedId(), board.getTitle(), LocalDateTime.now()))
                .toList();
        HistoryDto historyDto = new HistoryDto(history);

        //when
        HistoryDto result = userService.postMyBoardHistory(me.getId(), historyDto);

        //then
        Assertions.assertEquals(historyDto, result);
    }

    @Test
    public void 최근기록조회_성공() {
        //given
        User user = userRepository.findById(me.getId()).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        List<HistoryItem> history = boards.stream()
                .map((board) -> new HistoryItem(board.getEncryptedId(), board.getTitle(), now)).toList();
        HistoryDto historyDto = new HistoryDto(history);
        user.setHistories(boards.stream().map((board) -> new History(user, board, now)).toList());
        userService.postMyBoardHistory(user.getId(), historyDto);

        //when
        HistoryDto result = userService.readMyBoardHistory(user.getId());

        //then
        Assertions.assertTrue(result.history().containsAll(historyDto.history()));
    }
}
