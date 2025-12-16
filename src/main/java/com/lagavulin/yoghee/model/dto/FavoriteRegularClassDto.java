package com.lagavulin.yoghee.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavoriteRegularClassDto {

    @Schema(description = "클래스 아이디", example = "d1594-49853")
    private String classId;

    @Schema(description = "클래스명", example = "정환이와 함께하는 요가 클래스")
    private String className;

    @Schema(description = "클래스 등록 이미지 URL", example = "https://image1_url")
    private String image;

    @Schema(description = "요가원 주소 (시 동까지 노출)", example = "서울시 강남구 역삼동")
    private String address;

    @Schema(description = "찜한 수", example = "694")
    private Number favoriteCount;
}
