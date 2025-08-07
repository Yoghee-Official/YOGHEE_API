package com.lagavulin.yoghee.controller;

import com.lagavulin.yoghee.service.auth.google.model.GoogleUserInfo;
import com.lagavulin.yoghee.service.auth.google.GoogleOAuthService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login/google")
@ApiResponse(description = "구글 로그인 컨트롤러")
public class GoogleLoginController {

    private final GoogleOAuthService googleOAuthService;

    public GoogleLoginController(GoogleOAuthService googleOAuthService) {
        this.googleOAuthService = googleOAuthService;
    }

    @GetMapping("/callback")
    public ResponseEntity<?> googleLogin(@RequestParam("code") String code) {
        System.out.println(code);
        GoogleUserInfo userInfo = googleOAuthService.getUserInfo(code);

        // 여기서 DB 저장 or JWT 토큰 발급 가능
        return ResponseEntity.ok(userInfo);
    }
}
