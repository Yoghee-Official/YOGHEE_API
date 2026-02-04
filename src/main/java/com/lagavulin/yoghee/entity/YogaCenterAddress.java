package com.lagavulin.yoghee.entity;

import java.util.Date;

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

    @Column
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

    private Double latitude;

    private Double longitude;

    @Schema(description = "수련 장소명", example = "정환요가원")
    private String name;

    @Column(name = "DESC")
    @Schema(description = "상세 위치 설명", example = "좌회전 후 첫번째 건물 3층")
    private String description;

    @Column(name = "USER_UUID")
    private String userUuid;

    @Column(name = "CREATED_AT")
    private Date createdAt;
}