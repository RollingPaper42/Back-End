package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.Content;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.ContentRepository;
import com.strcat.util.SecureDataUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final BoardRepository boardRepository;
    private final SecureDataUtils secureDataUtils;

    public Long create(CreateContentReqDto dto, String encryptedBoardId) {
        Long boardId = secureDataUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException("존재하지 않는 보드입니다.");
        }
        Board board = optionalBoard.get();
        return contentRepository.save(new Content(dto.getWriter(), dto.getText(), dto.getPhotoUrl(), board)).getId();
    }
}
