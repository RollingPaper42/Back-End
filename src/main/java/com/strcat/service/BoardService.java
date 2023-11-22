package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.BoardGroup;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardGroupRepository;
import com.strcat.repository.BoardRepository;
import com.strcat.util.AesSecretUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardGroupRepository boardGroupRepository;
    private final AesSecretUtils aesSecretUtils;
    private final UserService userService;

    public String createBoard(CreateBoardReqDto dto, String token) {
        Board board;
        User user = userService.getUser(token);
        if (dto.getGroupId() != null) {
            BoardGroup boardGroup = getBoardGroup(dto.getGroupId());
            board = boardRepository.save(new Board(boardGroup, dto.getTitle(), dto.getTheme(), user));
        } else {
            board = boardRepository.save(new Board(dto.getTitle(), dto.getTheme(), user));
        }
        return aesSecretUtils.encrypt(board.getId());
    }

    public Board readBoard(String encryptedBoardId) {
        return getBoard(encryptedBoardId);
    }

    public ReadBoardSummaryResDto readSummary(String encryptedBoardId, String token) {
        userService.getUser(token);
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

    private BoardGroup getBoardGroup(String encryptedBoardGroupId) {
        Long boardGroupId = aesSecretUtils.decrypt(encryptedBoardGroupId);
        Optional<BoardGroup> boardGroup = boardGroupRepository.findById(boardGroupId);

        if (boardGroup.isEmpty()) {
            throw new NotAcceptableException("존재하지 않는 보드 그룹입니다.");
        }
        return boardGroup.get();
    }
}
