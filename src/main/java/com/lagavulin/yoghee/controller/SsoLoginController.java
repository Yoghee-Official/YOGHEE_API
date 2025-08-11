package com.lagavulin.yoghee.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/login/")
@Hidden
public class SsoLoginController {

    @Value("${kakao.client_id}")
    private String kakao_client_id;

    @Value("${kakao.redirect_uri}")
    private String kakao_redirect_uri;

    @Value("${google.redirect_uri}")
    private String google_redirect_uri;

    @Value("${google.client_id}")
    private String google_client_id;

    /*
     * description: 카카오 로그인 페이지로 이동
     * return: login.html
     * */
    @GetMapping("/page")
    @Operation(summary = "DELETE 예정", description = "웹 SSO 테스트용 ")
    public String loginPage(Model model) {
        String kakaoUrl =
            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + kakao_client_id + "&redirect_uri=" + kakao_redirect_uri;
        model.addAttribute("kakaoLocation", kakaoUrl);

        String googleUrl = "https://accounts.google.com/o/oauth2/auth?client_id=" + google_client_id + "&redirect_uri=" + google_redirect_uri
            + "&response_type=code&scope=email";
        model.addAttribute("googleLocation", googleUrl);
        return "login";
    }
}