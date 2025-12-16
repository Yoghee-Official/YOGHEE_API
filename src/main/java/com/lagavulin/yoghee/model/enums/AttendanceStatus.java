package com.lagavulin.yoghee.model.enums;

public enum AttendanceStatus {
    REGISTERED("R"),
    ATTENDED("A"),
    CANCELLED("C"),
    NOT_ATTENDED("N");

    private final String code;

    AttendanceStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}