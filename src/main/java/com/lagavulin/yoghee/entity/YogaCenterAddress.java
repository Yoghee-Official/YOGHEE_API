package com.lagavulin.yoghee.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "CENTER_ADDRESS")
public class YogaCenterAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ADDRESS_ID")
    @Schema(description = "주소정보 PK(UUID)", example = "adf1234e-ab12-cd34-ef56-abcdef")
    private String addressId;

    @Schema(description = "국가/지역", example = "대한민국")
    private String country;

    @Schema(description = "광역시/도", example = "경기도 or 서울특별시 or 제주특별자치도 or 대구광역시")
    private String state;

    @Schema(description = "시/군/구", example = "성남시 분당구 or 서초구 or 제주시 or 수성구")
    private String city;

    @Column(name = "STREET_ADDRESS")
    @Schema(description = "도로명 주소", example = "다산중앙로 123번길 22-26")
    private String streetAddress;

    @Schema(description = "우편번호", example = "13561")
    private String postal;

    @Schema(description = "수련 장소명", example = "정환요가원")
    private String name;

    @Column(name = "DESC")
    @Schema(description = "상세 위치 설명", example = "좌회전 후 첫번째 건물 3층")
    private String description;

    @Column(name = "USER_UUID")
    private String userUuid;
}