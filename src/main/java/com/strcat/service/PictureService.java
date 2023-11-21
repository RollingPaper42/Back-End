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
    private final AesSecretUtils aesSecretUtils;

    public String postPicture(String encryptedBoardId, String contentId, MultipartFile picture) {
        pictureRepository.postPicture(aesSecretUtils.decrypt(encryptedBoardId), contentId, picture);
    }
}
