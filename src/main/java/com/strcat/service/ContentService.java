package com.strcat.service;

import com.strcat.domain.Board;
import com.strcat.domain.Content;
import com.strcat.domain.User;
import com.strcat.dto.CreateContentReqDto;
import com.strcat.dto.DeleteContentReqDto;
import com.strcat.dto.ReadBoardResDto;
import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.BoardRepository;
import com.strcat.repository.ContentRepository;
import com.strcat.repository.PictureRepository;
import com.strcat.util.SecureDataUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final BoardRepository boardRepository;
    private final PictureRepository pictureRepository;
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

    public String postPicture(String encryptedBoardId, MultipartFile picture) {
        if (isInvalidContentType(picture.getContentType())) {
            throw new NotAcceptableException("처리할 수 없는 파일 형식입니다.");
        }

        return pictureRepository.postPicture(encryptedBoardId, picture);
    }

    private boolean isInvalidContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg", "image/png", "image/jpg" -> false;
            default -> true;
        };
    }

    public ReadBoardResDto deleteContent(String encryptedBoardId, DeleteContentReqDto dto, User user) {
        Long boardId = secureDataUtils.decrypt(encryptedBoardId);

        contentRepository.deleteAllById(dto.contentIds());
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotAcceptableException("보드를 찾을 수 없습니다"));

        return board.toReadBoardResDto(user.getId().equals(board.getUser().getId()));
    }
}
