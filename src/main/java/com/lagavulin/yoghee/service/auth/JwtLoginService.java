package com.lagavulin.yoghee.service.auth;


import com.lagavulin.yoghee.model.CustomOAuth2User;
import com.lagavulin.yoghee.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtLoginService {

    private final JwtUtil jwtUtil;

    public CustomOAuth2User parse(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(jwtUtil.getKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

        String userId = claims.getSubject();

        if(userId == null) {
            throw new RuntimeException("Invalid token: missing subject");
        }
        log.info("Parsed userId: " + userId);

        return new CustomOAuth2User(userId);
    }
}