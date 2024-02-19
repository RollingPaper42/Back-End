package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.History;
import com.strcat.domain.User;
import com.strcat.dto.HistoryDto;
import com.strcat.dto.HistoryItem;
import com.strcat.dto.ReadMyInfoResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.HistoryRepository;
import com.strcat.repository.UserRepository;
import com.strcat.usecase.RecordHistoryUseCase;
import com.strcat.util.JwtUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final RecordHistoryUseCase recordHistoryUseCase;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepository userRepository, BoardRepository boardRepository,
                       HistoryRepository historyRepository,
                       JwtUtils jwtUtils) {

        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.recordHistoryUseCase = new RecordHistoryUseCase(userRepository, boardRepository, historyRepository);
        this.jwtUtils = jwtUtils;
    }

    public boolean isLogin(String token) {
        String tokenCode = jwtUtils.removeBearerString(token);
        if (!jwtUtils.isValidateToken(tokenCode)) {
            return false;
        }

        Long userId = jwtUtils.parseUserId(tokenCode);
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent();
    }

    public Optional<User> validate(String rawToken) {
        String token = jwtUtils.exportToken(rawToken);

        if (jwtUtils.isValidateToken(token)) {
            Long userId = jwtUtils.parseUserId(token);

            return userRepository.findById(userId);
        }

        return Optional.empty();
    }

    public List<ReadMyInfoResDto> readMyBoardInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotAcceptableException("유저가 존재하지 않습니다."));
        List<Board> boards = user.getBoards();
        return boards.stream()
                .map(Board::toReadMyInfoResDto)
                .collect(Collectors.toList());
    }


    public HistoryDto readMyBoardHistory(Long userId) {
        List<History> histories = historyRepository.findByUserIdOrderByVisitedAtAsc(userId);
        List<HistoryItem> historyItems = histories.stream().map(History::toDto).toList();

        return new HistoryDto(historyItems);
    }

    @Transactional
    public HistoryDto postMyBoardHistory(Long userId, HistoryDto dto) {
        List<History> result = recordHistoryUseCase.write(userId, dto.history());

        return new HistoryDto(result.stream()
                .map((history -> new HistoryItem(history.getBoard().getEncryptedId(), history.getBoard().getTitle(),
                        history.getVisitedAt()))).toList());
    }
}