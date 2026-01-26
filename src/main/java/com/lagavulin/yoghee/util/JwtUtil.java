package com.lagavulin.yoghee.util;

import java.security.Key;

import com.lagavulin.yoghee.config.jwt.JwtProperties;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.enums.VerificationType;
import com.lagavulin.yoghee.service.auth.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT 유틸리티 클래스 (하위 호환성을 위해 유지)
 *
 * @deprecated 새로운 코드에서는 TokenService나 JwtProvider를 직접 사용하세요
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class JwtUtil {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    // 하위 호환성을 위해 public static 상수 유지
    public static final Long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간
    public static final Long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30; // 30일

    /**
     * 비밀번호 재설정 토큰 생성
     *
     * @deprecated TokenService.createResetPasswordToken 사용 권장
     */
    @Deprecated
    public String generateResetPasswordToken(VerificationType type, String phoneNoOrEmail) {
        log.warn("Deprecated method used: generateResetPasswordToken. Use TokenService instead.");
        return jwtProvider.createResetPasswordToken(phoneNoOrEmail, type.name());
    }

    /**
     * 비밀번호 재설정 토큰에서 클레임 추출
     *
     * @deprecated TokenService.validateResetPasswordToken 사용 권장
     */
    @Deprecated
    public Claims getClaimsFromResetPasswordToken(String token) {
        log.warn("Deprecated method used: getClaimsFromResetPasswordToken. Use TokenService instead.");
        Claims claims = jwtProvider.parseToken(token);

        // 기존 로직 유지
        if (!"reset_password".equals(claims.getSubject())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
        return claims;
    }

    /**
     * 액세스 토큰 생성
     *
     * @deprecated TokenService.createTokenPair 사용 권장
     */
    @Deprecated
    public String generateAccessToken(String userId) {
        log.warn("Deprecated method used: generateAccessToken. Use TokenService instead.");
        return jwtProvider.createAccessToken(userId);
    }

    /**
     * 리프레시 토큰 생성
     *
     * @deprecated TokenService.createTokenPair 사용 권장
     */
    @Deprecated
    public String generateRefreshToken(String userId) {
        log.warn("Deprecated method used: generateRefreshToken. Use TokenService instead.");
        return jwtProvider.createRefreshToken(userId);
    }

    /**
     * 토큰 파싱
     *
     * @deprecated TokenService.parseToken 사용 권장
     */
    @Deprecated
    public Claims parseClaims(String token) throws BusinessException {
        log.warn("Deprecated method used: parseClaims. Use TokenService instead.");
        return jwtProvider.parseToken(token);
    }

    /**
     * /** 서명 키 반환 (필터에서 사용)
     *
     * @deprecated JwtProvider.getKey 사용 권장
     */
    @Deprecated
    public Key getKey() {
        return jwtProvider.getKey();
    }
}
