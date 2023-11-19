package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.ReadBoardInfoResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.UserRepository;
import com.strcat.util.AesSecretUtils;
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
    private final AesSecretUtils aesSecretUtils;

    public String createBoard(CreateBoardReqDto dto, String token) throws Exception {
        Long userId = Long.parseLong(jwtUtils.parseUserId(token.substring(7)));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotAcceptableException();
        }

        // TODO: group id 유효성 검사

        Board board = boardRepository.save(new Board(dto.getTitle(), dto.getBackgroundColor(), user.get()));
        return aesSecretUtils.encrypt(board.getId());
    }

    // TODO: 삭제 예정
    public ReadBoardInfoResDto readBoardInfo(String encryptedBoardId) throws Exception {
        Long boardId = aesSecretUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException();
        }
        Board board = optionalBoard.get();
        return new ReadBoardInfoResDto(board.getTitle(), board.getBackgroundColor());
    }

    public Board readBoard(String encryptedBoardId) throws Exception {
        Long boardId = aesSecretUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException();
        }
        return optionalBoard.get();

    }
}
