package com.lagavulin.yoghee.config;

import java.io.IOException;
import java.util.Collections;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.CustomOAuth2User;
import com.lagavulin.yoghee.service.auth.JwtLoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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

            log.info("authHeader : " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(7);

            log.info("Authentication set: " + SecurityContextHolder.getContext().getAuthentication());

            CustomOAuth2User loginUser = jwtLoginService.parse(token);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginUser.getUserId(), null, Collections.emptyList());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((request)));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authentication after set: " + SecurityContextHolder.getContext().getAuthentication());

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
    }
}
