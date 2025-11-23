package com.lagavulin.yoghee.model.swagger.main;

import java.util.List;

import com.lagavulin.yoghee.entity.Category;
import com.lagavulin.yoghee.entity.Layout;
import com.lagavulin.yoghee.model.dto.YogaReviewDto;
import com.lagavulin.yoghee.model.swagger.main.center.CenterDto;
import com.lagavulin.yoghee.model.swagger.main.classes.ClassDto;
import com.lagavulin.yoghee.model.swagger.main.classes.MainBannerClassDto;
import com.lagavulin.yoghee.model.swagger.main.classes.TodayClassDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainApiDto {

    private List<TodayClassDto> todayClass;
    private List<MainBannerClassDto> imageBanner;
    private List<ClassDto> interestedClass;
    private List<CenterDto> interestedCenter;
    private List<ClassDto> top10Class;
    private List<CenterDto> top10Center;
    private List<YogaReviewDto> newReview;
    private List<Category> yogaCategory;
    private List<Layout> layoutOrder;
}
