package com.strcat.service;

import com.strcat.exception.NotAcceptableException;
import com.strcat.repository.PictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureService {
    private final PictureRepository pictureRepository;

    public boolean isInvalidContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg", "image/png" -> false;
            default -> true;
        };
    }

    public String postPicture(String encryptedBoardId, MultipartFile picture) {
        if (isInvalidContentType(picture.getContentType())) {
            throw new NotAcceptableException("처리할 수 없는 파일 형식입니다.");
        }

        return pictureRepository.postPicture(encryptedBoardId, picture);
    }
}
