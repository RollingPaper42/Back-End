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


    public String createBoard(CreateBoardReqDto dto, String token) {
        User user = getUser(token);

        // TODO: group id 유효성 검사

        Board board = boardRepository.save(new Board(dto.getTitle(), dto.getBackgroundColor(), user));
        return aesSecretUtils.encrypt(board.getId());
    }

    // TODO: 삭제 예정
    public ReadBoardInfoResDto readBoardInfo(String encryptedBoardId) {
        Board board = getBoard(encryptedBoardId);
        return new ReadBoardInfoResDto(board.getTitle(), board.getBackgroundColor());
    }

    public Board readBoard(String encryptedBoardId) {
        return getBoard(encryptedBoardId);
    }

    public ReadBoardSummaryResDto readSummary(String encryptedBoardId, String token) {
        getUser(token);
        Board board = getBoard(encryptedBoardId);

        ReadBoardSummaryResDto dto = new ReadBoardSummaryResDto(board.getTitle(), board.getContents().size(),
                board.calculateTotalContentLength());
        return dto;
    }

    private Board getBoard(String encryptedBoardId) {
        Long boardId = aesSecretUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException("존재하지 않는 보드입니다.");
        }
        return optionalBoard.get();
    }

    private User getUser(String token) {
        Long userId = Long.parseLong(jwtUtils.parseUserId(token.substring(7)));
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotAcceptableException("존재하지 않는 유저입니다.");
        }
        return user.get();
    }
}
