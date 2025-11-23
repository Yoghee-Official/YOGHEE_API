package com.lagavulin.yoghee.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import com.lagavulin.yoghee.entity.Category;
import com.lagavulin.yoghee.model.dto.MainApiDto;
import com.lagavulin.yoghee.model.dto.YogaCenterDto;
import com.lagavulin.yoghee.model.dto.YogaClassDto;
import com.lagavulin.yoghee.model.dto.YogaReviewDto;
import com.lagavulin.yoghee.service.CategoryService;
import com.lagavulin.yoghee.service.CenterService;
import com.lagavulin.yoghee.service.ClassService;
import com.lagavulin.yoghee.service.LayoutService;
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

    private final LayoutService layoutService;
    private final ClassService classService;
    private final CenterService centerService;
    private final CategoryService categoryService;

    @GetMapping("/")
    @Operation(summary = "메인 API", description = "메인화면 정보 조회 API",
        responses = {
            @ApiResponse(responseCode = "200", description = "로그인 비로그인",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = com.lagavulin.yoghee.model.swagger.main.MainApiDto.class)
                )
            )
        })
    public ResponseEntity<?> mainApi(
        @Parameter(name = "Authorization", description = "[Header] 사용자 JWT 토큰") Principal principal,
        @Parameter(name = "type", description = "R: Regular 정규수련, O: One day 하루수련") @RequestParam(name = "type") String type) {

        String userUuid = principal != null ? principal.getName() : null;

        List<YogaClassDto> imageClass = classService.getMainDisplayNClass(type, 10);
        List<YogaReviewDto> newReviews = classService.getRecentNClassReviewWithImage(type, 5);

        List<YogaClassDto> todayClasses = getTodayClasses(userUuid);
        List<YogaClassDto> interestedClasses = getInterestedClasses(type, userUuid);
        List<YogaCenterDto> interestedCenters = getInterestedCenters(type, userUuid);
        List<YogaClassDto> top10Class = getTop10Classes(type);
        List<YogaCenterDto> top10Center = getTop10Centers(type);
        List<Category> categories = categoryService.getMainDisplay(type);

        return ResponseUtil.success(MainApiDto.builder()
                                              .layoutOrder(layoutService.getMainLayouts(type))
                                              .imageBanner(imageClass)
                                              .todayClass(todayClasses)
                                              .interestedClass(interestedClasses)
                                              .interestedCenter(interestedCenters)
                                              .top10Class(top10Class)
                                              .top10Center(top10Center)
                                              .newReview(newReviews)
                                              .yogaCategory(categories)
                                              .build());
    }

    private List<YogaClassDto> getTodayClasses(String userUuid) {
        if (userUuid == null) {
            return new ArrayList<>();
        }
        return classService.getTodaySchedule(userUuid);
    }

    private List<YogaClassDto> getInterestedClasses(String type, String userUuid) {
        if ("O".equals(type)) {
            return (userUuid != null)
                ? classService.getUserCategoryNClass(type, userUuid, 15)
                : classService.getClickedTopNClassLastMDays(type, 15, 3);
        }
        return null;
    }

    private List<YogaCenterDto> getInterestedCenters(String type, String userUuid) {
        if ("R".equals(type)) {
            return (userUuid != null)
                ? centerService.getUserCategoryNCenter(userUuid, 15) // 카테고리에 맞는 요가 추천
                : centerService.getClickedTopNCenterLastMDays(15, 3); // 최근 3일간 가장 많이 클릭한 요가센터
        }
        return null;
    }

    private List<YogaClassDto> getTop10Classes(String type) {
        return "O".equals(type)
            ? classService.getNewSignUpTopNClassSinceStartDate(type, 10)
            : null;
    }

    private List<YogaCenterDto> getTop10Centers(String type) {
        return "R".equals(type)
            ? centerService.getNewSignUpTopNCenterSinceStartDate(type, 10)
            : null;
    }
}
