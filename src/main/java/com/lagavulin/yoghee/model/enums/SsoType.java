package com.lagavulin.yoghee.model.enums;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;

public enum SsoType {
    KAKAO("KAKAO", "k"),
    APPLE("APPLE", "a"),
    GOOGLE("GOOGLE", "g");

    private final String vendor;
    private final String ssoCode;

    SsoType(String vendor, String ssoCode) {
        this.vendor = vendor;
        this.ssoCode = ssoCode;
    }

    public String getVendor() {
        return vendor;
    }

    public String getSsoCode() {
        return ssoCode;
    }

    public static SsoType fromSsoCode(String ssoCode){
        for (SsoType type : SsoType.values()) {
            if (type.getSsoCode().equals(ssoCode)) {
                return type;
            }
        }
        throw new BusinessException(ErrorCode.VALIDATION_ERROR, "ssoCode");
    }
}
