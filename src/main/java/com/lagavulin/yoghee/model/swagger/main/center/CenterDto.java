package com.lagavulin.yoghee.model.swagger.main.center;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CenterDto {

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
}
