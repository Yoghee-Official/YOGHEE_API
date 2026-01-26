package com.lagavulin.yoghee.service;

import java.time.Duration;
import java.util.UUID;

import com.lagavulin.yoghee.model.PresignedFileModel;
import com.lagavulin.yoghee.model.dto.PresignedFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    // NCloud Object Storage 버킷명 (application.yml에서 주입 권장)
    private static final String BUCKET_NAME = "yoghee-storage";

    /**
     * 여러 파일에 대한 Presigned URL 발급
     * <p>
     * Type별 디렉토리 구조: - profile/images/  : 사용자 프로필 이미지 - class/images/  :  클래스 이미지 - center/images/  : 센터 이미지
     */
    public PresignedFileDto generatePresignedUrls(PresignedFileDto model) {

        for (PresignedFileModel file : model.getFiles()) {

            String objectKey = buildObjectKey(model.getType(), file);
            file.setImageKey(objectKey);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                                                             .bucket(BUCKET_NAME)
                                                             .key(objectKey)
                                                             .contentType(file.getContentType())
                                                             .build();

            PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(
                p -> p.signatureDuration(Duration.ofMinutes(10))
                      .putObjectRequest(objectRequest)
            );

            file.setPresignedUrl(presigned.url().toString());
        }

        return model;
    }

    /**
     * Object Key 생성 형식: {type}/images/{UUID}_{fileName} 예시: profile/images/550e8400-e29b-41d4-a716-446655440000_profile.jpg
     */
    private String buildObjectKey(String type, PresignedFileModel file) {
        String sanitizedFileName = file.getFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
        return String.format("%s/images/%s_%s", type, UUID.randomUUID(), sanitizedFileName);
    }

    /**
     * 업로드된 파일을 Public Read로 설정 Presigned URL로 업로드 완료 후 호출
     *
     * @param objectKey 파일의 Object Key (예: profile/images/xxx.jpg)
     */
    public void makeFilePublic(String objectKey) {
        PutObjectAclRequest aclRequest = PutObjectAclRequest.builder()
                                                            .bucket(BUCKET_NAME)
                                                            .key(objectKey)
                                                            .acl(ObjectCannedACL.PUBLIC_READ)
                                                            .build();
        s3Client.putObjectAcl(aclRequest);
    }

    /**
     * 파일의 Public URL 생성
     *
     * @param objectKey 파일의 Object Key
     * @return Public URL
     */
    public String getPublicUrl(String objectKey) {
        return String.format("https://%s.kr.object.ncloudstorage.com/%s", BUCKET_NAME, objectKey);
    }
}
