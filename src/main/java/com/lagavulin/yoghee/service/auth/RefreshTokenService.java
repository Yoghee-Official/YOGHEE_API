package com.lagavulin.yoghee.service.auth;

import java.util.concurrent.TimeUnit;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "refresh:";

    public void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(PREFIX + userId, refreshToken, JwtUtil.REFRESH_TOKEN_VALIDITY, TimeUnit.MILLISECONDS);
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }

    public Claims validateRefreshToken(String refreshToken) {
        Claims claims = jwtUtil.parseClaims(refreshToken);
        String userId = claims.getSubject();

        String storedToken = redisTemplate.opsForValue().get(PREFIX + userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        return claims;
    }
}
