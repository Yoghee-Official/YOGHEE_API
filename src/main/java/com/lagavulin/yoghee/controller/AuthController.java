package com.lagavulin.yoghee.controller;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.TokenResponse;
import com.lagavulin.yoghee.model.dto.RegistrationDto;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.service.AppUserService;
import com.lagavulin.yoghee.service.auth.jwt.AuthenticationService;
import com.lagavulin.yoghee.service.auth.sso.AbstractOAuthService;
import com.lagavulin.yoghee.service.auth.sso.OAuthServiceFactory;
import com.lagavulin.yoghee.service.auth.sso.SsoUserInfo;
import com.lagavulin.yoghee.util.JwtUtil;
import com.lagavulin.yoghee.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

    private final JwtUtil jwtUtil; // @Deprecated - 점진적 제거 예정
    private final AppUserService appUserService; // @Deprecated - AuthenticationService로 이동 예정
    private final OAuthServiceFactory oAuthServiceFactory; // @Deprecated - AuthenticationService로 이동 예정

    // 새로운 통합 인증 서비스
    private final AuthenticationService authenticationService;
    //    private final EmailVerificationService emailService;
    //    private final SmsVerificationService smsService;
    //    private final KgInicisVerificationService verificationService;

    @GetMapping("/sso/callback")
    @Operation(summary = "SSO 로그인 콜백", description = "SSO 인가코드를 통해 로그인 처리",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 시 JWT 토큰 반환",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "code": 200,
                            "status": "success",
                            "data": {
                                "accessToken" : "ey@@@.@@@@.@@@@",
                                "refreshToken" : "ey@@@.@@@@.@@@@",
                                "accessTokenExpiresIn" : 3600,
                                "refreshTokenExpiresIn" : 1209600
                            }
                        }
                        """, description = "accessToken : 엑세스 토큰 \nrefreshToken : 리프레시 토큰\naccessTokenExpiresIn : 엑세스 토큰 만료시간(초)\nrefreshTokenExpiresIn : 리프레시 토큰 만료시간(초)"
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                        {
                            "code": 400,
                            "status": "fail",
                            "errorCode": "INVALID_REQUEST",
                            "errorMessage": "SSO 인가코드 또는 토큰이 필요합니다."
                        }
                        """
                    )
                )
            )
        })
    public ResponseEntity<?> callback(
        @Parameter(name = "code", description = "[WEB] SSO 인가코드") @RequestParam(name = "code", required = false) String code,
        @Parameter(name = "token", description = "[iOS or Android] SSO 토큰") @RequestParam(name = "token", required = false) String token,
        @Parameter(name = "sso", description = "SSO 타입 / k : 카카오, g : 구글, n : 네이버") @RequestParam("sso") String sso) {

        SsoType ssoType = SsoType.fromSsoCode(sso);

        // OAuth 서비스로 사용자 정보 획득
        AbstractOAuthService service = getOAuthService(ssoType);
        SsoUserInfo userInfo;

        if (code != null) {
            // 웹 인가코드 방식
            var loginToken = service.getAccessToken(code);
            userInfo = service.getUserInfo(loginToken.getAccessToken());
        } else if (token != null) {
            // 모바일 토큰 방식
            userInfo = service.getUserInfo(token);
        } else {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "SSO 인가코드 또는 토큰이 필요합니다.");
        }

        // AuthenticationService를 통한 인증 및 토큰 생성
        TokenResponse tokenResponse = authenticationService.processUserAuthenticationAndCreateTokens(ssoType, userInfo);

        return ResponseUtil.success(tokenResponse);
    }

    //    @PostMapping("/registration")
    //    @Operation(summary = "회원 가입")
    //    @ApiResponse(responseCode = "200", description = "회원 가입 성공"
    //        , content = @Content(mediaType = "application/json",
    //        schema = @Schema(example = """
    //            {
    //                "code": 200,
    //                "status": "success",
    //                "data": "회원 가입이 완료되었습니다."
    //            }
    //            """
    //        )
    //    ))
    //    @ApiResponse(responseCode = "400", description = "회원 가입 실패",
    //        content = @Content(mediaType = "application/json",
    //            schema = @Schema(example = """
    //                {
    //                    "code": 400,
    //                    "status": "fail",
    //                    "errorCode": "INVALID_REQUEST",
    //                    "errorMessage": "잘못된 요청입니다. 이미 가입된 계정 : abc"
    //                }
    //                """
    //            )
    //        )
    //    )
    //    public ResponseEntity<?> register(
    //        @io.swagger.v3.oas.annotations.parameters.RequestBody(
    //            description = "userId, password, name, email, phoneNo 입력",
    //            required = true,
    //            content = @Content(
    //                mediaType = "application/json",
    //                examples = {
    //                    @ExampleObject(
    //                        description = "phoneNo 하이픈(-) 제외 숫자만",
    //                        value = """
    //                            {
    //                                "userId": "yoghee",
    //                                "password": "yogheePassword",
    //                                "name": "요기",
    //                                "email": "yoghee@yoghee.com",
    //                                "phoneNo": "01012345678"
    //                            }
    //                            """)
    //                })
    //        )
    //        @RequestBody RegistrationDto registrationDto) {
    //        appUserService.register(registrationDto);
    //
    //        return ResponseUtil.success("회원 가입이 완료되었습니다.");
    //    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디 비밀번호로 로그인")
    @ApiResponse(responseCode = "200", description = "로그인 성공 시 JWT 토큰 반환",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
                {
                    "code": 200,
                    "status": "success",
                    "data": {
                        "accessToken" : "ey@@@.@@@@.@@@@",
                        "refreshToken" : "ey@@@.@@@@.@@@@",
                        "accessTokenExpiresIn" : 3600,
                        "refreshTokenExpiresIn" : 1209600
                    }
                }
                """,
                description = "accessToken : 엑세스 토큰 \nrefreshToken : 리프레시 토큰\naccessTokenExpiresIn : 엑세스 토큰 만료시간(초)\nrefreshTokenExpiresIn : 리프레시 토큰 만료시간(초)"
            )
        )
    )
    @ApiResponse(responseCode = "401", description = "로그인 실패",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
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
    public ResponseEntity<?> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "userId, password 입력",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        value = """
                            {
                                "userId": "yoghee",
                                "password": "yogheePassword"
                            }
                            """)
                })
        )
        @RequestBody RegistrationDto registrationDto) {

        // 새로운 AuthenticationService 사용
        TokenResponse tokenResponse = authenticationService.authenticateWithCredentials(
            registrationDto.getUserId(),
            registrationDto.getPassword()
        );

        return ResponseUtil.success(tokenResponse);
    }

    //    @GetMapping("/id/check")
    //    @Operation(summary = "ID 중복 여부 검사", description = "회원가입 단계에서 ID 중복 여부 검사")
    //    @ApiResponse(responseCode = "200", description = "사용 가능 ID"
    //        , content = @Content(mediaType = "application/json",
    //        schema = @Schema(example = """
    //            {
    //                "code": 200,
    //                "status": "success",
    //                "data": "사용가능한 ID 입니다."
    //            }
    //            """
    //        )
    //    )
    //    )
    //    @ApiResponse(responseCode = "400", description = "사용 불가 ID",
    //        content = @Content(mediaType = "application/json",
    //            schema = @Schema(example = """
    //                {
    //                    "code": 400,
    //                    "status": "fail",
    //                    "errorCode": "INVALID_REQUEST",
    //                    "errorMessage": "잘못된 요청입니다. 사용중인 ID : abc"
    //                }
    //                """
    //            )
    //        )
    //    )
    //    @ApiResponse(responseCode = "200", description = "사용 가능한 ID")
    //    public ResponseEntity<?> checkId(
    //        @Parameter(name = "id", description = "중복 확인 필요한 ID", example = "yoghee")
    //        @RequestParam(name = "id", required = false) String id) {
    //        appUserService.idDuplicationCheck(id);
    //
    //        return ResponseUtil.success("사용가능한 ID 입니다.");
    //    }
    //
    //    @GetMapping("/jwt")
    //    @Operation(summary = "JWT 토큰 생성", description = "사용자 ID로 JWT 토큰 생성")
    //    @ApiResponse(responseCode = "200", description = "JWT 임시 발급용")
    //    public ResponseEntity<?> generateJwt(
    //        @RequestParam("id") String id) {
    //        String jwt = jwtUtil.generateAccessToken(id);
    //
    //        return ResponseUtil.success(jwt);
    //    }
    //
    //    @PostMapping("/verification")
    //    @Operation(summary = "아이디/비밀번호 찾기 - 인증번호 발송", description = "전화번호 or 이메일로 인증번호 발송")
    //    @ApiResponse(responseCode = "200", description = "인증번호 발송 성공",
    //        content = @Content(mediaType = "application/json",
    //            examples = {
    //                @ExampleObject(
    //                    name = "전화번호 인증",
    //                    value = """
    //                        {
    //                          "code": 200,
    //                          "status": "success",
    //                          "data": "전화번호로 인증번호를 발송했습니다."
    //                        }
    //                        """
    //                ),
    //                @ExampleObject(
    //                    name = "이메일 인증",
    //                    value = """
    //                        {
    //                          "code": 200,
    //                          "status": "success",
    //                          "data": "이메일로 인증번호를 발송했습니다."
    //                        }
    //                        """
    //                )
    //            }
    //        )
    //    )
    //    @ApiResponse(responseCode = "400", description = "잘못된 양식",
    //        content = @Content(mediaType = "application/json",
    //            examples = {
    //                @ExampleObject(
    //                    name = "전화번호 양식 오류",
    //                    value = """
    //                        {
    //                            "code": 400,
    //                            "status": "fail",
    //                            "errorCode": "INVALID_REQUEST",
    //                            "errorMessage": "PHONE 형식이 올바르지 않습니다."
    //                        }
    //                        """
    //                ),
    //                @ExampleObject(
    //                    name = "이메일 양식 오류",
    //                    value = """
    //                        {
    //                            "code": 400,
    //                            "status": "fail",
    //                            "errorCode": "INVALID_REQUEST",
    //                            "errorMessage": "EMAIL 형식이 올바르지 않습니다."
    //                        }
    //                        """
    //                )
    //            }
    //        )
    //    )
    //
    //    @ApiResponse(responseCode = "404", description = "존재하지 않는 정보",
    //        content = @Content(mediaType = "application/json",
    //            examples = {
    //                @ExampleObject(
    //                    name = "전화번호 인증",
    //                    value = """
    //                        {
    //                            "code": 404,
    //                            "status": "fail",
    //                            "errorCode": "USER_NOT_FOUND",
    //                            "errorMessage": "해당 전화번호로 가입된 계정이 없습니다."
    //                        }
    //                        """
    //                ),
    //                @ExampleObject(
    //                    name = "이메일 인증",
    //                    value = """
    //                        {
    //                            "code": 404,
    //                            "status": "fail",
    //                            "errorCode": "USER_NOT_FOUND",
    //                            "errorMessage": "해당 이메일로 가입된 계정이 없습니다."
    //                        }
    //                        """
    //                )
    //            }
    //        )
    //    )
    //    public ResponseEntity<?> sendVerificationCode(
    //        @io.swagger.v3.oas.annotations.parameters.RequestBody(
    //            description = "email 또는 phoneNo 중 선택",
    //            required = true,
    //            content = @Content(
    //                mediaType = "application/json",
    //                examples = {
    //                    @ExampleObject(
    //                        name = "이메일 사용하는 경우",
    //                        value = """
    //                            {
    //                                "type": "EMAIL",
    //                                "email": "yoghee@yoghee.com"
    //                            }
    //                            """),
    //                    @ExampleObject(
    //                        name = "전화번호 사용하는 경우",
    //                        description = "phoneNo 하이픈(-) 제외 숫자만",
    //                        value = """
    //                            {
    //                                "type": "PHONE",
    //                                "phoneNo": "01012345678"
    //                            }
    //                            """)
    //                })
    //        ) @RequestBody VerificationDto verificationDto) {
    //        if (!verificationDto.validate()) {
    //            throw new BusinessException(ErrorCode.INVALID_REQUEST, verificationDto.getType() + " 형식이 올바르지 않습니다.");
    //        }
    //
    //        switch (verificationDto.getType()) {
    //            case EMAIL -> {
    //                appUserService.findUserByEmail(verificationDto.getEmail());
    //                emailService.sendVerificationCode(verificationDto.getEmail());
    //                return ResponseUtil.success("이메일로 인증번호를 발송했습니다.");
    //            }
    //            case PHONE -> {
    //                appUserService.findUserByPhoneNo(verificationDto.getPhoneNo());
    //                //                smsService.sendVerificationCode(verificationDto.getPhoneNo());
    //                return ResponseUtil.success("전화번호로 인증번호를 발송했습니다.");
    //            }
    //            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 타입입니다.");
    //        }
    //    }
    //
    //    @PostMapping("/verification/code")
    //    @Operation(summary = "아이디/비밀번호 찾기 - 인증번호 검증", description = "전화번호 or 이메일로 발송된 인증번호 확인")
    //    @ApiResponse(responseCode = "200", description = "인증번호 확인 성공, 비밀번호 재설정용 JWT 토큰 발급",
    //        content = @Content(mediaType = "application/json",
    //            schema = @Schema(example = """
    //                {
    //                    "code": 200,
    //                    "status": "success",
    //                    "data": "ey@@@.@@@@.@@@@"
    //                }
    //                """
    //            )
    //        )
    //    )
    //    @ApiResponse(responseCode = "400", description = "인증번호 불일치",
    //        content = @Content(mediaType = "application/json",
    //            schema = @Schema(example = """
    //                {
    //                    "code": 400,
    //                    "status": "fail",
    //                    "errorCode": "INVALID_REQUEST",
    //                    "errorMessage": "인증 코드가 일치하지 않습니다."
    //                }
    //                """
    //            )
    //        )
    //    )
    //    public ResponseEntity<?> checkVerificationCode(
    //        @io.swagger.v3.oas.annotations.parameters.RequestBody(
    //            description = "email 또는 phoneNo 중 선택",
    //            required = true,
    //            content = @Content(
    //                mediaType = "application/json",
    //                examples = {
    //                    @ExampleObject(
    //                        name = "이메일 사용하는 경우",
    //                        value = """
    //                            {
    //                                "type": "EMAIL",
    //                                "email": "yoghee@yoghee.com",
    //                                "code": "123456"
    //                            }
    //                            """),
    //                    @ExampleObject(
    //                        name = "전화번호 사용하는 경우",
    //                        description = "phoneNo 하이픈(-) 제외 숫자만",
    //                        value = """
    //                            {
    //                                "type": "PHONE",
    //                                "phoneNo": "01012345678",
    //                                "code": "123456"
    //                            }
    //                            """)
    //                })
    //        ) @RequestBody VerificationDto verificationDto) {
    //        switch (verificationDto.getType()) {
    //            case EMAIL -> {
    //                emailService.verifyCode(verificationDto.getEmail(), verificationDto.getCode());
    //                return ResponseUtil.success(jwtUtil.generateResetPasswordToken(VerificationType.EMAIL, verificationDto.getEmail()));
    //            }
    //            case PHONE -> {
    //                //                smsService.verifyCode(verificationDto.getPhoneNo(), verificationDto.getCode());
    //                return ResponseUtil.success(jwtUtil.generateResetPasswordToken(VerificationType.PHONE, verificationDto.getPhoneNo()));
    //            }
    //            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 타입입니다.");
    //        }
    //    }
    //
    //    @PostMapping("/password/reset")
    //    @Operation(summary = "아이디/비밀번호 찾기 - 인증번호 검증", description = "전화번호 or 이메일로 발송된 인증번호 확인")
    //    @ApiResponse(responseCode = "200", description = "인증번호 확인 성공, 비밀번호 재설정용 JWT 토큰 발급",
    //        content = @Content(mediaType = "application/json",
    //            schema = @Schema(example = """
    //                {
    //                    "code": 200,
    //                    "status": "success",
    //                    "data": "비밀번호가 재설정되었습니다."
    //                }
    //                """
    //            )
    //        )
    //    )
    //    @ApiResponse(responseCode = "400", description = "인증번호 불일치",
    //        content = @Content(mediaType = "application/json",
    //            schema = @Schema(examples = {"""
    //                {
    //                    "code": 400,
    //                    "status": "fail",
    //                    "errorCode": "INVALID_REQUEST",
    //                    "errorMessage":  "비밀번호는 8자 이상 15자 이하이며 영문, 숫자, 특수문자를 모두 포함해야 합니다."
    //                }
    //                """,
    //                """
    //                    {
    //                        "code": 400,
    //                        "status": "fail",
    //                        "errorCode": "INVALID_REQUEST",
    //                        "errorMessage":  "지원하지 않는 타입입니다."
    //                    }
    //                    """,
    //            })
    //        )
    //    )
    //    @ApiResponse(responseCode = "400", description = "인증번호 불일치",
    //        content = @Content(mediaType = "application/json",
    //            schema = @Schema(example = """
    //                {
    //                    "code": 401,
    //                    "status": "fail",
    //                    "errorCode": "UNAUTHORIZED",
    //                    "errorMessage": "유효하지 않은 토큰입니다."
    //                }
    //                """
    //            )
    //        )
    //    )
    //    public ResponseEntity<?> resetPassword(
    //        @io.swagger.v3.oas.annotations.parameters.RequestBody(
    //            description = "/verification/code 에서 발급된 resetPasswordToken, 새 비밀번호 입력",
    //            required = true,
    //            content = @Content(
    //                mediaType = "application/json",
    //                examples = {
    //                    @ExampleObject(
    //                        name = "비밀번호 재설정",
    //                        value = """
    //                            {
    //                                "resetPasswordToken": "ey@@@.@@@@.@@@@",
    //                                "newPassword": "newPassword123"
    //                            }
    //                            """)
    //                })
    //        ) @RequestBody ResetPasswordDto resetPasswordDto) {
    //        appUserService.resetPassword(resetPasswordDto);
    //        return ResponseUtil.success("비밀번호가 재설정되었습니다.");
    //    }

    @PostMapping("/refresh")
    @Operation(summary = "accessToken 재발급", description = "refreshToken을 통해 accessToken 재발급")
    @ApiResponse(responseCode = "200", description = "accessToken 재발급 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = TokenResponse.class)
        )
    )
    @ApiResponse(responseCode = "401", description = "refreshToken 만료, 재로그인 필요",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
                {
                    "code": 401,
                    "status": "fail",
                    "errorCode": "REFRESH_TOKEN_EXPIRED",
                    "errorMessage":  "만료된 리프레쉬 토큰입니다."
                }
                """
            )
        )
    )
    public ResponseEntity<?> refresh(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "로그인에서 발급된 token 모델 그대로 입력",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenResponse.class)
            )
        ) @RequestBody TokenResponse token) {
        try {
            if (token == null || token.getRefreshToken() == null || token.getRefreshToken().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "refreshToken이 필요합니다.");
            }

            // 새로운 AuthenticationService 사용
            TokenResponse newTokens = authenticationService.refreshTokens(token.getRefreshToken());

            return ResponseUtil.success(newTokens);
        } catch (BusinessException ex) {
            // 토큰 관련 에러(INVALID_TOKEN, REFRESH_TOKEN_EXPIRED 등)는 일관된 실패 응답으로 반환
            return ResponseUtil.fail(ex);
        } catch (Exception ex) {
            log.error("Unexpected error in refresh token endpoint", ex);
            return ResponseUtil.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Hidden
    @GetMapping("/token/generate")
    @Operation(summary = "[개발용] userUuid로 토큰 발급", description = "특정 사용자의 UUID로 JWT 토큰 발급 (개발/테스트용)")
    @ApiResponse(responseCode = "200", description = "토큰 발급 성공",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
                {
                    "code": 200,
                    "status": "success",
                    "data": {
                        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
                        "accessTokenExpiresIn": 3600,
                        "refreshTokenExpiresIn": 1209600
                    }
                }
                """
            )
        )
    )
    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
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
    public ResponseEntity<?> generateTokenByUserUuid(
        @Parameter(name = "userUuid", description = "사용자 UUID", example = "1a48d9d4-eeb4-4781-b816-9e8c2b4b0ba7")
        @RequestParam("userUuid") String userUuid) {

        // 사용자 존재 확인
        AppUser user = appUserService.findUserByUuid(userUuid);

        // 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(userUuid);
        String refreshToken = jwtUtil.generateRefreshToken(userUuid);

        TokenResponse tokenResponse = TokenResponse.builder()
                                                   .accessToken(accessToken)
                                                   .refreshToken(refreshToken)
                                                   .build();

        return ResponseUtil.success(tokenResponse);
    }

    //    @PostMapping("/verify")
    //    public ResponseEntity<?> verify(@RequestBody Map<String, String> body) {
    //        String verificationId = body.get("identityVerificationId");
    //
    //        // 1. 포트원 서버에서 실제 인증 데이터 가져오기
    //        Map<String, Object> result = verificationService.getVerificationResult(verificationId);
    //
    //        // 2. 인증 상태 확인 (성공 시 'VERIFIED')
    //        String status = (String) result.get("status");
    //        if (!"VERIFIED".equals(status)) {
    //            return ResponseUtil.fail(ErrorCode.INVALID_REQUEST);
    //        }
    //
    //        // 3. 사용자 정보 추출
    //        Map<String, Object> verifiedInfo = (Map<String, Object>) result.get("verifiedCustomer");
    //        String name = (String) verifiedInfo.get("name");
    //        String birthDate = (String) verifiedInfo.get("birthDate");
    //        String ci = (String) verifiedInfo.get("ci"); // 중복 가입 방지용 고유 식별값
    //
    //        // 4. 비즈니스 로직 (예: DB 저장 또는 세션 저장)
    //        System.out.println("인증 성공: " + name + ", " + birthDate + ", CI: " + ci);
    //
    //        return ResponseEntity.ok(Map.of("message", "인증 성공", "userName", name));
    //    }

    /**
     * OAuth 서비스 팩토리 접근 메서드
     *
     * @deprecated AuthenticationService가 내부적으로 처리하므로 더 이상 필요 없음
     */
    @Deprecated
    private AbstractOAuthService getOAuthService(SsoType ssoType) {
        return oAuthServiceFactory.getOAuthService(ssoType);
    }
}
