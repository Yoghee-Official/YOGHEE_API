package com.lagavulin.yoghee.model.swagger.main;

import java.util.List;

import com.lagavulin.yoghee.entity.Layout;
import com.lagavulin.yoghee.model.dto.YogaReviewDto;
import com.lagavulin.yoghee.model.swagger.main.center.CenterDto;
import com.lagavulin.yoghee.model.swagger.main.classes.ClassDto;
import com.lagavulin.yoghee.model.swagger.main.classes.MainBannerClassDto;
import com.lagavulin.yoghee.model.swagger.main.classes.TodayClassDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainApiDto {

    @Schema(description = "오늘의 수련 목록")
    private List<TodayClassDto> todayClass;

    @Schema(description = "메인 배너 목록")
    private List<MainBannerClassDto> imageBanner;

    @Schema(description = "관심 수련 목록")
    private List<ClassDto> interestedClass;

    @Schema(description = "관심 요가원 목록")
    private List<CenterDto> interestedCenter;

    @Schema(description = "인기 수련 목록")
    private List<ClassDto> top10Class;

    @Schema(description = "인기 요가원 목록")
    private List<CenterDto> top10Center;

    @Schema(description = "최근 리뷰 목록")
    private List<YogaReviewDto> newReview;

    @Schema(description = "레이아웃 순서")
    private List<Layout> layoutOrder;
}
