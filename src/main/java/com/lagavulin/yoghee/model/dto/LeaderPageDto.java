package com.lagavulin.yoghee.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "지도자 페이지 응답 DTO")
public class LeaderPageDto {

    @Schema(description = "지도자 프로필 정보")
    private LeaderProfileDto leaderProfile;

    @Schema(description = "오늘의 수련 목록")
    private List<YogaClassScheduleDto> todayClasses;

    @Schema(description = "수련 미리보기 목록 (작년 이번달 ~ 내년 이번달)")
    private List<YogaClassScheduleDto> reservedClasses;
}

