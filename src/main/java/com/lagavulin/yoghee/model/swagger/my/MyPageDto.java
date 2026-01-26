package com.lagavulin.yoghee.model.swagger.my;

import java.util.List;

import com.lagavulin.yoghee.model.dto.UserProfileDto;
import com.lagavulin.yoghee.model.dto.YogaClassScheduleDto;
import com.lagavulin.yoghee.model.swagger.main.center.CenterDto;
import com.lagavulin.yoghee.model.swagger.main.classes.ClassDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "마이페이지 응답 DTO")
public class MyPageDto {

    @Schema(description = "유저 프로필 정보")
    private UserProfileDto userProfile;

    @Schema(description = "예약한 수련 미리보기 목록 (작년 이번달 ~ 내년 이번달)")
    private List<YogaClassScheduleDto> reservedClasses;

    @Schema(description = "이번 주 수련 목록 (평일/주말)")
    private WeekClasses weekClasses;

    @Schema(description = "찜한 수련 목록")
    private List<ClassDto> favoriteClasses;

    @Schema(description = "찜한 요가원 목록")
    private List<CenterDto> favoriteCenters;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeekClasses {

        @Schema(description = "이번 주 평일 수련 목록")
        private List<YogaClassScheduleDto> weekDay;

        @Schema(description = "이번 주 주말 수련 목록")
        private List<YogaClassScheduleDto> weekEnd;
    }
}
