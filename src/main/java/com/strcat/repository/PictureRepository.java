package com.strcat.repository;

import com.strcat.exception.NotAcceptableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Repository
@Slf4j
public class PictureRepository {
    private final S3Client s3Client;
    private final String BUCKET_NAME = "elasticbeanstalk-ap-northeast-2-168479654979";

    @Autowired
    public PictureRepository() {
        Region REGION = Region.AP_NORTHEAST_2;

        s3Client = S3Client.builder()
                .region(REGION)
                .build();
    }

    public String postPicture(String boardId, long photoId, MultipartFile picture) {
        String key = String.format("pictures/strcat:%s:%d:%s", boardId, System.currentTimeMillis(), picture.getOriginalFilename());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .contentType("image/png")
                .build();

        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromBytes(picture.getBytes()));

        } catch (Exception exception) {
            throw new NotAcceptableException("사진 저장 실패");
        }

        return generateUrl(key);
    }

    private String generateUrl(String key) {
        return s3Client.utilities().getUrl(builder -> builder.bucket(BUCKET_NAME).key(key)).toString();
    }
}
