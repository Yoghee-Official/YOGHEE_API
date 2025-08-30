package com.lagavulin.yoghee.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.enums.VerificationType;
import io.jsonwebtoken.Claims;
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


    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 30); // 30일

        return Jwts.builder()
                   .setSubject(userId)
                   .setIssuedAt(now)
                   .setExpiration(expiryDate)
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
