package com.lagavulin.yoghee.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SSO 로그인 테스트용 컨트롤러 local 프로파일에서만 활성화됨
 */
@Slf4j
@Controller
@RequestMapping("/sso")
@Profile("local")
@Hidden
public class SsoLoginController {

    @Value("${kakao.client_id}")
    private String kakaoClientId;

    @Value("${kakao.redirect_uri}")
    private String kakaoRedirectUri;

    @Value("${google.redirect_uri}")
    private String googleRedirectUri;

    @Value("${google.client_id}")
    private String googleClientId;

    @Value("${naver.client_id}")
    private String naverClientId;

    @Value("${naver.redirect_uri}")
    private String naverRedirectUri;

    @Value("${naver.state}")
    private String naverState;

    /**
     * SSO 로그인 페이지로 이동 Kakao, Google, Naver 로그인 링크 제공
     */
    @GetMapping("/page")
    @Operation(summary = "SSO 로그인 페이지", description = "웹 SSO 테스트용 페이지")
    public String loginPage(Model model) {
        // Kakao OAuth URL
        String kakaoUrl = String.format(
            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=%s&redirect_uri=%s",
            kakaoClientId, kakaoRedirectUri
        );
        model.addAttribute("kakaoLocation", kakaoUrl);

        // Google OAuth URL
        String googleUrl = String.format(
            "https://accounts.google.com/o/oauth2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=email profile",
            googleClientId, googleRedirectUri
        );
        model.addAttribute("googleLocation", googleUrl);

        // Naver OAuth URL
        String naverUrl = String.format(
            "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
            naverClientId, naverRedirectUri, naverState
        );
        model.addAttribute("naverLocation", naverUrl);

        return "login";
    }

    @GetMapping("/address")
    public String addressPopupPage() {
        return "address";
    }
}