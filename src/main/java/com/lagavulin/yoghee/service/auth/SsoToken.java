package com.lagavulin.yoghee.service.auth;

import java.util.Date;

public abstract class SsoToken {
    protected Date issuedAt = new Date();
    protected Integer expiresIn; // 초 단위
    protected Integer refreshTokenExpiresIn;

    public abstract String getAccessToken();
    public abstract String getRefreshToken();

    public Date getAccessTokenExpiresAt() {
        if (expiresIn == null) return null;
        return new Date(issuedAt.getTime() + expiresIn * 1000L);
    }

    public Date getRefreshTokenExpiresAt() {
        if (refreshTokenExpiresIn == null) return null;
        return new Date(issuedAt.getTime() + refreshTokenExpiresIn * 1000L);
    }
}