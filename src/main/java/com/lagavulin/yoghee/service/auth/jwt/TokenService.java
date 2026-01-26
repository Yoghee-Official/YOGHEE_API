package com.lagavulin.yoghee.service.auth.jwt;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.lagavulin.yoghee.config.jwt.JwtProperties;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.TokenResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 토큰의 라이프사이클을 관리하는 서비스 - 토큰 생성/갱신/삭제 - 리프레시 토큰 저장/검증 - 토큰 쌍(액세스+리프레시) 관리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    /**
     * 새로운 토큰 쌍 생성 (로그인 시 사용)
     *
     * @param userId 사용자 ID
     * @return TokenResponse (액세스 토큰 + 리프레시 토큰)
     */
    public TokenResponse createTokenPair(String userId) {
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);

        // 리프레시 토큰을 Redis에 저장
        saveRefreshTokenToRedis(userId, refreshToken);

        return TokenResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
    }

    /**
     * 토큰 갱신 (리프레시 토큰을 이용한 액세스 토큰 재발급)
     *
     * @param refreshToken 리프레시 토큰
     * @return TokenResponse (새로운 액세스 토큰 + 새로운 리프레시 토큰)
     */
    public TokenResponse refreshTokenPair(String refreshToken) {
        // 리프레시 토큰 검증
        Claims claims = validateRefreshTokenWithRedis(refreshToken);
        String userId = claims.getSubject();

        // 기존 리프레시 토큰 삭제
        deleteRefreshTokenFromRedis(userId);

        // 새로운 토큰 쌍 생성
        return createTokenPair(userId);
    }

    /**
     * 토큰 무효화 (로그아웃 시 사용)
     *
     * @param userId 사용자 ID
     */
    public void revokeTokens(String userId) {
        deleteRefreshTokenFromRedis(userId);
        // TODO: 액세스 토큰 블랙리스트 추가 (필요한 경우)
    }

    /**
     * 비밀번호 재설정 토큰 생성
     */
    public String createResetPasswordToken(String target, String type) {
        return jwtProvider.createResetPasswordToken(target, type);
    }

    /**
     * 비밀번호 재설정 토큰 검증
     */
    public Claims validateResetPasswordToken(String token) {
        Claims claims = jwtProvider.parseToken(token);

        // 토큰 만료 검증
        if (claims.getExpiration().before(new Date())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "만료된 토큰입니다.");
        }

        // 토큰 용도 검증
        if (!"reset_password".equals(claims.getSubject()) ||
            !"reset_password".equals(claims.get("purpose"))) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "비밀번호 재설정 토큰이 아닙니다.");
        }

        return claims;
    }

    /**
     * 액세스 토큰에서 사용자 ID 추출
     */
    public String getUserIdFromAccessToken(String accessToken) {
        return jwtProvider.getUserIdFromToken(accessToken);
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateAccessToken(String accessToken) {
        return jwtProvider.validateToken(accessToken);
    }

    /**
     * 토큰 파싱 (만료된 토큰도 파싱 가능)
     */
    public Claims parseToken(String token) {
        return jwtProvider.parseToken(token);
    }

    // =============== Private Methods ===============

    /**
     * 리프레시 토큰을 Redis에 저장
     */
    private void saveRefreshTokenToRedis(String userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        long timeoutInMillis = jwtProperties.getRefreshTokenValidity();
        redisTemplate.opsForValue().set(key, refreshToken, timeoutInMillis, TimeUnit.MILLISECONDS);
        log.debug("Refresh token saved for user: {}", userId);
    }

    /**
     * Redis에서 리프레시 토큰 삭제
     */
    private void deleteRefreshTokenFromRedis(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
        log.debug("Refresh token deleted for user: {}", userId);
    }

    /**
     * 리프레시 토큰을 Redis와 함께 검증
     */
    private Claims validateRefreshTokenWithRedis(String refreshToken) {
        // 1. 토큰 파싱 및 기본 검증
        Claims claims = jwtProvider.parseToken(refreshToken);

        // 2. 만료 시간 검증
        if (claims.getExpiration().before(new Date())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 3. Redis에 저장된 토큰과 비교
        String userId = claims.getSubject();
        String key = REFRESH_TOKEN_PREFIX + userId;
        String storedToken = redisTemplate.opsForValue().get(key);

        if (storedToken == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED, "저장된 리프레시 토큰이 없습니다.");
        }

        if (!storedToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED, "리프레시 토큰이 일치하지 않습니다.");
        }

        return claims;
    }
}
