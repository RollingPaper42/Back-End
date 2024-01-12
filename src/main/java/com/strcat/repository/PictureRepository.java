package com.strcat.repository;

import com.strcat.exception.NotAcceptableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Repository
@Slf4j
public class PictureRepository {
    private final S3Client s3Client;
    private final String s3bucketName;

    @Autowired
    public PictureRepository(@Value("${s3.bucket.name}") String s3bucketName) {
        Region REGION = Region.AP_NORTHEAST_2;

        this.s3bucketName = s3bucketName;
        s3Client = S3Client.builder()
                .region(REGION)
                .build();
    }

    public String postPicture(String boardId, MultipartFile picture) {
        String key = String.format("pictures/strcat:%s:%d:%s", boardId, System.currentTimeMillis(), picture.getOriginalFilename());

        log.info("bucketName: " + s3bucketName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3bucketName)
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
        return s3Client.utilities().getUrl(builder -> builder.bucket(s3bucketName).key(key)).toString();
    }
}
