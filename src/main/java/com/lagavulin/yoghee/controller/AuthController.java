package com.lagavulin.yoghee.controller;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.RegistrationDto;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.service.AppUserService;
import com.lagavulin.yoghee.service.auth.AbstractOAuthService;
import com.lagavulin.yoghee.service.auth.SsoToken;
import com.lagavulin.yoghee.service.auth.SsoUserInfo;
import com.lagavulin.yoghee.service.auth.google.GoogleOAuthService;
import com.lagavulin.yoghee.service.auth.kakao.KakaoOAuthService;
import com.lagavulin.yoghee.util.JwtUtil;
import com.lagavulin.yoghee.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final AppUserService appUserService;
    private final GoogleOAuthService googleOAuthService;
    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/sso/callback")
    @Operation(summary = "SSO 로그인 콜백", description = "SSO 인가코드를 통해 로그인 처리",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 시 JWT 토큰 반환",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example= """
                        {
                            "code": 200,
                            "status": "success",
                            "data": "ey@@@.@@@@.@@@@"
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
        })
    public ResponseEntity<?> callback(
        @Parameter(name = "code", description = "[WEB] SSO 인가코드") @RequestParam(name = "code", required = false) String code,
        @Parameter(name = "token", description = "[iOS or Android] SSO 토큰") @RequestParam(name = "token", required = false) String token,
        @Parameter(name= "sso", description = "SSO 타입 / k : 카카오, g : 구글, a : 애플") @RequestParam("sso") String sso) {
        SsoType ssoType = SsoType.fromSsoCode(sso);
        AbstractOAuthService service = getOAuthService(ssoType);
        SsoUserInfo userInfo;
        if(code != null ) {
            SsoToken loginToken = service.getAccessToken(code);
            userInfo = service.getUserInfo(loginToken.getAccessToken());
        }else if(token != null) {
            userInfo = service.getUserInfo(token);
        }else{
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "SSO 인가코드 또는 토큰이 필요합니다.");
        }
        AppUser loginUser = appUserService.ssoUserLogin(ssoType, userInfo);

        String jwt = jwtUtil.generateToken(loginUser.getUserId());
        return ResponseUtil.success(jwt);
    }

    @PostMapping("/registration")
    @Operation(summary = "회원 가입")
    @ApiResponse(responseCode = "200", description = "회원 가입 성공"
    , content = @Content(mediaType = "application/json",
            schema = @Schema(example= """
                {
                    "code": 200,
                    "status": "success",
                    "data": "회원 가입이 완료되었습니다."
                }
                """
            )
        )
    )
    @ApiResponse(responseCode = "400", description = "회원 가입 실패",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example= """
                {
                    "code": 400,
                    "status": "fail",
                    "errorCode": "INVALID_REQUEST",
                    "errorMessage": "잘못된 요청입니다. 이미 가입된 계정 : abc"
                }
                """
            )
        )
    )
    public ResponseEntity<?> register(@RequestBody RegistrationDto registrationDto){
        appUserService.register(registrationDto);

        return ResponseUtil.success("회원 가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인")
    @ApiResponse(responseCode = "200", description = "로그인 성공 시 JWT 토큰 반환",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example= """
                {
                    "code": 200,
                    "status": "success",
                    "data": "ey@@@.@@@@.@@@@"
                }
                """
            )
        )
    )
    @ApiResponse(responseCode = "401", description = "로그인 실패",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example= """
                {
                    "code": 401,
                    "status": "fail",
                    "errorCode": "UNAUTHORIZED",
                    "errorMessage": "인증이 필요합니다."
                }
                """
            )
        )
    )
    @ApiResponse(responseCode = "404", description = "존재하지 않는 계정",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example= """
                {
                    "code": 404,
                    "status": "fail",
                    "errorCode": "USER_NOT_FOUND",
                    "errorMessage": "사용자를 찾을 수 없습니다."
                }
                """
            )
        )
    )
    public ResponseEntity<?> login(@RequestBody RegistrationDto registrationDto) {
        AppUser user = appUserService.login(registrationDto.getEmail(), registrationDto.getPassword());
        String jwt = jwtUtil.generateToken(user.getUserId());
        return ResponseUtil.success(jwt);
    }

    @GetMapping("/jwt")
    @Operation(summary = "JWT 토큰 생성", description = "사용자 ID로 JWT 토큰 생성")
    @ApiResponse(responseCode = "200", description = "JWT 임시 발급용")
    public ResponseEntity<?> generateJwt() {
        String jwt = jwtUtil.generateToken("ddd");
        return ResponseUtil.success(jwt);
    }

    private AbstractOAuthService getOAuthService(SsoType ssoType) {
        return switch (ssoType) {
            case GOOGLE -> googleOAuthService;
            case KAKAO -> kakaoOAuthService;
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST, ssoType.getVendor());
        };
    }
}
