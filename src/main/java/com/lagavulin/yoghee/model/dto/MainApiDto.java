package com.lagavulin.yoghee.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lagavulin.yoghee.entity.Category;
import com.lagavulin.yoghee.entity.Layout;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MainApiDto {
    private List<YogaClassDto> todayClass;

    private List<YogaClassDto> imageBanner;

    private List<YogaClassDto> interestedClass;
    private List<YogaCenterDto> interestedCenter;

    private List<YogaClassDto> top10Class;
    private List<YogaCenterDto> top10Center;

    private List<YogaReviewDto> newReview;
    private List<Category> yogaCategory;

    private List<Layout> layoutOrder;
}