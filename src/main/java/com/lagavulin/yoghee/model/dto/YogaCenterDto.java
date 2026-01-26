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

    // 3단계 주소 구조
    private String addressDepth1; // 시/도 (예: 서울, 경북)
    private String addressDepth2; // 시/군/구 (예: 강남구, 경주시)
    private String addressDepth3; // 동/읍/면 (예: 역삼동, 용강동)

    // 상세 주소 정보
    private String fullAddress; // 전체 주소
    private String roadAddress; // 도로명 주소
    private String jibunAddress; // 지번 주소
    private String zonecode; // 우편번호
    private String addressDetail; // 상세 주소 (건물명, 호수 등)

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

    // 전체 생성자 (JPQL 쿼리용)
    public YogaCenterDto(String centerId, String addressDepth1, String addressDepth2, String addressDepth3,
        String fullAddress, String roadAddress, String jibunAddress, String zonecode,
        String addressDetail, String name, String thumbnail,
        Number favoriteCount, Boolean isFavorite) {
        this.centerId = centerId;
        this.addressDepth1 = addressDepth1;
        this.addressDepth2 = addressDepth2;
        this.addressDepth3 = addressDepth3;
        this.fullAddress = fullAddress;
        this.roadAddress = roadAddress;
        this.jibunAddress = jibunAddress;
        this.zonecode = zonecode;
        this.addressDetail = addressDetail;
        this.address = fullAddress; // 하위 호환성
        this.name = name;
        this.thumbnail = thumbnail;
        this.favoriteCount = favoriteCount;
        this.isFavorite = isFavorite;
    }

    public YogaCenterDto(String centerId, String address, String name, String thumbnail,
        Number favoriteCount, Boolean isFavorite) {
        this.centerId = centerId;
        this.address = address;
        this.name = name;
        this.thumbnail = thumbnail;
        this.favoriteCount = favoriteCount;
        this.isFavorite = isFavorite;
    }

    // fullAddress를 설정할 때 address도 함께 설정 (하위 호환성)
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
        this.address = fullAddress; // 하위 호환성
    }
}
