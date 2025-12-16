package com.lagavulin.yoghee.service;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lagavulin.yoghee.entity.AppUser;
import com.lagavulin.yoghee.entity.UserLicense;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.CategoryCountDto;
import com.lagavulin.yoghee.model.dto.FavoriteOneDayClassDto;
import com.lagavulin.yoghee.model.dto.FavoriteRegularClassDto;
import com.lagavulin.yoghee.model.dto.MyPageDto;
import com.lagavulin.yoghee.model.dto.YogaClassScheduleDto;
import com.lagavulin.yoghee.model.enums.AttendanceStatus;
import com.lagavulin.yoghee.repository.AppUserRepository;
import com.lagavulin.yoghee.repository.LicenseRepository;
import com.lagavulin.yoghee.repository.UserClassScheduleRepository;
import com.lagavulin.yoghee.repository.YogaClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final AppUserRepository appUserRepository;
    private final LicenseRepository licenseRepository;
    private final YogaClassRepository yogaClassRepository;
    private final UserClassScheduleRepository userClassScheduleRepository;
    private static final int GRADE_SIZE = 7 * 180;
    private static final int LEVEL_SIZE = 180;

    public void saveUserLicense(String userUuid, String imageUrl) {
        if (userUuid == null || userUuid.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "사용자 계정 정보를 알 수 없습니다.");
        }

        if (imageUrl == null || !imageUrl.contains("/license")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미지 URL이 유효하지 않습니다.");
        }

        licenseRepository.save(UserLicense.builder()
                                          .userUuid(userUuid)
                                          .imageUrl(imageUrl)
                                          .status("U")
                                          .build());
    }

    public MyPageDto getMyPage(String userUuid) {
        AppUser appUser = appUserRepository.findById(userUuid)
                                           .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        int attendedCount = userClassScheduleRepository.countByUserUuidAndStatus(userUuid, AttendanceStatus.ATTENDED);
        int plannedCount = userClassScheduleRepository.countByUserUuidAndStatus(userUuid, AttendanceStatus.REGISTERED);
        int totalMinutes = userClassScheduleRepository.sumDurationMinutesByUserUuidAndStatus(userUuid, AttendanceStatus.ATTENDED);

        // reserved: 작년 이번달 1일 ~ 내년 이번달 말일까지, 상태 상관없이 조회
        LocalDate today = LocalDate.now();
        LocalDate reservedStartLocal = today.minusYears(1).withDayOfMonth(1);
        LocalDate reservedEndLocal = YearMonth.from(today.plusYears(1)).atEndOfMonth();

        Date reservedStartDate = Date.from(reservedStartLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date reservedEndDate = Date.from(reservedEndLocal.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<YogaClassScheduleDto> reservedClasses = userClassScheduleRepository.findSchedulesBetweenDates(userUuid, reservedStartDate,
            reservedEndDate);

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<YogaClassScheduleDto> weekSchedules = userClassScheduleRepository.findSchedulesBetweenDates(userUuid, startDate, endDate);

        Map<Boolean, List<YogaClassScheduleDto>> partitioned = weekSchedules.stream()
                                                                            .collect(Collectors.partitioningBy(dto -> {
                                                                                Integer dow = dto.getDayOfWeek();
                                                                                return dow != null && dow >= 1 && dow <= 5; // 1..5 = Mon..Fri
                                                                            }));

        List<YogaClassScheduleDto> weekDayClasses = partitioned.getOrDefault(true, List.of());
        List<YogaClassScheduleDto> weekEndClasses = partitioned.getOrDefault(false, List.of());

        CategoryCountDto categoryInfo = getTopCategoryForThisMonth(userUuid);
        List<FavoriteOneDayClassDto> favoriteOneDayClasses = yogaClassRepository.findUserFavoriteOneDayClasses(userUuid);
        List<FavoriteRegularClassDto> favoriteRegularClasses = yogaClassRepository.findUserFavoriteRegularClasses(userUuid);

        return MyPageDto.builder()
                        .nickname(appUser.getNickname())
                        .profileImage(appUser.getProfileUrl())
                        .accumulatedClass(attendedCount)
                        .plannedClass(plannedCount)
                        .accumulatedHours(formatHours(Math.max(0, totalMinutes / 60)))
                        .grade(getGrade(totalMinutes))
                        .level(getLevel(totalMinutes))
                        .monthlyCategory(categoryInfo != null ? categoryInfo.getCategoryName() : null)
                        .monthlyCategoryCount(categoryInfo != null ? categoryInfo.getCount() : 0L)
                        .reservedClasses(reservedClasses)
                        .weekDayClasses(weekDayClasses)
                        .weekEndClasses(weekEndClasses)
                        .favoriteOneDayClasses(favoriteOneDayClasses)
                        .favoriteRegularClasses(favoriteRegularClasses)
                        .build();
    }

    private String formatHours(int hours) {
        String hourDisplay;
        if (hours > 99999) {
            hourDisplay = "99,999+";
        } else {
            hourDisplay = String.format("%,d", hours);
        }

        String footerMessage;
        if (hours == 0) {
            footerMessage = "첫 시작은 언제나 설레죠!\n지금 바로 첫 수련을 떠나볼까요?";
        } else if (hours <= 10) {
            footerMessage = "작지만 확실한 변화가 시작됐어요!\n몸과 마음이 서서히 깨어나는 시간이에요.";
        } else if (hours <= 30) {
            footerMessage = "기운이 아주 단단해졌어요!\n요가가 일상 속 리듬이 되어가고 있어요.";
        } else if (hours <= 50) {
            footerMessage = "와, 흐름이 잡혔어요!\n더 깊은 에너지로 부드럽게 움직이는 단계예요.";
        } else if (hours <= 100) {
            footerMessage = "곧 공중부양 하겠어요!\n수련의 흔적이 몸과 마음에 차곡차곡 쌓이고 있어요.";
        } else if (hours <= 150) {
            footerMessage = "진짜 요기니 그 자체!\n당신의 집중력과 꾸준함이 가장 아름답게 드러나는 순간이에요.";
        } else {
            footerMessage = "고요하고 단단한 에너지가 느껴져요!\n이제 한 단계 더 넓은 수련으로 떠나볼까요?";
        }

        return "총 " + hourDisplay + "시간, " + footerMessage;
    }

    private String getGrade(int totalMinutes) {
        if (totalMinutes < GRADE_SIZE) {
            return "시작";
        } else if (totalMinutes < GRADE_SIZE * 2) {
            return "호흡";
        } else if (totalMinutes < GRADE_SIZE * 3) {
            return "흐름";
        } else if (totalMinutes < GRADE_SIZE * 4) {
            return "균형";
        } else if (totalMinutes < GRADE_SIZE * 5) {
            return "정제";
        } else if (totalMinutes < GRADE_SIZE * 6) {
            return "집중";
        } else {
            return "통합";
        }
    }

    private int getLevel(int totalMinutes) {
        return totalMinutes / LEVEL_SIZE % 7 + 1;
    }

    public CategoryCountDto getTopCategoryForThisMonth(String userUuid) {
        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);
        LocalDate startLocal = ym.atDay(1);
        LocalDate endLocal = ym.atEndOfMonth();

        Date startDate = Date.from(startLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocal.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<CategoryCountDto> counts = userClassScheduleRepository.findTopCategoriesByUserForPeriod(userUuid, startDate, endDate);
        if (counts == null || counts.isEmpty()) {
            return null;
        }
        return counts.get(0);
    }
}
