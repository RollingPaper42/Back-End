package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.BoardGroup;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.dto.ReadMyBoardInfoResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardGroupRepository;
import com.strcat.repository.BoardRepository;
import com.strcat.util.SecureDataUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardGroupRepository boardGroupRepository;
    private final SecureDataUtils secureDataUtils;
    private final UserService userService;

    public List<Board> findByUserId(Long userId) {
        return boardRepository.findByUserId(userId);
    }

    public List<ReadMyBoardInfoResDto> readMyBoardInfo(String token) {
        User user = userService.getUser(token);
        List<Board> boards = findByUserId(user.getId());
        return boards.stream()
                .map(board -> new ReadMyBoardInfoResDto(secureDataUtils.encrypt(
                        board.getId()),
                        board.getTitle(),
                        board.getTheme()))
                .collect(Collectors.toList());
    }

    public String createBoard(CreateBoardReqDto dto, String token) {
        Board board;
        User user = userService.getUser(token);
        if (dto.getGroupId() != null) {
            BoardGroup boardGroup = getBoardGroup(dto.getGroupId());
            board = boardRepository.save(new Board(boardGroup, dto.getTitle(), dto.getTheme(), user));
        } else {
            board = boardRepository.save(new Board(dto.getTitle(), dto.getTheme(), user));
        }
        return secureDataUtils.encrypt(board.getId());
    }

    public ReadBoardResDto readBoard(String encryptedBoardId, String token) {
        User user = userService.getUser(token);
        Board board = getBoard(encryptedBoardId);
        Boolean isOwner = user.getId().equals(board.getUser().getId());
        return new ReadBoardResDto(isOwner, board);
    }

    public ReadBoardSummaryResDto readSummary(String encryptedBoardId, String token) {
        userService.getUser(token);
        Board board = getBoard(encryptedBoardId);

        return new ReadBoardSummaryResDto(board.getTitle(), board.getTheme(), board.getContents().size(),
                board.calculateTotalContentLength());
    }

    private Board getBoard(String encryptedBoardId) {
        Long boardId = secureDataUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException("존재하지 않는 보드입니다.");
        }
        return optionalBoard.get();
    }

    private BoardGroup getBoardGroup(String encryptedBoardGroupId) {
        Long boardGroupId = secureDataUtils.decrypt(encryptedBoardGroupId);
        Optional<BoardGroup> boardGroup = boardGroupRepository.findById(boardGroupId);

        if (boardGroup.isEmpty()) {
            throw new NotAcceptableException("존재하지 않는 보드 그룹입니다.");
        }
        return boardGroup.get();
    }
}
