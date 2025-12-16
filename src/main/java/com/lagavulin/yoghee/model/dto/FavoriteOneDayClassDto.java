package com.lagavulin.yoghee.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteOneDayClassDto {

    @Schema(description = "클래스 아이디", example = "d1594-49853")
    private String classId;

    @Schema(description = "클래스명", example = "정환이와 함께하는 요가 클래스")
    private String className;

    @Schema(description = "클래스 등록 이미지 URL", example = "https://image1_url")
    private String image;

    @Schema(description = "요가원 원장 ID", example = "d1594-49853")
    private String masterId;

    @Schema(description = "요가원 원장 이름", example = "오원장")
    private String masterName;

    @Schema(description = "작성된 리뷰수", example = "352")
    private Number review;

    @Schema(description = "작성된 리뷰 평점 (1~5, 소수 첫째자리까지 노출)", example = "4.7")
    private Number rating;
}
