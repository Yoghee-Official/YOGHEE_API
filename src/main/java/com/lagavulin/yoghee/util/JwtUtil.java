package com.lagavulin.yoghee.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.enums.VerificationType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final String RESET_PASSWORD_SUBJECT = "ResetPassword";
    public static final Long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간
    public static final Long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30; // 7일
    @Value("${jwt.secret}")
    private String secret;

    @Getter
    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 bytes for HS256");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateResetPasswordToken(VerificationType type, String phoneNoOrEmail) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000L * 60 * 10); // 10분

        return Jwts.builder()
                   .setSubject(RESET_PASSWORD_SUBJECT)
                   .claim("type", type.name())
                   .claim("target", phoneNoOrEmail)
                   .setIssuedAt(now)
                   .setExpiration(expiryDate)
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }


    public Claims getClaimsFromResetPasswordToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        if (!RESET_PASSWORD_SUBJECT.equals(claims.getSubject()) || claims.getExpiration().before(new Date())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
        return claims;
    }


    public String generateAccessToken(String userId) {
        return getAccessToken(userId, ACCESS_TOKEN_VALIDITY); // 1시간
    }

    public String generateRefreshToken(String userId) {
        return getAccessToken(userId, REFRESH_TOKEN_VALIDITY); // 7일
    }

    private String getAccessToken(String userId, Long durationMillis){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + durationMillis); // durationMillis 후 만료

        return Jwts.builder()
                   .setSubject(userId)
                   .setIssuedAt(now)
                   .setExpiration(expiryDate)
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                       .setSigningKey(key)
                       .build()
                       .parseClaimsJws(token)
                       .getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
