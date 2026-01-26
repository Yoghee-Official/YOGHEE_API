package com.lagavulin.yoghee.service.auth.jwt;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.CustomOAuth2User;
import com.lagavulin.yoghee.model.TokenResponse;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.service.AppUserService;
import com.lagavulin.yoghee.service.auth.sso.SsoUserInfo;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 통합 인증 서비스 - JWT 기반 인증 및 SSO 후처리를 담당 - 일반 로그인 (ID/Password) - SSO 후처리 (OAuth 결과로 JWT 토큰 생성) - JWT 토큰 기반 인증 및 갱신 - 사용자 세션 관리 - 보안 감사 로깅
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final TokenService tokenService;
    private final AppUserService appUserService;

    // =============== 인증 관련 메서드 ===============


    /**
     * 일반 로그인 (ID/Password 방식)
     *
     * @param userId   사용자 ID
     * @param password 비밀번호
     * @return TokenResponse JWT 토큰 쌍
     */
    public TokenResponse authenticateWithCredentials(String userId, String password) {
        try {
            log.info("Starting credential authentication for user: {}", userId);

            // 1. 사용자 인증
            AppUser user = appUserService.login(userId, password);

            // 2. JWT 토큰 생성
            TokenResponse tokenResponse = tokenService.createTokenPair(user.getUuid());

            logAuthenticationEvent("CREDENTIAL_LOGIN", user.getUuid(), true);
            log.info("Credential authentication successful for user: {}", userId);

            return tokenResponse;

        } catch (BusinessException e) {
            logAuthenticationEvent("CREDENTIAL_LOGIN", userId, false);
            log.warn("Credential authentication failed for user {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            logAuthenticationEvent("CREDENTIAL_LOGIN", userId, false);
            log.error("Unexpected error during credential authentication for user {}: {}",
                userId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                "로그인 중 오류가 발생했습니다.");
        }
    }

    // =============== JWT 토큰 기반 인증 관련 메서드 ===============

    /**
     * 액세스 토큰으로부터 인증된 사용자 정보 생성
     */
    public CustomOAuth2User authenticateWithAccessToken(String accessToken) {
        try {
            // 토큰 유효성 검증
            if (!tokenService.validateAccessToken(accessToken)) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 액세스 토큰입니다.");
            }

            // 사용자 ID 추출
            String userId = tokenService.getUserIdFromAccessToken(accessToken);

            if (userId == null || userId.trim().isEmpty()) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "토큰에서 사용자 정보를 찾을 수 없습니다.");
            }

            log.debug("User authenticated successfully: {}", userId);
            return new CustomOAuth2User(userId);

        } catch (BusinessException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "인증 중 오류가 발생했습니다.");
        }
    }

    /**
     * 토큰 갱신 (리프레시 토큰을 이용한 새로운 토큰 쌍 발급)
     */
    public TokenResponse refreshTokens(String refreshToken) {
        try {
            log.info("Starting token refresh");

            TokenResponse newTokens = tokenService.refreshTokenPair(refreshToken);

            // 사용자 ID 추출 (로깅용)
            String userId = extractUserIdFromToken(newTokens.getAccessToken());
            logAuthenticationEvent("TOKEN_REFRESH", userId, true);

            log.info("Token refresh successful for user: {}", userId);
            return newTokens;

        } catch (BusinessException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                "토큰 갱신 중 오류가 발생했습니다.");
        }
    }

    // =============== 유틸리티 메서드 ===============

    /**
     * 토큰에서 사용자 ID만 추출 (만료된 토큰도 허용)
     */
    public String extractUserIdFromToken(String token) {
        try {
            Claims claims = tokenService.parseToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.warn("Failed to extract user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 사용자의 모든 토큰 무효화 (강제 로그아웃)
     */
    public void logout(String userId) {
        try {
            tokenService.revokeTokens(userId);
            logAuthenticationEvent("LOGOUT", userId, true);
            log.info("All tokens invalidated for user: {}", userId);
        } catch (Exception e) {
            logAuthenticationEvent("LOGOUT", userId, false);
            log.error("Error invalidating tokens for user {}: {}", userId, e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "로그아웃 중 오류가 발생했습니다.");
        }
    }

    /**
     * 토큰 기반 권한 검증
     */
    public boolean hasValidAccess(String accessToken, String requiredRole) {
        // 현재는 단순 토큰 검증만 수행
        // 향후 역할 기반 접근 제어 로직 추가 가능
        return tokenService.validateAccessToken(accessToken);
    }

    // =============== Private 헬퍼 메서드 ===============

    /**
     * SSO 사용자 정보 처리 및 JWT 토큰 생성 AuthController에서 OAuth 과정을 거쳐 획득한 사용자 정보로 JWT 토큰 생성
     */
    public TokenResponse processUserAuthenticationAndCreateTokens(SsoType ssoType, SsoUserInfo userInfo) {
        try {
            log.info("Processing SSO user authentication for provider: {}", ssoType.getVendor());

            // SSO 사용자 정보 검증
            validateSsoUserInfo(userInfo);

            // 사용자 등록/조회
            AppUser user = appUserService.ssoUserLogin(ssoType, userInfo);

            // JWT 토큰 생성
            TokenResponse tokenResponse = tokenService.createTokenPair(user.getUuid());

            // 로그인 이벤트 기록
            logAuthenticationEvent("SSO_LOGIN_" + ssoType.getVendor(), user.getUuid(), true);

            log.info("SSO authentication successful for user: {} (provider: {})",
                user.getUuid(), ssoType.getVendor());

            return tokenResponse;

        } catch (BusinessException e) {
            log.warn("SSO authentication failed for provider {}: {}", ssoType.getVendor(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during SSO authentication for provider {}: {}",
                ssoType.getVendor(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                "SSO 인증 중 오류가 발생했습니다.");
        }
    }

    /**
     * SSO 사용자 정보 유효성 검증
     */
    private void validateSsoUserInfo(com.lagavulin.yoghee.service.auth.sso.SsoUserInfo userInfo) {
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "SSO 사용자 정보가 없습니다.");
        }

        String email = userInfo.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                "SSO 계정에서 이메일 정보를 가져올 수 없습니다.");
        }
    }

    /**
     * 보안 감사를 위한 인증 이벤트 로깅
     */
    private void logAuthenticationEvent(String event, String userId, boolean success) {
        if (success) {
            log.info("Auth Event: {} - User: {} - Success", event, userId);
        } else {
            log.warn("Auth Event: {} - User: {} - Failed", event, userId);
        }
        // TODO: 필요한 경우 외부 감사 시스템에 이벤트 전송
    }
}
