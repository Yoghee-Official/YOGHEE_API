package com.lagavulin.yoghee.model.swagger.main.image;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이미지 업로드 정보 DTO")
public class ImageUploadDto {
    @Schema(description = "업로드할 이미지 종류", example = "class")
    private String bucket;

    @Schema(description = "업로드할 이미지 파일 정보 리스트", required = true )
    private List<ImageUploadInfoDto> files;

    @Schema(description = "업로드 이미지 파일 정보")
    @Getter
    @Setter
    public static class ImageUploadInfoDto {
        @Schema(description = "파일명 확장자 포함해서", example = "정환이와함께클래스_메인이미지.jpg")
        private String fileName;

        @Schema(description = "이미지 파일 종류", example = "image/jpeg")
        private String contentType;

        @Schema(description = "파일 너비 해상도 (px)", example = "1200")
        private Integer width;

        @Schema(description = "파일 높이 해상도 (px)", example = "800")
        private Integer height;

        @Schema(description = "파일 크기 (바이트)", example = "204800")
        private Long fileSize;
    }
}
