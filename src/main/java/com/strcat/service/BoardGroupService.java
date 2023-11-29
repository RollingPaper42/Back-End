package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.BoardGroup;
import com.strcat.domain.User;
import com.strcat.dto.CreateBoardGroupReqDto;
import com.strcat.dto.ReadBoardGroupResDto;
import com.strcat.dto.ReadBoardGroupSummaryResDto;
import com.strcat.dto.ReadMyInfoResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardGroupRepository;
import com.strcat.util.JwtUtils;
import com.strcat.util.SecureDataUtils;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardGroupService {
    private final BoardGroupRepository boardGroupRepository;
    private final BoardService boardService;
    private final UserService userService;
    private final SecureDataUtils secureDataUtils;
    private final JwtUtils jwtUtils;

    public String create(CreateBoardGroupReqDto dto, String token) {
        User user = userService.getUser(token);
        BoardGroup boardGroup = boardGroupRepository.save(new BoardGroup(dto.getTitle(), user));

        String encryptedId = secureDataUtils.encrypt(boardGroup.getId());
        boardGroup.setEncryptedId(encryptedId);
        boardGroupRepository.save(boardGroup);
        return encryptedId;
    }

    public ReadBoardGroupResDto readBoardGroup(String encryptedBoardGroupId, String token) {
        BoardGroup boardGroup = getBoardGroup(encryptedBoardGroupId);
        try {
            Long userId = jwtUtils.parseUserId(jwtUtils.removeBearerString(token));
            Boolean isOwner = userId.equals(boardGroup.getUser().getId());
            return new ReadBoardGroupResDto(boardGroup.getTitle(), boardGroup.getEncryptedId(), isOwner,
                    boardService.fetchBoardResponses(boardGroup.getBoards()));
        } catch (NotAcceptableException e) {
            return new ReadBoardGroupResDto(boardGroup.getTitle(), boardGroup.getEncryptedId(), false,
                    boardService.fetchBoardResponses(boardGroup.getBoards()));
        }
    }

    public ReadBoardGroupSummaryResDto readSummary(String encryptedBoardGroupId, String token) {
        userService.getUser(token);
        BoardGroup boardGroup = getBoardGroup(encryptedBoardGroupId);
        List<Board> boards = boardGroup.getBoards();
        return new ReadBoardGroupSummaryResDto(boardGroup.getTitle(),
                boards.stream().mapToLong(board -> board.getContents().size()).sum(), boards.size(),
                boardGroup.calculateTotalContentLength(boards));
    }

    public List<ReadMyInfoResDto> readMyBoardGroupInfo(String token) {
        User user = userService.getUser(token);
        List<BoardGroup> boardGroups = boardGroupRepository.findByUserId(user.getId());

        // 테스트를 위해 우선 바로 암호화 해서 보내주는 방식으로 구현함
        return boardGroups.stream()
                .map(boardGroup -> new ReadMyInfoResDto(secureDataUtils.encrypt(boardGroup.getId()),
                        boardGroup.getTitle()))
                .collect(Collectors.toList());
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
