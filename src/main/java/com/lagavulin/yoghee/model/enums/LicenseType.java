package com.lagavulin.yoghee.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 자격증 타입
 */
@Getter
@RequiredArgsConstructor
public enum LicenseType {

    YOGA_ALLIANCE_RYT200("요가얼라이언스 RYT-200"),
    YOGA_ALLIANCE_RYT500("요가얼라이언스 RYT-500"),
    KYTA_LEVEL1("한국요가지도자협회 1급"),
    KYTA_LEVEL2("한국요가지도자협회 2급"),
    PILATES_INSTRUCTOR("필라테스 지도자"),
    SPORTS_INSTRUCTOR("생활체육지도사"),
    HEALTH_TRAINER("건강운동관리사"),
    OTHER("기타");

    private final String description;
}

