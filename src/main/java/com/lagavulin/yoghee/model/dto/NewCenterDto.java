package com.lagavulin.yoghee.model.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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
public class NewCenterDto {

    @Schema(description = "클래스 ID (수정시에만 입력)", example = "class-1234abcd")
    private String centerId;

    @Schema(description = "요가원 이름", example = "힐링 요가 센터", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "요가원 설명", example = "도심 속에서 마음과 몸의 힐링을 찾는 요가 센터입니다.")
    private String description;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnail;

    private String masterId;

    @Schema(description = "시/도", example = "서울")
    private String depth1;

    @Column
    @Schema(description = "시/군/구", example = "강남구")
    private String depth2;

    @Column
    @Schema(description = "동/읍/면", example = "역삼동")
    private String depth3;

    @Column(name = "ROAD_ADDRESS")
    @Schema(description = "도로명 주소", example = "서울 강남구 테헤란로 212")
    private String roadAddress;

    @Column(name = "JIBUN_ADDRESS")
    @Schema(description = "지번 주소", example = "서울 강남구 역삼동 718-5")
    private String jibunAddress;

    @Column(name = "ZONECODE")
    @Schema(description = "우편번호", example = "06220")
    private String zonecode;

    @Column(name = "ADDRESS_DETAIL")
    @Schema(description = "상세 주소", example = "멀티캠퍼스 3층")
    private String addressDetail;

    @Column(name = "FULL_ADDRESS")
    @Schema(description = "전체 주소", example = "서울 강남구 테헤란로 212 802호")
    private String fullAddress;

    @Schema(description = "제공물품/편의시설 ID 목록", example = "[\"1\", \"5\"]")
    @Builder.Default
    private List<String> amenityIds = new ArrayList<>();
}
