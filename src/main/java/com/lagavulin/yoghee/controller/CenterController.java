package com.lagavulin.yoghee.controller;

import java.security.Principal;

import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.NewCenterDto;
import com.lagavulin.yoghee.model.dto.YogaCenterDto;
import com.lagavulin.yoghee.model.swagger.main.center.CenterBaseDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
        description = "인증된 사용자의 JWT 토큰을 통해 요가원 찜하기 또는 찜 해제를 처리합니다. 이미 추가된 요가원일 경우 찜 해제됩니다.",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰 (필수)", required = true),
            @Parameter(name = "centerId", description = "Center ID", example = "655-ba-353dc-97a12", required = true)
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
            ),
            @ApiResponse(
                responseCode = "401", description = "인증 실패 - 토큰이 없거나 유효하지 않음",
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
        })
    public ResponseEntity<?> favoriteClass(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "centerId", description = "Center ID", example = "655ba-353dc-97a12") @RequestBody String centerId) {

        if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증이 필요합니다.");
        }

        String userUuid = principal.getName();
        centerService.addFavoriteClass(userUuid, centerId);

        return ResponseUtil.success("요가원 찜 처리 완료" + centerId);
    }

    @GetMapping
    @Operation(summary = "요가원 정보 목록 조회 API",
        description = "인증된 사용자가 등록한 요가원 목록을 조회합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "요가원 정보 목록 조회 성공",
                content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(implementation = CenterBaseDto.class)
                    )
                )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰이 없거나 유효하지 않음",
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
                    ))
            )
        },
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰 (필수)", required = true)
        }
    )
    public ResponseEntity<?> getCenter(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰", required = true) Principal principal) {

        if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증이 필요합니다.");
        }

        return ResponseUtil.success(centerService.findCenterByUserUuid(principal.getName()));
    }

    @GetMapping("/{centerId}")
    @Operation(summary = "특정 요가원 정보 조회 API",
        description = "centerId를 통해 특정 요가원의 상세 정보를 조회합니다. 인증된 사용자의 찜 여부도 함께 반환됩니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "요가원 정보 조회 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = YogaCenterDto.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "요가원을 찾을 수 없음",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 404,
                                    "status": "fail",
                                    "errorCode": "RESOURCE_NOT_FOUND",
                                    "errorMessage": "해당 요가원을 찾을 수 없습니다."
                                }
                            """
                    )
                )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰이 없거나 유효하지 않음 (선택적)",
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
                    ))
            )
        },
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰 (선택)", required = false),
            @Parameter(name = "centerId", description = "요가원 ID", example = "655-ba-353dc-97a12", required = true)
        }
    )
    public ResponseEntity<?> getCenterById(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "centerId", description = "요가원 ID", example = "655-ba-353dc-97a12") @PathVariable String centerId) {

        String userUuid = (principal != null && principal.getName() != null) ? principal.getName() : null;

        YogaCenterDto center = centerService.findCenterById(centerId, userUuid);

        if (center == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "해당 요가원을 찾을 수 없습니다.");
        }

        return ResponseUtil.success(center);
    }

    @PostMapping
    @Operation(summary = "요가원 정보 등록 API",
        description = "요가원 정보를 신규 등록합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "요가원 등록 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            {
                                "code": 200,
                                "status": "success",
                                "data": "요가원이 성공적으로 등록되었습니다."
                            }
                        """)
                )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            {
                                "code": 401,
                                "status": "fail",
                                "errorCode": "UNAUTHORIZED",
                                "errorMessage": "인증이 필요합니다."
                            }
                        """)
                )
            )
        },
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰 (필수)", required = true)
        }
    )
    public ResponseEntity<?> createCenter(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰", required = true) Principal principal,
        @Parameter(name = "NewCenterDto", description = "등록할 요가원 정보", required = true) @RequestBody NewCenterDto newCenterDto) {

        if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증이 필요합니다.");
        }

        centerService.createCenter(principal.getName(), newCenterDto);
        return ResponseUtil.success("요가원이 성공적으로 등록되었습니다.");
    }

    @PutMapping("/{centerId}")
    @Operation(summary = "요가원 수정 API",
        description = "본인이 등록한 요가원 정보를 수정합니다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "요가원 수정 완료",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            {
                                "code": 200,
                                "status": "success",
                                "data": "요가원 정보가 성공적으로 수정되었습니다."
                            }
                        """)
                )
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 본인이 등록한 요가원이 아님",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            {
                                "code": 403,
                                "status": "fail",
                                "errorCode": "FORBIDDEN",
                                "errorMessage": "본인이 등록한 요가원만 수정할 수 있습니다."
                            }
                        """)
                )
            ),
            @ApiResponse(responseCode = "404", description = "요가원을 찾을 수 없음",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            {
                                "code": 404,
                                "status": "fail",
                                "errorCode": "CENTER_NOT_FOUND",
                                "errorMessage": "해당 요가원을 찾을 수 없습니다."
                            }
                        """)
                )
            )
        },
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰 (필수)", required = true),
            @Parameter(name = "centerId", description = "수정할 요가원 ID", example = "center-1234abcd", required = true)
        }
    )
    public ResponseEntity<?> updateCenter(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰", required = true) Principal principal,
        @Parameter(name = "NewCenterDto", description = "수정할 요가원 정보", required = true) @RequestBody NewCenterDto newCenterDto) {

        if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증이 필요합니다.");
        }

        centerService.updateCenter(principal.getName(), newCenterDto);
        return ResponseUtil.success("요가원 정보가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{centerId}")
    @Operation(summary = "요가원 삭제 API",
        description = "본인이 등록한 요가원을 삭제합니다. MASTER_ID가 일치하지 않으면 삭제 불가.",
        responses = {
            @ApiResponse(responseCode = "200", description = "요가원 삭제 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 200,
                                    "status": "success",
                                    "data": "요가원이 성공적으로 삭제되었습니다."
                                }
                            """
                    )
                )
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 본인이 등록한 요가원이 아님",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 403,
                                    "status": "fail",
                                    "errorCode": "FORBIDDEN",
                                    "errorMessage": "본인이 등록한 요가원만 삭제할 수 있습니다."
                                }
                            """
                    )
                )
            ),
            @ApiResponse(responseCode = "404", description = "요가원을 찾을 수 없음",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                {
                                    "code": 404,
                                    "status": "fail",
                                    "errorCode": "RESOURCE_NOT_FOUND",
                                    "errorMessage": "해당 요가원을 찾을 수 없습니다."
                                }
                            """
                    )
                )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
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
        },
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰 (필수)", required = true),
            @Parameter(name = "centerId", description = "삭제할 요가원 ID", example = "center-1234abcd", required = true)
        }
    )
    public ResponseEntity<?> deleteCenter(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰", required = true) Principal principal,
        @Parameter(name = "centerId", description = "삭제할 요가원 ID") @PathVariable String centerId) {

        if (principal == null || principal.getName() == null || principal.getName().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "인증이 필요합니다.");
        }

        centerService.deleteCenter(centerId, principal.getName());
        return ResponseUtil.success("요가원이 성공적으로 삭제되었습니다.");
    }
}
