package com.lagavulin.yoghee.model.enums;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;

public enum FavoriteTarget {
    YOGA_CLASS("CLASS"),
    YOGA_CENTER("CENTER");

    private final String type;

    FavoriteTarget(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static FavoriteTarget fromType(String type) {
        for (FavoriteTarget target : FavoriteTarget.values()) {
            if (target.getType().equals(type)) {
                return target;
            }
        }
        throw new BusinessException(ErrorCode.VALIDATION_ERROR, "favoriteTarget");
    }
}
