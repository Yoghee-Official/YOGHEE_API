package com.lagavulin.yoghee.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YogaCenterDto {

    @Schema(description = "요가원 아이디", example = "655-ba-353dc-97a12")
    private String centerId;

    @Schema(description = "요가원 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "요가원 이름", example = "힐링 요가원")
    private String name;

    @Schema(description = "요가원 썸네일 이미지 URL", example = "https://image1_url")
    private String thumbnail;

    @Schema(description = "찜 수", example = "15000")
    private Number favoriteCount;
    
    @Schema(description = "사용자 찜여부", example = "true or false")
    private Boolean isFavorite;

    public YogaCenterDto(String centerId, String address, String name, String thumbnail,
        Number favoriteCount, Boolean isFavorite) {
        this.centerId = centerId;
        this.address = address;
        this.name = name;
        this.thumbnail = thumbnail;
        this.favoriteCount = favoriteCount;
        this.isFavorite = isFavorite;
    }
}
