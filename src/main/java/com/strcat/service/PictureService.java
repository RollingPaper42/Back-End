package com.strcat.service;

import com.strcat.repository.PictureRepository;
import com.strcat.util.AesSecretUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PictureService {
    private final PictureRepository pictureRepository;

    public String postPicture(String encryptedBoardId, MultipartFile picture) {
        return pictureRepository.postPicture(encryptedBoardId, picture);
    }
}
