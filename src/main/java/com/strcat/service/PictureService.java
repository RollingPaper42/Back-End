package com.strcat.service;

import com.strcat.repository.ContentRepository;
import com.strcat.repository.PictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureService {
    private final PictureRepository pictureRepository;
    private final ContentRepository contentRepository;

    public String postPicture(String encryptedBoardId, MultipartFile picture) {
        long photoId = contentRepository.countExistPhoto();
        return pictureRepository.postPicture(encryptedBoardId, photoId, picture);
    }
}
