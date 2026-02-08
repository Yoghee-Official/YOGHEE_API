package com.lagavulin.yoghee.model.swagger.main.classes;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassDto {

    @Schema(description = "클래스 아이디", example = "d1594-49853")
    private String classId;

    @Schema(description = "클래스명", example = "정환이와 함께하는 요가 클래스")
    private String className;

    @Schema(description = "썸네일 이미지명", example = "https://image1_url")
    private String thumbnail;

    @Schema(description = "요가원 원장 ID", example = "d1594-49853")
    private String masterId;

    @Schema(description = "요가원 원장 이름", example = "오원장")
    private String masterName;

    @Schema(description = "평점(1~5점 사이 소수 첫째자리까지 노출)", example = "4.3")
    private Number rating;

    @Schema(description = "작성된 리뷰수", example = "352")
    private Number review;

    @Schema(description = "찜한 수", example = "694")
    private Number favoriteCount;

    @Schema(description = "카테고리", example = "[\"빈야사\", \"하타\"]")
    private List<String> categories;
}
