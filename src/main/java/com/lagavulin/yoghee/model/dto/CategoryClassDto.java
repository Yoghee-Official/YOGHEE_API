package com.lagavulin.yoghee.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryClassDto {

    @Schema(description = "클래스 아이디", example = "d1594-49853")
    private String classId;

    @Schema(description = "클래스명", example = "정환이와 함께하는 요가 클래스")
    private String className;

    @Schema(description = "요가원 등록 이미지 URL list", example = "[\"https://image1_url\", \"https://image2_url\"] ")
    private List<String> images;

    @Schema(description = "요가원 원장 ID", example = "d1594-49853")
    private String masterId;

    @Schema(description = "요가원 원장 이름", example = "오원장")
    private String masterName;

    @Schema(description = "작성된 리뷰수", example = "352")
    private Number rating;

    @Schema(description = "작성된 리뷰수", example = "352")
    private Number review;

    @Schema(description = "요가 클래스 가격", example = "15000")
    private Number price;

    public CategoryClassDto(String classId, String className, String image, String masterId, String masterName, Number rating, Number review,
        Number price) {
        this.classId = classId;
        this.className = className;
        this.images = List.of(image);
        this.masterId = masterId;
        this.masterName = masterName;
        this.rating = rating;
        this.review = review;
        this.price = price;
    }
}
