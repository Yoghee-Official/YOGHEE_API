package com.lagavulin.yoghee.controller;

import java.security.Principal;

import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.ImageUpdateDto;
import com.lagavulin.yoghee.model.dto.UpdateLeaderIntroductionDto;
import com.lagavulin.yoghee.service.MyPageService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
@Tag(name = "My Page", description = "마이페이지 관련 API")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping
    @Operation(
        summary = "마이페이지 조회 API",
        description = "마이페이지 정보 조회 API",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "마이페이지 조회 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.lagavulin.yoghee.model.swagger.my.MyPageDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
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
        }
    )
    public ResponseEntity<?> getMyPage(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isEmpty()) {
            return ResponseUtil.fail(ErrorCode.UNAUTHORIZED);
        }
        return ResponseUtil.success(myPageService.getMyPage(principal.getName()));
    }


    @GetMapping("/leader")
    @Operation(
        summary = "지도자 조회 API",
        description = "지도자 조회 API",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "지도자 페이지 조회 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.lagavulin.yoghee.model.dto.LeaderPageDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "지도자로 등록되지 않은 계정",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 400,
                                        "status": "fail",
                                        "errorCode": "BAD_REQUEST",
                                        "errorMessage": "지도자로 등록되지 않은 계정입니다."
                                    }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
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
        }
    )
    public ResponseEntity<?> getLeaderPage(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isEmpty()) {
            return ResponseUtil.fail(ErrorCode.UNAUTHORIZED);
        }
        return ResponseUtil.success(myPageService.getLeaderPage(principal.getName()));
    }

    @PatchMapping("/leader/introduction")
    @Operation(
        summary = "지도자 소개 문구 변경 API",
        description = "지도자 소개 문구 + 경력 년수 변경",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "지도자 소개글 수정 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 200,
                                        "status": "success",
                                        "data" : "지도자 소개가 수정되었습니다."
                                    }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 401,
                                        "status": "fail",
                                        "errorCode": "UNAUTHORIZED",
                                        "errorMessage": "인증이 필요합니다."
                                    }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "조회 실패",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
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
        }
    )
    public ResponseEntity<?> updateLeaderIntroduction(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @RequestBody UpdateLeaderIntroductionDto updateLeaderIntroductionDto) {
        if (principal == null || principal.getName() == null || principal.getName().isEmpty()) {
            return ResponseUtil.fail(ErrorCode.UNAUTHORIZED);
        }
        myPageService.updateLeaderIntroduction(principal.getName(), updateLeaderIntroductionDto);
        return ResponseUtil.success("지도자 소개가 수정되었습니다.");
    }

    @PostMapping("/license")
    @Operation(
        summary = "자격증 인증 등록 API",
        description = "/api/image/presign 에서 발급받은 imageKey를 통해 자격증 인증 등록 요청 (자동으로 Public 처리)",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰")
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "이미지 키 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = ImageUpdateDto.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200", description = "자격증 인증 등록 요청 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 200,
                                    "status": "success",
                                    "data": {
                                        "message": "자격증 인증 등록 요청 완료",
                                        "imageUrl": "https://yoghee-storage.kr.object.ncloudstorage.com/license/images/xxx.jpg"
                                    }
                                }
                            """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> verifyLicense(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @RequestBody ImageUpdateDto request) {

        String imageUrl = myPageService.saveUserLicense(principal.getName(), request.getImageKey());

        log.info("License Image URL: {}", imageUrl);

        return ResponseUtil.success("자격증 인증 등록 요청 완료");
    }

    @PostMapping("/profile")
    @Operation(
        summary = "프로필 이미지 변경 API",
        description = "/api/image/presign 에서 발급받은 imageKey를 통해 프로필 이미지 변경 (자동으로 Public 처리)",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰")
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "이미지 키 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = ImageUpdateDto.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "프로필 이미지 변경 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 200,
                                    "status": "success",
                                    "data": {
                                        "message": "프로필 이미지 변경 완료",
                                        "imageUrl": "https://yoghee-storage.kr.object.ncloudstorage.com/profile/images/xxx.jpg"
                                    }
                                }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
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
        }
    )
    public ResponseEntity<?> updateProfileImage(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @RequestBody ImageUpdateDto request) {

        if (principal == null || principal.getName() == null || principal.getName().isEmpty()) {
            return ResponseUtil.fail(ErrorCode.UNAUTHORIZED);
        }

        String imageUrl = myPageService.updateProfileImage(principal.getName(), request.getImageKey());
        log.info("Profile Image URL: {}", imageUrl);

        return ResponseUtil.success("프로필 이미지 변경 완료");
    }
}