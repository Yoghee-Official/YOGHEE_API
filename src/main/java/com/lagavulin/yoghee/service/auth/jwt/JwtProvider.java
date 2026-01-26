package com.lagavulin.yoghee.service.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import com.lagavulin.yoghee.config.jwt.JwtProperties;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰의 생성, 파싱, 검증을 담당하는 순수 기술 구현체 비즈니스 로직은 포함하지 않음
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private Key signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes for HS256");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT 토큰 생성
     *
     * @param subject          토큰 주체 (사용자 ID)
     * @param validityInMillis 유효 기간 (밀리초)
     * @param claims           추가 클레임
     * @return JWT 토큰 문자열
     */
    public String createToken(String subject, Long validityInMillis, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMillis);

        JwtBuilder builder = Jwts.builder()
                                 .setSubject(subject)
                                 .setIssuer(jwtProperties.getIssuer())
                                 .setIssuedAt(now)
                                 .setExpiration(expiryDate)
                                 .signWith(signingKey, SignatureAlgorithm.HS256);

        if (claims != null && !claims.isEmpty()) {
            builder.addClaims(claims);
        }

        return builder.compact();
    }

    /**
     * 액세스 토큰 생성
     */
    public String createAccessToken(String userId) {
        return createToken(userId, jwtProperties.getAccessTokenValidity(), null);
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(String userId) {
        return createToken(userId, jwtProperties.getRefreshTokenValidity(), null);
    }

    /**
     * 비밀번호 재설정 토큰 생성
     */
    public String createResetPasswordToken(String target, String type) {
        Map<String, Object> claims = Map.of(
            "target", target,
            "type", type,
            "purpose", "reset_password"
        );
        return createToken("reset_password", jwtProperties.getResetPasswordTokenValidity(), claims);
    }

    /**
     * JWT 토큰 파싱 및 검증
     *
     * @param token JWT 토큰
     * @return Claims 객체
     * @throws BusinessException 토큰이 유효하지 않은 경우
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(signingKey)
                       .build()
                       .parseClaimsJws(token)
                       .getBody();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token expired: {}", e.getMessage());
            // 만료된 토큰의 경우 claims를 반환 (리프레시 로직에서 사용할 수 있음)
            return e.getClaims();
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "지원되지 않는 토큰입니다.");
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "잘못된 형식의 토큰입니다.");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "토큰 서명이 유효하지 않습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "토큰이 비어있습니다.");
        }
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (BusinessException e) {
            return true;
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 토큰 유효성 검증 (만료 및 형식 검증)
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * Key 객체 반환 (하위 호환성을 위해 유지)
     */
    public Key getKey() {
        return signingKey;
    }
}
