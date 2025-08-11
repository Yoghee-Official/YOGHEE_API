package com.lagavulin.yoghee.controller;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.service.AppUserService;
import com.lagavulin.yoghee.service.auth.google.GoogleOAuthService;
import com.lagavulin.yoghee.service.auth.google.model.GoogleUserInfo;
import com.lagavulin.yoghee.service.auth.kakao.KakaoOAuthService;
import com.lagavulin.yoghee.service.auth.kakao.model.KakaoToken;
import com.lagavulin.yoghee.service.auth.kakao.model.KakaoUserInfo;
import com.lagavulin.yoghee.util.JwtUtil;
import com.lagavulin.yoghee.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련")
public class AuthController {
    private final KakaoOAuthService kakaoOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final AppUserService appUserService;
    private final JwtUtil jwtUtil;

    @GetMapping("/sso/callback")
    @Operation(summary = "SSO 로그인 콜백", description = "SSO 인가코드를 통해 로그인 처리")
    @ApiResponse(responseCode = "200", description = "로그인 성공 시 JWT 토큰 반환")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    public ResponseEntity<?> callback(
        @Parameter(name = "code", description = "SSO 인가코드") @RequestParam("code") String code,
        @Parameter(name= "sso", description = "SSO 타입 / k : 카카오, g : 구글, a : 애플") @RequestParam("sso") String sso) {
        if(SsoType.KAKAO.getSsoCode().equals(sso)) {
            KakaoToken accessToken = kakaoOAuthService.getAccessToken(code);

            log.info(" [Kakao Controller] Access Token ------> {}", accessToken);

            KakaoUserInfo userInfo = kakaoOAuthService.getUserInfo(accessToken.getAccessToken());

            log.info(" [Kakao Controller] User Info ------> {}", userInfo);

            AppUser loginUser = appUserService.ssoUserLogin(SsoType.KAKAO, accessToken, userInfo);

            String jwt = jwtUtil.generateToken(loginUser.getUserId());
            return ResponseUtils.success(jwt);
        }
        else if(SsoType.GOOGLE.getSsoCode().equals(sso)) {
            GoogleUserInfo userInfo = googleOAuthService.getUserInfo(code);

            return null;
        }
        else{
            return null;
        }
    }
}
