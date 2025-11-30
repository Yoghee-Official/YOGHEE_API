package com.lagavulin.yoghee.model.swagger.main.image;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이미지 Presign 응답 모델")
public class ImagePresignResponseDto {

    @Schema(description = "업로드할 이미지 종류", example = "class/center/profile", required = true)
    private String type;

    @Schema(description = "업로드할 이미지 파일 정보 리스트", required = true)
    private List<ImagePresignInfo> files;

    @Schema(description = "업로드할 이미지 파일 정보")
    @Getter
    @Setter
    public static class ImagePresignInfo {

        @Schema(description = "파일명", example = "image1.jpg")
        private String fileName;

        @Schema(description = "이미지 파일 종류", example = "image/jpeg")
        private String contentType;

        @Schema(description = "파일 너비 해상도 (px)", example = "1200")
        private Integer width;

        @Schema(description = "파일 높이 해상도 (px)", example = "800")
        private Integer height;

        @Schema(description = "파일 크기 (바이트)", example = "204800")
        private Long fileSize;

        @Schema(description = "이미지 키값 (DB 저장시에 사용될 이미지 값)", example = "center/images/9d05bbb0-10d1-42c8-bd17-4e876dbcd035_thumbnail.jpg")
        private String imageKey;

        @Schema(description = "Presigned URL 해당 URL로 이미지 Body에 넣어서 HTTP PUT 요청", example = "https://center.kr.object.ncloudstorage.com/images/...")
        private String presignedUrl;
    }
}
