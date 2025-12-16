package com.lagavulin.yoghee.controller;

import java.security.Principal;

import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.MyPageDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
@Tag(name = "My Page", description = "마이페이지 관련 API")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/")
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
                    schema = @Schema(implementation = MyPageDto.class)
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

    @PostMapping("/license")
    @Operation(
        summary = "자격증 인증 등록 API",
        description = "/api/image/presign 에서 발급받은 URL을 통해 자격증 인증 등록 요청",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰"),
            @Parameter(name = "imageUrl", description = "라이센스 등록 URL")
        },
        responses = {
            @ApiResponse(
                responseCode = "200", description = "자격증 인증 등록 요청 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 200,
                                    "status": "success",
                                    "data": "자격증 인증 등록 요청 완료"
                                }
                            """
                    )
                )
            )
        }
    )

    public ResponseEntity<?> verifyLicense(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "imageUrl", description = "라이센스 등록 URL") String imageUrl) {
        myPageService.saveUserLicense(principal.getName(), imageUrl);
        return ResponseUtil.success("자격증 인증 등록 요청 완료");
    }
}
