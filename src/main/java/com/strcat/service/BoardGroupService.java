package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.BoardGroup;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardGroupReqDto;
import com.strcat.dto.ReadBoardGroupResDto;
import com.strcat.dto.ReadBoardGroupSummaryResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardGroupRepository;
import com.strcat.repository.BoardRepository;
import com.strcat.util.AesSecretUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardGroupService {
    private final BoardGroupRepository boardGroupRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;
    private final AesSecretUtils aesSecretUtils;

    public String create(CreateBoardGroupReqDto dto, String token) {
        User user = userService.getUser(token);
        BoardGroup boardGroup = boardGroupRepository.save(new BoardGroup(dto.getTitle(), user));
        return aesSecretUtils.encrypt(boardGroup.getId());
    }

    public ReadBoardGroupResDto readBoardGroup(String encryptedBoardGroupId) {
        BoardGroup boardGroup = getBoardGroup(encryptedBoardGroupId);
        return new ReadBoardGroupResDto(boardGroup.getTitle(), boardGroup.getBoards());
    }

    public ReadBoardGroupSummaryResDto readSummary(String encryptedBoardGroupId, String token) {
        userService.getUser(token);
        BoardGroup boardGroup = getBoardGroup(encryptedBoardGroupId);
        List<Board> boards = boardGroup.getBoards();
        return new ReadBoardGroupSummaryResDto(boardGroup.getTitle(),
                boards.stream().mapToLong(board -> board.getContents().size()).sum(), boards.size(),
                boardGroup.calculateTotalContentLength(boards));
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
