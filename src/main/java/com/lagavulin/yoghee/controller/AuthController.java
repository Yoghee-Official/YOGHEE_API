package com.lagavulin.yoghee.controller;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.TokenResponse;
import com.lagavulin.yoghee.model.dto.RegistrationDto;
import com.lagavulin.yoghee.model.dto.ResetPasswordDto;
import com.lagavulin.yoghee.model.dto.VerificationDto;
import com.lagavulin.yoghee.model.enums.SsoType;
import com.lagavulin.yoghee.model.enums.VerificationType;
import com.lagavulin.yoghee.service.AppUserService;
import com.lagavulin.yoghee.service.auth.AbstractOAuthService;
import com.lagavulin.yoghee.service.auth.RefreshTokenService;
import com.lagavulin.yoghee.service.auth.SsoToken;
import com.lagavulin.yoghee.service.auth.SsoUserInfo;
import com.lagavulin.yoghee.service.auth.google.GoogleOAuthService;
import com.lagavulin.yoghee.service.auth.kakao.KakaoOAuthService;
import com.lagavulin.yoghee.service.verfication.EmailVerificationService;
import com.lagavulin.yoghee.util.JwtUtil;
import com.lagavulin.yoghee.util.ResponseUtil;
import io.jsonwebtoken.Claims;
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

    private final JwtUtil jwtUtil;
    private final AppUserService appUserService;
    private final GoogleOAuthService googleOAuthService;
    private final KakaoOAuthService kakaoOAuthService;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationService emailService;
    //    private final SmsVerificationService smsService;

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
        @Parameter(name = "sso", description = "SSO 타입 / k : 카카오, g : 구글, a : 애플") @RequestParam("sso") String sso) {
        SsoType ssoType = SsoType.fromSsoCode(sso);
        AbstractOAuthService service = getOAuthService(ssoType);
        SsoUserInfo userInfo;
        if (code != null) {
            SsoToken loginToken = service.getAccessToken(code);
            userInfo = service.getUserInfo(loginToken.getAccessToken());
        } else if (token != null) {
            userInfo = service.getUserInfo(token);
        } else {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "SSO 인가코드 또는 토큰이 필요합니다.");
        }
        AppUser loginUser = appUserService.ssoUserLogin(ssoType, userInfo);

        String accessToken = jwtUtil.generateAccessToken(loginUser.getUuid());
        String refreshToken = jwtUtil.generateRefreshToken(loginUser.getUuid());
        refreshTokenService.saveRefreshToken(loginUser.getUuid(), refreshToken);

        return ResponseUtil.success(TokenResponse.builder()
                                                 .accessToken(accessToken)
                                                 .refreshToken(refreshToken)
                                                 .build());
    }

    @PostMapping("/registration")
    @Operation(summary = "회원 가입")
    @ApiResponse(responseCode = "200", description = "회원 가입 성공"
        , content = @Content(mediaType = "application/json",
        schema = @Schema(example = """
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
            schema = @Schema(example = """
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
    public ResponseEntity<?> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "userId, password, name, email, phoneNo 입력",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        description = "phoneNo 하이픈(-) 제외 숫자만",
                        value = """
                            {
                                "userId": "yoghee",
                                "password": "yogheePassword",
                                "name": "요기",
                                "email": "yoghee@yoghee.com",
                                "phoneNo": "01012345678"
                            }
                            """)
                })
        )
        @RequestBody RegistrationDto registrationDto) {
        appUserService.register(registrationDto);

        return ResponseUtil.success("회원 가입이 완료되었습니다.");
    }

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
        AppUser loginUser = appUserService.login(registrationDto.getUserId(), registrationDto.getPassword());

        String accessToken = jwtUtil.generateAccessToken(loginUser.getUuid());
        String refreshToken = jwtUtil.generateRefreshToken(loginUser.getUuid());
        refreshTokenService.saveRefreshToken(loginUser.getUuid(), refreshToken);

        return ResponseUtil.success(TokenResponse.builder()
                                                 .accessToken(accessToken)
                                                 .refreshToken(refreshToken)
                                                 .build());
    }

    @GetMapping("/id/check")
    @Operation(summary = "ID 중복 여부 검사", description = "회원가입 단계에서 ID 중복 여부 검사")
    @ApiResponse(responseCode = "200", description = "사용 가능 ID"
        , content = @Content(mediaType = "application/json",
        schema = @Schema(example = """
            {
                "code": 200,
                "status": "success",
                "data": "사용가능한 ID 입니다."
            }
            """
        )
    )
    )
    @ApiResponse(responseCode = "400", description = "사용 불가 ID",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
                {
                    "code": 400,
                    "status": "fail",
                    "errorCode": "INVALID_REQUEST",
                    "errorMessage": "잘못된 요청입니다. 사용중인 ID : abc"
                }
                """
            )
        )
    )
    @ApiResponse(responseCode = "200", description = "사용 가능한 ID")
    public ResponseEntity<?> checkId(
        @Parameter(name = "id", description = "중복 확인 필요한 ID", example = "yoghee")
        @RequestParam(name = "id", required = false) String id) {
        appUserService.idDuplicationCheck(id);

        return ResponseUtil.success("사용가능한 ID 입니다.");
    }

    @GetMapping("/jwt")
    @Operation(summary = "JWT 토큰 생성", description = "사용자 ID로 JWT 토큰 생성")
    @ApiResponse(responseCode = "200", description = "JWT 임시 발급용")
    public ResponseEntity<?> generateJwt(
        @RequestParam("id") String id) {
        String jwt = jwtUtil.generateAccessToken(id);

        return ResponseUtil.success(jwt);
    }

    @PostMapping("/verification")
    @Operation(summary = "아이디/비밀번호 찾기 - 인증번호 발송", description = "전화번호 or 이메일로 인증번호 발송")
    @ApiResponse(responseCode = "200", description = "인증번호 발송 성공",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "전화번호 인증",
                    value = """
                        {
                          "code": 200,
                          "status": "success",
                          "data": "전화번호로 인증번호를 발송했습니다."
                        }
                        """
                ),
                @ExampleObject(
                    name = "이메일 인증",
                    value = """
                        {
                          "code": 200,
                          "status": "success",
                          "data": "이메일로 인증번호를 발송했습니다."
                        }
                        """
                )
            }
        )
    )
    @ApiResponse(responseCode = "400", description = "잘못된 양식",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "전화번호 양식 오류",
                    value = """
                        {
                            "code": 400,
                            "status": "fail",
                            "errorCode": "INVALID_REQUEST",
                            "errorMessage": "PHONE 형식이 올바르지 않습니다."
                        }
                        """
                ),
                @ExampleObject(
                    name = "이메일 양식 오류",
                    value = """
                        {
                            "code": 400,
                            "status": "fail",
                            "errorCode": "INVALID_REQUEST",
                            "errorMessage": "EMAIL 형식이 올바르지 않습니다."
                        }
                        """
                )
            }
        )
    )

    @ApiResponse(responseCode = "404", description = "존재하지 않는 정보",
        content = @Content(mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "전화번호 인증",
                    value = """
                        {
                            "code": 404,
                            "status": "fail",
                            "errorCode": "USER_NOT_FOUND",
                            "errorMessage": "해당 전화번호로 가입된 계정이 없습니다."
                        }
                        """
                ),
                @ExampleObject(
                    name = "이메일 인증",
                    value = """
                        {
                            "code": 404,
                            "status": "fail",
                            "errorCode": "USER_NOT_FOUND",
                            "errorMessage": "해당 이메일로 가입된 계정이 없습니다."
                        }
                        """
                )
            }
        )
    )
    public ResponseEntity<?> sendVerificationCode(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "email 또는 phoneNo 중 선택",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "이메일 사용하는 경우",
                        value = """
                            {
                                "type": "EMAIL",
                                "email": "yoghee@yoghee.com"
                            }
                            """),
                    @ExampleObject(
                        name = "전화번호 사용하는 경우",
                        description = "phoneNo 하이픈(-) 제외 숫자만",
                        value = """
                            {
                                "type": "PHONE",
                                "phoneNo": "01012345678"
                            }
                            """)
                })
        ) @RequestBody VerificationDto verificationDto) {
        if (!verificationDto.validate()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, verificationDto.getType() + " 형식이 올바르지 않습니다.");
        }

        switch (verificationDto.getType()) {
            case EMAIL -> {
                appUserService.findUserByEmail(verificationDto.getEmail());
                emailService.sendVerificationCode(verificationDto.getEmail());
                return ResponseUtil.success("이메일로 인증번호를 발송했습니다.");
            }
            case PHONE -> {
                appUserService.findUserByPhoneNo(verificationDto.getPhoneNo());
                //                smsService.sendVerificationCode(verificationDto.getPhoneNo());
                return ResponseUtil.success("전화번호로 인증번호를 발송했습니다.");
            }
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 타입입니다.");
        }
    }

    @PostMapping("/verification/code")
    @Operation(summary = "아이디/비밀번호 찾기 - 인증번호 검증", description = "전화번호 or 이메일로 발송된 인증번호 확인")
    @ApiResponse(responseCode = "200", description = "인증번호 확인 성공, 비밀번호 재설정용 JWT 토큰 발급",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
                {
                    "code": 200,
                    "status": "success",
                    "data": "ey@@@.@@@@.@@@@"
                }
                """
            )
        )
    )
    @ApiResponse(responseCode = "400", description = "인증번호 불일치",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
                {
                    "code": 400,
                    "status": "fail",
                    "errorCode": "INVALID_REQUEST",
                    "errorMessage": "인증 코드가 일치하지 않습니다."
                }
                """
            )
        )
    )
    public ResponseEntity<?> checkVerificationCode(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "email 또는 phoneNo 중 선택",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "이메일 사용하는 경우",
                        value = """
                            {
                                "type": "EMAIL",
                                "email": "yoghee@yoghee.com",
                                "code": "123456"
                            }
                            """),
                    @ExampleObject(
                        name = "전화번호 사용하는 경우",
                        description = "phoneNo 하이픈(-) 제외 숫자만",
                        value = """
                            {
                                "type": "PHONE",
                                "phoneNo": "01012345678",
                                "code": "123456"
                            }
                            """)
                })
        ) @RequestBody VerificationDto verificationDto) {
        switch (verificationDto.getType()) {
            case EMAIL -> {
                emailService.verifyCode(verificationDto.getEmail(), verificationDto.getCode());
                return ResponseUtil.success(jwtUtil.generateResetPasswordToken(VerificationType.EMAIL, verificationDto.getEmail()));
            }
            case PHONE -> {
                //                smsService.verifyCode(verificationDto.getPhoneNo(), verificationDto.getCode());
                return ResponseUtil.success(jwtUtil.generateResetPasswordToken(VerificationType.PHONE, verificationDto.getPhoneNo()));
            }
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST, "지원하지 않는 타입입니다.");
        }
    }

    @PostMapping("/password/reset")
    @Operation(summary = "아이디/비밀번호 찾기 - 인증번호 검증", description = "전화번호 or 이메일로 발송된 인증번호 확인")
    @ApiResponse(responseCode = "200", description = "인증번호 확인 성공, 비밀번호 재설정용 JWT 토큰 발급",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
                {
                    "code": 200,
                    "status": "success",
                    "data": "비밀번호가 재설정되었습니다."
                }
                """
            )
        )
    )
    @ApiResponse(responseCode = "400", description = "인증번호 불일치",
        content = @Content(mediaType = "application/json",
            schema = @Schema(examples = {"""
                {
                    "code": 400,
                    "status": "fail",
                    "errorCode": "INVALID_REQUEST",
                    "errorMessage":  "비밀번호는 8자 이상 15자 이하이며 영문, 숫자, 특수문자를 모두 포함해야 합니다."
                }
                """,
                """
                    {
                        "code": 400,
                        "status": "fail",
                        "errorCode": "INVALID_REQUEST",
                        "errorMessage":  "지원하지 않는 타입입니다."
                    }
                    """,
            })
        )
    )
    @ApiResponse(responseCode = "400", description = "인증번호 불일치",
        content = @Content(mediaType = "application/json",
            schema = @Schema(example = """
                {
                    "code": 401,
                    "status": "fail",
                    "errorCode": "UNAUTHORIZED",
                    "errorMessage": "유효하지 않은 토큰입니다."
                }
                """
            )
        )
    )
    public ResponseEntity<?> resetPassword(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "/verification/code 에서 발급된 resetPasswordToken, 새 비밀번호 입력",
            required = true,
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "비밀번호 재설정",
                        value = """
                            {
                                "resetPasswordToken": "ey@@@.@@@@.@@@@",
                                "newPassword": "newPassword123"
                            }
                            """)
                })
        ) @RequestBody ResetPasswordDto resetPasswordDto) {
        appUserService.resetPassword(resetPasswordDto);
        return ResponseUtil.success("비밀번호가 재설정되었습니다.");
    }

    @PostMapping("/refresh")
    @Operation(summary = "accessToken 재발급", description = "refreshToken을 통해 accessToken 재발급")
    @ApiResponse(responseCode = "200", description = "accessToken 재발급 성공",
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
                """
            )
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
                examples = {
                    @ExampleObject(
                        name = "token 모델",
                        value = """
                            {
                                "accessToken": "ey@@@.@@@@.@@@@",
                                "refreshToken": "ey@@@.@@@@.@@@@"
                            }
                            """)
                })
        ) @RequestBody TokenResponse token) {
        Claims claims = refreshTokenService.validateRefreshToken(token.getRefreshToken());
        String userId = claims.getSubject();

        token.setAccessToken(jwtUtil.generateAccessToken(userId));
        return ResponseUtil.success(token);
    }

    private AbstractOAuthService getOAuthService(SsoType ssoType) {
        return switch (ssoType) {
            case GOOGLE -> googleOAuthService;
            case KAKAO -> kakaoOAuthService;
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST, ssoType.getVendor());
        };
    }
}
