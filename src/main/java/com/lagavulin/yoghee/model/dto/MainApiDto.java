package com.lagavulin.yoghee.model.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lagavulin.yoghee.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter

public class MainApiDto {
    private List<TodayClassDto> todayClass;
    private List<YogaClassDto> recommendClass;
    private List<YogaClassDto> customizedClass;
    private List<YogaClassDto> hotClass;
    private List<YogaReviewDto> newReview;
    private List<Category> yogaCategory;
    private List<String> layoutOrder;
}
