package com.lagavulin.yoghee.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPageDto {

    @Schema(description = "유저 닉네임", example = "앨리스")
    private String nickname;

    @Schema(description = "유저 프로필 이미지 URL", example = "https://profile_image_url")
    private String profileImage;

    @Schema(description = "누적 수련 수", example = "123")
    private Integer accumulatedClass;

    @Schema(description = "예정된 수련 수", example = "6")
    private Integer plannedClass;

    @Schema(description = "누적 수련 시간 관련 문구", example = "총 203시간 동안 좋은 에너지를 나누셨네요!\n공중부양 요기니로 거듭나볼까요?")
    private String accumulatedHours;

    @Schema(description = "유저 단계", example = "시작, 호흡, 흐름, 균형, 정제, 집중, 통합")
    private String grade;

    @Schema(description = "유저 레벨", example = "1~7")
    private Integer level;

    @Schema(description = "월간 상위 카테고리 수련 횟수", example = "25")
    private Long monthlyCategoryCount;

    @Schema(description = "월간 상위 카테고리 항목", example = "하타")
    private String monthlyCategory;

    @Schema(description = "예약한 수련 미리보기 목록 (작년 이번달 ~ 내년 이번달)")
    private List<YogaClassScheduleDto> reservedClasses;

    @Schema(description = "이번 주 평일 수련 목록")
    private List<YogaClassScheduleDto> weekDayClasses;

    @Schema(description = "이번 주 주말 수련 목록")
    private List<YogaClassScheduleDto> weekEndClasses;

    @Schema(description = "찜한 하루수련 목록")
    private List<FavoriteRegularClassDto> favoriteRegularClasses;

    @Schema(description = "찜한 정규수련 목록")
    private List<FavoriteOneDayClassDto> favoriteOneDayClasses;
}
