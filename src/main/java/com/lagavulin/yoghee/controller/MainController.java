package com.lagavulin.yoghee.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import com.lagavulin.yoghee.model.dto.MainApiDto;
import com.lagavulin.yoghee.model.dto.TodayClassDto;
import com.lagavulin.yoghee.model.dto.YogaClassDto;
import com.lagavulin.yoghee.service.CategoryService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
@Tag(name = "Main", description = "메인화면 진입시 조회 API")
public class MainController {
    private static final List<String> DEFAULT_LAYOUT_ORDER = List.of("todayClass", "recommendClass", "customizedClass", "category", "hotClass", "newReview");
    private final ClassService classService;
    private final CategoryService categoryService;
    @GetMapping("/")
    @Operation(summary = "메인 API", description = "메인화면 정보 조회 API",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그인 비로그인",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(example =
                        """
                            {
                                "code": 200,
                                "status": "success",
                                "data": {
                                    "notificationCount": 3,
                                    "layoutOrder": ["todayClass", "recommendClass", "customizedClass", "category", "hotClass", "newReview"],
                                    "todayClass": [
                                        {
                                            "classId": "d1594-49853",
                                            "className": "정환이와 아침요가를",
                                            "type": "R",
                                            "address": "춘천시 퇴계로 168, 207동 904호",
                                            "scheduleId": "s85fv-4x628",
                                            "startTime": "17:50:00",
                                            "endTime": "20:00:00"
                                        }
                                    ],
                                    "recommendClass": [
                                        {
                                            "classId": "a1234-56789",
                                            "className": "동굴에서 즐기는 이색 요가",
                                            "type": "R",
                                            "address" : "경남 남해시, 남쪽 바다 어딘가",
                                            "description" : "동굴 속에서 요가를 즐기며 자연과 하나되는 경험을 선사합니다.",
                                            "thumbnail" : "https://us.123rf.com/450wm/xalanx/xalanx2201/xalanx220100320/183568204-woman-meditating-in-a-yoga-pose-in-a-cave-lit-by-candles-through-the-opening-the-city-lights-are.jpg?ver=6",
                                            "instructor" : "오정환",
                                            "price" : 50000,
                                            "rating" : 4.8,
                                            "review" : 120,
                                            "capacity" : 10,
                                            "latitude" : 34.8233,
                                            "longitude" : 127.9575
                                        }
                                    ],
                                    "customizedClass": [
                                        {
                                            "classId": "a1234-56789",
                                            "className": "자연에서 즐기는 야외 요가",
                                            "type": "R",
                                            "address" : "대구광역시 낙동가 어딘가",
                                            "description" : "자연 속에서 요가를 즐기며 자연과 하나되는 경험을 선사합니다.",
                                            "thumbnail" : "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Hopetoun_falls.jpg/1200px-Hopetoun_falls.jpg",
                                            "instructor" : "오정환",
                                            "price" : 50000,
                                            "rating" : 4.8,
                                            "review" : 120,
                                            "capacity" : 10,
                                            "latitude" : 34.8233,
                                            "longitude" : 127.9575
                                        }
                                    ],
                                    "category": [
                                        {
                                            "title" : "Hatha Yoga",
                                            "description" : "A traditional form of yoga focusing on physical postures and breath control."
                                        },
                                        {
                                            "title" : "Ashtanga Yoga",
                                            "description" : "A dynamic and physically demanding style of yoga that follows a specific sequence of postures."
                                        }
                                    ],
                                    "hotClass": [
                                        {
                                            "classId": "a1234-56789",
                                            "className": "산속을 달리며 동료들과 재미",
                                            "type": "R",
                                            "address" : "강원 춘천시 삼악산 어디",
                                            "description" : "산속무서웡",
                                            "thumbnail" : "https://i.namu.wiki/i/wdQGP_DcVLHaa6mvhqq8LryxHAjlYv3pPdwAYyHCf-WjbqsGkEUyJo23mQbQkWbikzZrfgj3G7HywgQ3v-0lRyzW-h04BXet_uY-YWGOmm6FwwK-bsNguYIjFhKeoz9PHzyw7R_kWvRYUUZwENequQ.webp",
                                            "masterId" : "a1234-56789",
                                            "price" : 50000,
                                            "rating" : 4.8,
                                            "review" : 120,
                                            "capacity" : 10,
                                            "latitude" : 34.8233,
                                            "longitude" : 127.9575
                                        }
                                    ],
                                    "newReview": [
                                        {
                                            "classId": 101,
                                            "userId": "hwaning1",
                                            "thumbnail": "https://cdn.imweb.me/thumbnail/20240307/e08b8f16b91a3.jpg",
                                            "rating": 10,
                                            "content" : "아드벡 꽤괜"
                                        }
                                    ]
                                }
                            }
                            """
                    )
                )
            )
        })
    public ResponseEntity<?> mainApi(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "type", description = "R: Regular 정규수련, O: One day 하루수련")
        @RequestParam(name = "type") String type) {
        List<TodayClassDto> todaySchedule = new ArrayList<>();
        List<YogaClassDto> customizedClass = new ArrayList<>();
        if (principal != null) {
            // 로그인 사용자
            String userUuid = principal.getName();
            todaySchedule.addAll(classService.getTodaySchedule(userUuid));
            customizedClass.addAll(classService.getUserCategoryNClass(type, userUuid, 15));
        } else {
            // 비로그인 사용자
            customizedClass.addAll(classService.getClickedTopNClassLastMDays(type, 15, 3));
        }

        return ResponseUtil.success(MainApiDto.builder()
                                              .layoutOrder(DEFAULT_LAYOUT_ORDER)
                                              .recommendClass(classService.getRecommendNClass(type, 10))
                                              .todayClass(todaySchedule)
                                              .customizedClass(customizedClass)
                                              .hotClass(classService.getNewSignUpTopNClassSinceStartDate(type, 10))
                                              .newReview(classService.getRecentNClassReviewWithImage(type, 5))
                                              .yogaCategory(categoryService.getRandomNCategoriesWithClass(type, 2))
                                              .build());
    }
}
