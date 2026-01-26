package com.lagavulin.yoghee.entity;

import java.util.Date;

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

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CENTER")
public class YogaCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "CENTER_ID")
    private String centerId;

    private String name;

    // 주소 정보 - 3단계 구조
    @Column(name = "ADDRESS_DEPTH1")
    private String addressDepth1; // 시/도 (예: 서울, 경북)

    @Column(name = "ADDRESS_DEPTH2")
    private String addressDepth2; // 시/군/구 (예: 강남구, 경주시)

    @Column(name = "ADDRESS_DEPTH3")
    private String addressDepth3; // 동/읍/면 (예: 역삼동, 용강동)

    @Column(name = "FULL_ADDRESS")
    private String fullAddress; // 전체 주소 (기존 address 대체)

    @Column(name = "ROAD_ADDRESS")
    private String roadAddress; // 도로명 주소

    @Column(name = "JIBUN_ADDRESS")
    private String jibunAddress; // 지번 주소

    @Column(name = "ZONECODE")
    private String zonecode; // 우편번호

    @Column(name = "ADDRESS_DETAIL")
    private String addressDetail; // 상세 주소 (건물명, 호수 등)

    @Column(name = "PHONE_NO")
    private String phoneNo;

    @Column(name = "DESC")
    private String description;

    private String thumbnail;

    @Column(name = "MASTER_ID")
    private String masterId;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    private Double latitude;

    private Double longitude;
}