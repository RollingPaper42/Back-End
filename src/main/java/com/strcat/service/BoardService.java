package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardReqDto;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.dto.ReadBoardSummaryResDto;
import com.strcat.dto.ReadMyInfoResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.UserRepository;
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
    private final UserRepository userRepository;

    public List<Board> findByUserId(Long userId) {
        return boardRepository.findByUserId(userId);
    }

    public List<ReadMyInfoResDto> readMyBoardInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotAcceptableException("유저가 존재하지 않습니다."));
        List<Board> boards = findByUserId(user.getId());
        return boards.stream()
                .map(Board::toReadMyInfoResDto)
                .collect(Collectors.toList());
    }

    public String createBoard(CreateBoardReqDto dto, Long userId) {
        Board board;
        User user = userRepository.findById(userId).orElseThrow(() -> new NotAcceptableException("유저가 존재하지 않습니다."));
        board = new Board(dto.getTitle(), dto.getTheme(), user);
        board = boardRepository.save(board);

        String encryptedBoardId = secureDataUtils.encrypt(board.getId());
        board.setEncryptedId(encryptedBoardId);
        boardRepository.save(board);
        return encryptedBoardId;
    }

    public ReadBoardResDto readBoard(String encryptedBoardId, Long userId) {
        Board board = getBoard(encryptedBoardId);
        try {
            Boolean isOwner = userId.equals(board.getUser().getId());
            return board.toReadBoardResDto(isOwner);
        } catch (NotAcceptableException e) {
            return board.toReadBoardResDto(false);
        }
    }

    public ReadBoardSummaryResDto readSummary(String encryptedBoardId) {
        return getBoard(encryptedBoardId).toReadBoardSummaryDto();
    }

    public Board getBoard(String encryptedBoardId) {
        Long boardId = secureDataUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException("존재하지 않는 보드입니다.");
        }
        return optionalBoard.get();
    }

}
