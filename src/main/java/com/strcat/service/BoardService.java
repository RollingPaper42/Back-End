package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.ReadBoardInfoResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
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
        Optional<User> user = getUser(token);

        // TODO: group id 유효성 검사

        Board board = boardRepository.save(new Board(dto.getTitle(), dto.getBackgroundColor(), user.get()));
        return aesSecretUtils.encrypt(board.getId());
    }

    // TODO: 삭제 예정
    public ReadBoardInfoResDto readBoardInfo(String encryptedBoardId) throws Exception {
        Board board = getBoard(encryptedBoardId);
        return new ReadBoardInfoResDto(board.getTitle(), board.getBackgroundColor());
    }

    public Board readBoard(String encryptedBoardId) throws Exception {
        return getBoard(encryptedBoardId);
    }

    public ReadBoardSummaryResDto readSummary(String encryptedBoardId, String token) throws Exception {
        getUser(token);
        Board board = getBoard(encryptedBoardId);

        ReadBoardSummaryResDto dto = new ReadBoardSummaryResDto(board.getTitle(), board.getContents().size(),
                board.calculateTotalContentLength());
        return dto;
    }

    private Board getBoard(String encryptedBoardId) throws Exception {
        Long boardId = aesSecretUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException();
        }
        return optionalBoard.get();
    }

    private Optional<User> getUser(String token) {
        Long userId = Long.parseLong(jwtUtils.parseUserId(token.substring(7)));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotAcceptableException();
        }
        return user;
    }
}
