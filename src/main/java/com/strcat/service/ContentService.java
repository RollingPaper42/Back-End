package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.Content;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.ContentRepository;
import com.strcat.util.AesSecretUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final BoardRepository boardRepository;
    private final AesSecretUtils aesSecretUtils;

    public Content create(CreateContentReqDto dto, String encryptedBoardId) throws Exception {
        // 복호화
        Long boardId = aesSecretUtils.decrypt(encryptedBoardId);
        Optional<Board> optionalBoard = boardRepository.findById(boardId);

        if (optionalBoard.isEmpty()) {
            throw new NotAcceptableException();
        }

        Board board = optionalBoard.get();

        return contentRepository.save(new Content(dto.getWriter(), dto.getText(), board));
    }
}