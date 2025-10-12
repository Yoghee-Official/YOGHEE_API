package com.lagavulin.yoghee.model.enums;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;

public enum VerificationType {
    EMAIL,PHONE;
    public static VerificationType fromString(String value) {
        for (VerificationType type : VerificationType.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new BusinessException(ErrorCode.VALIDATION_ERROR, "verification type");
    }
}
