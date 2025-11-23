package com.lagavulin.yoghee.service;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import com.lagavulin.yoghee.model.PresignedFileModel;
import com.lagavulin.yoghee.model.dto.PresignedFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final S3Presigner s3Presigner;
    // 여러 파일 Presigned URL 발급
    public PresignedFileDto generatePresignedUrls(PresignedFileDto model) {

        for (PresignedFileModel file : model.getFiles()) {

            String objectKey = buildObjectKey(file);
            file.setImageKey(model.getBucket() + "/" + objectKey);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                                                             .bucket(model.getBucket())
                                                             .key(objectKey)
                                                             .contentType(file.getContentType())
                                                             .build();

            PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(
                p -> p.signatureDuration(Duration.ofMinutes(10))
                      .putObjectRequest(objectRequest)
            );

            file.setPresignedUrl(presigned.url().toString());
        }

        return model; // 요청과 동일한 구조로 반환
    }

    private String buildObjectKey(PresignedFileModel file) {
        return "images/" + UUID.randomUUID() + "_" + file.getFileName();
    }
}
