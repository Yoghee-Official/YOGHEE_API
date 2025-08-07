package com.lagavulin.yoghee.controller;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.service.AppUserService;
import com.lagavulin.yoghee.service.auth.kakao.model.KakaoToken;
import com.lagavulin.yoghee.service.auth.kakao.model.KakaoUserInfo;
import com.lagavulin.yoghee.service.auth.kakao.KakaoOAuthService;
import com.lagavulin.yoghee.util.JwtUtil;
import com.lagavulin.yoghee.util.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/login/kakao") // TODO 비인증 API로 전환
@ApiResponse(description = "카카오 로그인 컨트롤러")
public class KakaoLoginController {

    @Value("${kakao.client_id}")
    private String kakao_client_id;

    @Value("${kakao.redirect_uri}")
    private String kakao_redirect_uri;

    @Value("${google.redirect_uri}")
    private String google_redirect_uri;

    @Value("${google.client_id}")
    private String google_client_id;

    private final KakaoOAuthService kakaoOAuthService;
    private final AppUserService appUserService;
    private final JwtUtil jwtUtil;

    @Autowired
    public KakaoLoginController(KakaoOAuthService kakaoOAuthService, AppUserService appUserService, JwtUtil jwtUtil) {
        this.kakaoOAuthService = kakaoOAuthService;
        this.appUserService = appUserService;
        this.jwtUtil = jwtUtil;
    }

    /*
     * description: 카카오 로그인 페이지로 이동
     * return: login.html
     * */
    @GetMapping("/page")
    @Operation(summary = "DELETE 예정", description = "카카오 로그인 페이지로 이동 추후 화면 개발시 삭제")
    public String loginPage(Model model) {
        String kakaoUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+ kakao_client_id +"&redirect_uri="+ kakao_redirect_uri;
        model.addAttribute("kakaoLocation", kakaoUrl);

        String googleUrl = "https://accounts.google.com/o/oauth2/auth?client_id="+ google_client_id +"&redirect_uri="+ google_redirect_uri +"&response_type=code&scope=email";
        model.addAttribute("googleLocation", googleUrl);
        return "login";
    }

    @GetMapping("/callback") // TODO APP 에서 KAKAO LOGIN 이후 이 API 호출 필요
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        KakaoToken accessToken = kakaoOAuthService.getAccessToken(code);

        log.info(" [Kakao Controller] Access Token ------> {}", accessToken);

        KakaoUserInfo userInfo = kakaoOAuthService.getUserInfo(accessToken.getAccessToken());

        log.info(" [Kakao Controller] User Info ------> {}", userInfo);

        AppUser loginUser = appUserService.ssoUserLogin(SsoType.KAKAO, accessToken, userInfo);

        String jwt = jwtUtil.generateToken(loginUser.getUserId());

        return ResponseUtils.success(jwt);
    }
}