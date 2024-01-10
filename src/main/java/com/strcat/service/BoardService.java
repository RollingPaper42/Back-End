package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.User;
import com.strcat.dto.BoardResponse;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.dto.ReadMyInfoResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.util.JwtUtils;
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
    private final SecureDataUtils secureDataUtils;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public List<Board> findByUserId(Long userId) {
        return boardRepository.findByUserId(userId);
    }

    public List<ReadMyInfoResDto> readMyBoardInfo(String token) {
        User user = userService.getUser(token);
        List<Board> boards = findByUserId(user.getId());
        return boards.stream()
                .map(board -> new ReadMyInfoResDto(board.getEncryptedId(),
                        board.getTitle()))
                .collect(Collectors.toList());
    }

    public String createBoard(CreateBoardReqDto dto, String token) {
        Board board;
        User user = userService.getUser(token);
        board = new Board(dto.getTitle(), dto.getTheme(), user);
        board = boardRepository.save(board);

        String encryptedBoardId = secureDataUtils.encrypt(board.getId());
        board.setEncryptedId(encryptedBoardId);
        boardRepository.save(board);
        return encryptedBoardId;
    }

    public ReadBoardResDto readBoard(String encryptedBoardId, String token) {
        Board board = getBoard(encryptedBoardId);
        BoardResponse boardResponse = fetchBoardResponse(board);
        try {
            Long userId = jwtUtils.parseUserId(jwtUtils.removeBearerString(token));
            Boolean isOwner = userId.equals(board.getUser().getId());
            return new ReadBoardResDto(isOwner, boardResponse);
        } catch (NotAcceptableException e) {
            return new ReadBoardResDto(false, boardResponse);
        }
    }

    public ReadBoardSummaryResDto readSummary(String encryptedBoardId) {
        Board board = getBoard(encryptedBoardId);

        return new ReadBoardSummaryResDto(board.getTitle(), board.getTheme(), board.getContents().size(),
                board.calculateTotalContentLength());
    }

    public Board getBoard(String encryptedBoardId) {
        Long boardId = secureDataUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException("존재하지 않는 보드입니다.");
        }
        return optionalBoard.get();
    }

    public BoardResponse fetchBoardResponse(Board board) {
        return new BoardResponse(board.getEncryptedId(),
                board.getTitle(), board.getTheme(), board.getContents());
    }
}
