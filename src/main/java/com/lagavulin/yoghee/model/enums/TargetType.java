package com.lagavulin.yoghee.model.enums;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;

public enum TargetType {
    CLASS("CLASS"),
    CENTER("CENTER");

    private final String type;

    TargetType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static TargetType fromType(String type) {
        for (TargetType target : TargetType.values()) {
            if (target.getType().equals(type)) {
                return target;
            }
        }
        throw new BusinessException(ErrorCode.VALIDATION_ERROR, "favoriteTarget");
    }
}
