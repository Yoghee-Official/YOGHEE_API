package com.lagavulin.yoghee.controller;

import java.security.Principal;

import com.lagavulin.yoghee.model.dto.AttendanceDto;
import com.lagavulin.yoghee.model.dto.CategoryClassDto;
import com.lagavulin.yoghee.model.enums.ClassSortType;
import com.lagavulin.yoghee.service.ClassService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
@Tag(name = "Class", description = "클래스 관련 API")
public class ClassController {

    private final ClassService classService;

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "카테고리 클래스 조회 API", description = "카테고리마다 존재하는 클래스 조회 API",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "카테고리 클래스 조회 성공",
                content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(implementation = CategoryClassDto.class)
                    )
                )
            )
        })
    public ResponseEntity<?> categoryClass(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "categoryId", description = "카테고리 ID")
        @PathVariable(name = "categoryId") String categoryId,
        @Parameter(name = "type", description = "R: Regular 정규수련, O: One day 하루수련")
        @RequestParam(name = "type") String type,
        @Parameter(name = "sort", description = "recommend : 추천순 (default), review: 리뷰많은순, recent : 최신순, favorite : 찜순, expensive : 가격높은순, cheap : 가격낮은순")
        @RequestParam(name = "sort", required = false) String sort) {
        String userUuid = principal != null ? principal.getName() : null;
        // TODO 주소 동까지 나오도록 수정 필요함
        return ResponseUtil.success(classService.getCategoryClasses(type, categoryId, ClassSortType.fromCode(sort), userUuid));
    }

    @PostMapping("/favorite/")
    @Operation(
        summary = "클래스찜 API",
        description = "유저 JWT 토큰을 통해 요가 클래스 찜, 이미 추가된 클래스일 경우 취소",
        parameters = {
            @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰"),
            @Parameter(name = "classId", description = "Class ID", example = "655-ba-353dc-97a12")
        },
        responses =
            {
                @ApiResponse(
                    responseCode = "200", description = "클래스 찜 처리 완료",
                    content = @Content(mediaType = "application/json",
                        schema = @Schema(example =
                            """
                                    {
                                        "code": 200,
                                        "status": "success",
                                        "data": "클래스 찜 처리 완료 655-ba-353dc-97a12"
                                    }
                                """
                        )
                    )
                )
            })
    public ResponseEntity<?> favoriteClass(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "classId", description = "Class ID") @RequestBody String classId) {
        String userUuid = principal.getName();
        classService.addFavoriteClass(userUuid, classId);

        return ResponseUtil.success("클래스 찜 처리 완료 " + classId);
    }

    @GetMapping("/attendance/{scheduleId}")
    @Operation(summary = "클래스 스케줄 참석자 조회 API", description = "특정 스케줄의 참석자 목록을 조회합니다. (지도자 전용)",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "참석자 목록 조회 성공",
                content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(implementation = AttendanceDto.class)
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 403,
                                        "status": "fail",
                                        "errorCode": "FORBIDDEN",
                                        "message": "해당 클래스의 지도자만 참석자 목록을 조회할 수 있습니다."
                                    }
                            """
                    )
                )
            )
        })
    public ResponseEntity<?> getClassScheduleAttendance(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰", required = true) Principal principal,
        @Parameter(name = "scheduleId", description = "스케줄 ID", example = "schedule-001")
        @PathVariable(name = "scheduleId") String scheduleId) {
        return ResponseUtil.success(classService.getClassScheduleAttendance(scheduleId, principal.getName()));
    }

    @PatchMapping("/attendance/{scheduleId}/all")
    @Operation(
        summary = "스케줄 참석자 전체 출석 처리 API",
        description = "특정 스케줄의 모든 REGISTERED 상태 참석자를 ATTENDED로 일괄 변경합니다. (지도자 전용)",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "출석 처리 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 200,
                                        "status": "success",
                                        "data": "5명의 참석자를 출석 처리했습니다."
                                    }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 403,
                                        "status": "fail",
                                        "errorCode": "FORBIDDEN",
                                        "message": "해당 클래스의 지도자만 출석 처리를 할 수 있습니다."
                                    }
                            """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> markAllAttendance(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰", required = true) Principal principal,
        @Parameter(name = "scheduleId", description = "스케줄 ID", example = "schedule-001")
        @PathVariable(name = "scheduleId") String scheduleId) {

        int updatedCount = classService.markAllAttendance(scheduleId, principal.getName());

        return ResponseUtil.success(updatedCount + "명의 참석자를 출석 처리했습니다.");
    }

    @PatchMapping("/attendance/{attendanceId}")
    @Operation(
        summary = "개별 참석자 출석 처리 API",
        description = "특정 참석자를 ATTENDED 상태로 변경합니다. (지도자 전용)",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "출석 처리 성공",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 200,
                                        "status": "success",
                                        "data": "출석 처리가 완료되었습니다."
                                    }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 403,
                                        "status": "fail",
                                        "errorCode": "FORBIDDEN",
                                        "message": "해당 클래스의 지도자만 출석 처리를 할 수 있습니다."
                                    }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "참석 정보를 찾을 수 없음",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                                    {
                                        "code": 404,
                                        "status": "fail",
                                        "errorCode": "RESOURCE_NOT_FOUND",
                                        "message": "참석 정보를 찾을 수 없습니다."
                                    }
                            """
                    )
                )
            )
        }
    )
    public ResponseEntity<?> markAttendance(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰", required = true) Principal principal,
        @Parameter(name = "attendanceId", description = "참석 ID (ATTENDANCE_ID)", example = "attendance-uuid-001")
        @PathVariable(name = "attendanceId") String attendanceId) {

        classService.markAttendance(attendanceId, principal.getName());

        return ResponseUtil.success("출석 처리가 완료되었습니다.");
    }
}
