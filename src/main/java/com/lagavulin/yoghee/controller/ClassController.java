package com.lagavulin.yoghee.controller;

import java.security.Principal;

import com.lagavulin.yoghee.model.enums.ClassSortType;
import com.lagavulin.yoghee.service.ClassService;
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
import org.springframework.web.bind.annotation.PathVariable;
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
            @ApiResponse(responseCode = "200", description = "로그인 비로그인",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                            {
                                "code": 200,
                                "status": "success",
                                "data": {
                                    "categoryId": 3,
                                    "categoryName": "힐링",
                                    "classes": [
                                        {
                                            "classId": "d1594-49853",
                                            "className": "정환이와 아침요가를",
                                            "address": "춘천시 퇴계로 168, 207동 904호",
                                            "thumbnail": "https://yoghee.s3.ap-northeast-2.amazonaws.com/class/d1594-49853/thumbnail.jpg",
                                            "masterId" : "m1594-49853",
                                            "masterName" : "김정환",
                                            "rating": 4.5,
                                            "review": 20,
                                            "price": 15000
                                        },
                                        {
                                            "classId": "d1594-49852",
                                            "className": "김원장과 요가를",
                                            "address": "춘천시 퇴계로 168, 207동 904호",
                                            "thumbnail": "https://yoghee.s3.ap-northeast-2.amazonaws.com/class/d1594-49853/thumbnail.jpg",
                                            "masterId" : "m1594-49852",
                                            "masterName" : "김원장",
                                            "rating": 8.9,
                                            "review": 11,
                                            "price": 15000
                                        }
                                    ]
                                }
                            """
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
        @Parameter(name = "sort", description = "recommend : 추천순 (default), review: 리뷰많은순, recent : 최근 등록순")
        @RequestParam(name = "sort", required = false) String sort){
        return ResponseUtil.success(classService.getCategoryClasses(type, categoryId, ClassSortType.fromCode(sort)));
    }
}
