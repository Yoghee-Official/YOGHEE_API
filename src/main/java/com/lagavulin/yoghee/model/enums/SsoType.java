package com.lagavulin.yoghee.model.enums;

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
        throw new RuntimeException("Invalid SSO code: " + ssoCode); // TODO 예외 처리
    }
}
