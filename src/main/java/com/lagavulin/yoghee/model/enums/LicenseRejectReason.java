package com.lagavulin.yoghee.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 자격증 거절 사유
 */
@Getter
@RequiredArgsConstructor
public enum LicenseRejectReason {

    UNCLEAR_IMAGE("이미지가 불명확합니다"),
    INVALID_CERTIFICATE("유효하지 않은 자격증입니다"),
    EXPIRED("만료된 자격증입니다"),
    FAKE_SUSPECTED("위조 의심"),
    WRONG_TYPE("요가 관련 자격증이 아닙니다"),
    INCOMPLETE_INFO("자격증 정보가 불완전합니다"),
    OTHER("기타 사유");

    private final String description;
}

