package com.lagavulin.yoghee.model.enums;

public enum SsoType {
    KAKAO("KAKAO"),
    APPLE("APPLE"),
    GOOGLE("GOOGLE");

    private final String vendor;

    SsoType(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
    }
}
