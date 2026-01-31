package com.lagavulin.yoghee.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderProfileDto {

    @Schema(description = "유저 닉네임", example = "앨리스")
    private String nickname;

    @Schema(description = "유저 프로필 이미지 URL", example = "https://profile_image_url")
    private String profileImage;

    @Schema(description = "누적된 리뷰 수", example = "123")
    private Long totalReview;

    @Schema(description = "개설된 수련 수", example = "6")
    private Integer totalMyClass;

    @Schema(description = "소개 문구", example = "요가를 사랑하는 앨리스입니다. 함께 수련해요!")
    private String introduction;

    @Schema(description = "대표 자격증", example = "RYT 200")
    private String certificate;

    @Schema(description = "월간 예약이 가장 많은 카테고리", example = "하타")
    private String popularCategory;

    @Schema(description = "월간 예약 수", example = "482")
    private Long reservedCount;

}
