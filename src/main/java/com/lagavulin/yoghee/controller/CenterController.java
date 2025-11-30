package com.lagavulin.yoghee.controller;

import java.security.Principal;

import com.lagavulin.yoghee.entity.YogaCenterAddress;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.service.CenterService;
import com.lagavulin.yoghee.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/api/center")
@RequiredArgsConstructor
@Tag(name = "Center", description = "요가원 관련 API")
public class CenterController {

    private final CenterService centerService;

    @PostMapping("/favorite/")
    @Operation(
        summary = "요가원찜 API",
        description = "유저 JWT 토큰을 통해 요가원 찜, 이미 추가된 요가원일 경우 취소",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰"),
            @Parameter(name = "centerId", description = "Center ID", example = "655-ba-353dc-97a12")
        },
        responses = {
            @ApiResponse(
                responseCode = "200", description = "요가원 찜 처리 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 200,
                                    "status": "success",
                                    "data": "요가원 찜 처리 완료 655-ba-353dc-97a12"
                                }
                            """
                    )
                )
            )
        })
    public ResponseEntity<?> favoriteClass(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "centerId", description = "Center ID", example = "655ba-353dc-97a12") @RequestBody String centerId) {
        String userUuid = principal.getName();
        centerService.addFavoriteClass(userUuid, centerId);

        return ResponseUtil.success("요가원 찜 처리 완료" + centerId);
    }

    @GetMapping("/address")
    @Operation(summary = "요가원 주소 목록 API", description = "유저가 등록한 요가원 주소 목록 조회, 로그인 헤더 or 키워드로 요가원 주소, 키워드값이 있는경우 키워드로 우선 검색처리",
        responses = {
            @ApiResponse(responseCode = "200", description = "요가원 주소 목록",
                content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(implementation = YogaCenterAddress.class)
                    )
                )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 400,
                                    "status": "fail",
                                    "message": "키워드 또는 사용자 정보가 필요합니다."
                                }
                            """
                    ))
            )
        },
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰"),
            @Parameter(name = "keyword", description = "검색 키워드")
        }
    )
    public ResponseEntity<?> updateCenterAddress(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "keyword", description = "검색 키워드") @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return ResponseUtil.success(centerService.searchByKeyword(keyword));
        }

        if (principal != null) {
            return ResponseUtil.success(centerService.findAddressByUserUuid(principal.getName()));
        }

        throw new BusinessException(ErrorCode.INVALID_REQUEST, "키워드 또는 사용자 정보가 필요합니다.");
    }

    @PostMapping("/address")
    @Operation(
        summary = "요가원 주소 등록 및 수정 API",
        description = "요가원 주소 신규 등록 or 업데이트 / addressId가 존재하면 수정, 없으면 신규 등록",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰"),
            @Parameter(name = "keyword", description = "검색 키워드")
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "주소 처리 완료", content = @Content(mediaType = "application/json",
                schema = @Schema(example =
                    """
                            {
                                "code": 200,
                                "status": "success",
                                "data": "주소 처리 완료"
                            }
                        """
                ))
            )
        }
    )
    public ResponseEntity<?> updateCenterAddress(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "address", description = "요가센터 주소 정보") @RequestBody YogaCenterAddress address) {
        centerService.saveCenterAddress(principal.getName(), address);

        return ResponseUtil.success("주소 처리 완료");
    }
}
