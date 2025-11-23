package com.lagavulin.yoghee.config;

import java.io.IOException;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.CustomOAuth2User;
import com.lagavulin.yoghee.service.auth.JwtLoginService;
import com.lagavulin.yoghee.util.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtLoginService jwtLoginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(7);

            CustomOAuth2User loginUser = jwtLoginService.parse(token);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser.getUserId(), null,
                Collections.emptyList());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            log.error("JwtAuthFilter: {}", e.getMessage());
            ResponseEntity<?> error;
            if (e instanceof ExpiredJwtException) {
                error = ResponseUtil.fail(ErrorCode.ACCESS_TOKEN_EXPIRED);
            } else if (e instanceof MalformedJwtException) {
                error = ResponseUtil.fail(ErrorCode.INVALID_TOKEN);
            } else if (e instanceof SignatureException) {
                error = ResponseUtil.fail(ErrorCode.INVALID_TOKEN);
            } else {
                error = ResponseUtil.fail(ErrorCode.UNAUTHORIZED);
            }
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(error.getBody());

            response.getWriter().write(json);
            response.getWriter().flush();
        }
    }
}
