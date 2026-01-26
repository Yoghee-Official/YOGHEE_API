package com.lagavulin.yoghee.config.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 관련 설정을 중앙화하는 Configuration 클래스
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtProperties {

    /**
     * JWT 서명을 위한 비밀키
     */
    private String secret;

    /**
     * 액세스 토큰 유효 기간 (밀리초) 기본값: 1시간 (3,600,000ms)
     */
    private Long accessTokenValidity = 1000L * 60 * 60;

    /**
     * 리프레시 토큰 유효 기간 (밀리초) 기본값: 30일 (2,592,000,000ms)
     */
    private Long refreshTokenValidity = 1000L * 60 * 60 * 24 * 30;

    /**
     * 비밀번호 재설정 토큰 유효 기간 (밀리초) 기본값: 10분 (600,000ms)
     */
    private Long resetPasswordTokenValidity = 1000L * 60 * 10;

    /**
     * JWT 발급자 정보
     */
    private String issuer = "yoghee-api";

    /**
     * 토큰 타입
     */
    private String tokenType = "Bearer";

    // Setter methods for @ConfigurationProperties
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAccessTokenValidity(Long accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public void setRefreshTokenValidity(Long refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public void setResetPasswordTokenValidity(Long resetPasswordTokenValidity) {
        this.resetPasswordTokenValidity = resetPasswordTokenValidity;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
