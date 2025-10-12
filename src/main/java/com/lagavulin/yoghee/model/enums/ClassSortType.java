package com.lagavulin.yoghee.model.enums;

import lombok.Getter;

@Getter
public enum ClassSortType {
    RECOMMEND("recommend", "추천순"),
    REVIEW("review", "리뷰많은순"),
    RECENT("recent", "최신순");
    private final String code;
    private final String description;

    ClassSortType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ClassSortType fromCode(String code) {
        for (ClassSortType classSortType : ClassSortType.values()) {
            if (classSortType.getCode().equals(code)) {
                return classSortType;
            }
        }
        return RECOMMEND;
    }
}
