package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.OAuthUserRepository;
import com.strcat.repository.UserRepository;
import com.strcat.util.JwtUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public void createBoard(CreateBoardReqDto dto, String token) {
        Long userId = Long.parseLong(jwtUtils.parseUserId(token));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotAcceptableException();
        }

        // TODO: group id 유효성 검사

        boardRepository.save(new Board(dto.getTitle(), dto.getBackgroundColor(), user.get()));
    }
}
