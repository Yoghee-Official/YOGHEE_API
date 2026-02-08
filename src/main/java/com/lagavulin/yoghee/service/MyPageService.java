package com.lagavulin.yoghee.service;


import java.sql.Timestamp;
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
import com.lagavulin.yoghee.entity.YogaClass;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.*;
import com.lagavulin.yoghee.model.enums.AttendanceStatus;
import com.lagavulin.yoghee.repository.AppUserRepository;
import com.lagavulin.yoghee.repository.LicenseRepository;
import com.lagavulin.yoghee.repository.UserClassScheduleRepository;
import com.lagavulin.yoghee.repository.YogaCenterRepository;
import com.lagavulin.yoghee.repository.YogaClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final AppUserRepository appUserRepository;
    private final LicenseRepository licenseRepository;
    private final YogaClassRepository yogaClassRepository;
    private final UserClassScheduleRepository userClassScheduleRepository;
    private final YogaCenterRepository yogaCenterRepository;
    private final ImageService imageService;
    private final EmailService emailService;
    private static final int GRADE_SIZE = 7 * 180;
    private static final int LEVEL_SIZE = 180;

    /**
     * 자격증 이미지 등록 1. imageKey(objectKey)로 파일을 Public Read로 설정 2. Public URL 생성 3. UserLicense에 저장 4. 관리자에게 승인 요청 이메일 발송
     *
     * @param userUuid 사용자 UUID
     * @param imageKey Presign API에서 받은 imageKey (예: license/images/xxx.jpg)
     * @return 생성된 Public URL
     */
    public String saveUserLicense(String userUuid, String imageKey) {
        if (userUuid == null || userUuid.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "사용자 계정 정보를 알 수 없습니다.");
        }

        if (imageKey == null || imageKey.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미지 키가 유효하지 않습니다.");
        }

        // license 타입 검증
        if (!imageKey.startsWith("license/")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "자격증 이미지 경로가 아닙니다.");
        }

        // 사용자 정보 조회
        AppUser user = appUserRepository.findById(userUuid)
                                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 파일을 Public Read로 설정
        imageService.makeFilePublic(imageKey);

        // Public URL 생성
        String imageUrl = imageService.getPublicUrl(imageKey);

        // 자격증 저장
        UserLicense license = licenseRepository.save(UserLicense.builder()
                                                                .userUuid(userUuid)
                                                                .imageUrl(imageUrl)
                                                                .status("U") // Uploaded
                                                                .build());

        // 관리자에게 승인 요청 이메일 발송
        try {
            emailService.sendLicenseApprovalRequestToAdmin(license, user);
        } catch (Exception e) {
            log.error("관리자 이메일 발송 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "자격증 승인 요청 이메일 발송에 실패했습니다.");
        }

        return imageUrl;
    }

    /**
     * 프로필 이미지 변경 1. imageKey(objectKey)로 파일을 Public Read로 설정 2. Public URL 생성 3. AppUser의 profileUrl 업데이트
     *
     * @param userUuid 사용자 UUID
     * @param imageKey Presign API에서 받은 imageKey (예: profile/images/xxx.jpg)
     * @return 생성된 Public URL
     */
    public String updateProfileImage(String userUuid, String imageKey) {
        if (userUuid == null || userUuid.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "사용자 계정 정보를 알 수 없습니다.");
        }

        if (imageKey == null || imageKey.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "이미지 키가 유효하지 않습니다.");
        }

        // profile 타입 검증
        if (!imageKey.startsWith("profile/")) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "프로필 이미지 경로가 아닙니다.");
        }

        AppUser appUser = appUserRepository.findById(userUuid)
                                           .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 파일을 Public Read로 설정
        imageService.makeFilePublic(imageKey);

        // Public URL 생성
        String imageUrl = imageService.getPublicUrl(imageKey);

        appUser.setProfileUrl(imageUrl);
        appUserRepository.save(appUser);

        return imageUrl;
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

        List<YogaClassScheduleDto> reservedClasses = convertToDto(
            userClassScheduleRepository.findSchedulesBetweenDatesRaw(userUuid, reservedStartDate, reservedEndDate)
        );

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<YogaClassScheduleDto> weekSchedules = convertToDto(
            userClassScheduleRepository.findSchedulesBetweenDatesRaw(userUuid, startDate, endDate)
        );

        Map<Boolean, List<YogaClassScheduleDto>> partitioned = weekSchedules.stream()
                                                                            .collect(Collectors.partitioningBy(dto -> {
                                                                                Long dow = dto.getDayOfWeek();
                                                                                return dow != null && dow >= 1L && dow <= 5L; // 1..5 = Mon..Fri
                                                                            }));

        List<YogaClassScheduleDto> weekDayClasses = partitioned.getOrDefault(true, List.of());
        List<YogaClassScheduleDto> weekEndClasses = partitioned.getOrDefault(false, List.of());

        CategoryCountDto categoryInfo = getTopCategoryForThisMonth(userUuid);
        List<YogaClassDto> favoriteClasses = convertOneDayClassToDto(
            yogaClassRepository.findUserFavoriteClassesRaw(userUuid)
        );
        List<YogaCenterDto> favoriteCenters = yogaCenterRepository.findUserFavoriteCenterRaw(userUuid);

        return MyPageDto.builder()
                        .userProfile(UserProfileDto.builder()
                                                   .nickname(appUser.getNickname())
                                                   .profileImage(appUser.getProfileUrl())
                                                   .totalClass(attendedCount)
                                                   .plannedClass(plannedCount)
                                                   .totalHour(formatHours(Math.max(0, totalMinutes / 60)))
                                                   .grade(getGrade(totalMinutes))
                                                   .level(getLevel(totalMinutes))
                                                   .monthlyCategory(categoryInfo != null ? categoryInfo.getCategoryName() : "없음")
                                                   .monthlyCategoryCount(categoryInfo != null ? categoryInfo.getCount() : 0L)
                                                   .build()
                        )
                        .weekClasses(MyPageDto.WeekClasses.builder()
                                                          .weekDay(weekDayClasses)
                                                          .weekEnd(weekEndClasses)
                                                          .build()
                        )
                        .reservedClasses(reservedClasses)
                        .favoriteClasses(favoriteClasses)
                        .favoriteCenters(favoriteCenters)
                        .build();
    }

    public LeaderPageDto getLeaderPage(String userUuid) {
        AppUser appUser = appUserRepository.findById(userUuid)
                                           .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 지도자 자격증 확인 (심사 중인 자격증도 포함)
        UserLicense userLicense = licenseRepository.findTopByUserUuidOrderByCreatedAtDesc(userUuid);

        if (userLicense == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "자격증이 등록되지 않은 계정입니다. 자격증을 먼저 등록해주세요.");
        }

        String certificate = null;
        // 심사 중인 경우
        if ("U".equals(userLicense.getStatus())) {
            certificate = "심사 중";
        }
        // 대표 자격증 조회 (승인된 자격증 중 가장 최근 것)
        UserLicense approvedLicense = licenseRepository.findTopByUserUuidAndStatusOrderByCreatedAtDesc(userUuid, "A");
        if (approvedLicense != null) {
            if (approvedLicense.getLicenseType() != null) {
                certificate = approvedLicense.getLicenseType().name();
            } else if (approvedLicense.getCustomLicenseTypeName() != null) {
                certificate = approvedLicense.getCustomLicenseTypeName();
            }
        }
        // 지도자가 개설한 클래스 조회 (선택 사항)
        List<YogaClass> myClasses = yogaClassRepository.findByMasterId(userUuid);

        // 누적 리뷰 수 (내가 개설한 모든 클래스의 리뷰 수 합계)
        Long totalReview = yogaClassRepository.countAllReviewsByMasterId(userUuid);

        // 개설된 수련 수
        int totalMyClass = (myClasses != null) ? myClasses.size() : 0;

        // 이번 달 예약이 가장 많은 카테고리 조회
        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.from(today);
        LocalDate startLocal = ym.atDay(1);
        LocalDate endLocal = ym.atEndOfMonth();

        Date startDate = Date.from(startLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endLocal.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        CategoryCountDto popularCategoryInfo = yogaClassRepository.findMostReservedCategoryByMasterIdForPeriod(
            userUuid, startDate, endDate
        );

        String popularCategory = (popularCategoryInfo != null) ? popularCategoryInfo.getCategoryName() : "";
        Long reservedCount = (popularCategoryInfo != null) ? popularCategoryInfo.getCount() : 0L;

        // 오늘의 수업 (오늘 날짜와 요일에 해당하는 스케줄)
        LocalDate nowDate = LocalDate.now();
        Date todayStart = Date.from(nowDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date todayEnd = Date.from(nowDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<YogaClassScheduleDto> todayClasses = convertToDto(
            yogaClassRepository.findTodayClassesByMasterIdRaw(userUuid, todayStart, todayEnd)
        );

        // 예약된 수업 목록 (작년 이번달 1일 ~ 내년 이번달 말일)
        LocalDate reservedStartLocal = today.minusYears(1).withDayOfMonth(1);
        LocalDate reservedEndLocal = YearMonth.from(today.plusYears(1)).atEndOfMonth();

        Date reservedStartDate = Date.from(reservedStartLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date reservedEndDate = Date.from(reservedEndLocal.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<YogaClassScheduleDto> reservedClasses = convertToDto(
            yogaClassRepository.findSchedulesByMasterIdBetweenDatesRaw(userUuid, reservedStartDate, reservedEndDate)
        );

        return LeaderPageDto.builder()
                            .leaderProfile(LeaderProfileDto.builder()
                                                           .nickname(appUser.getNickname())
                                                           .profileImage(appUser.getProfileUrl())
                                                           .totalReview(totalReview)
                                                           .totalMyClass(totalMyClass)
                                                           .introduction(appUser.getIntroduction())
                                                           .certificate(certificate)
                                                           .popularCategory(popularCategory)
                                                           .reservedCount(reservedCount)
                                                           .build()
                            )
                            .todayClasses(todayClasses)
                            .reservedClasses(reservedClasses)
                            .build();
    }


    public void updateLeaderIntroduction(String name, UpdateLeaderIntroductionDto introduction) {
        AppUser appUser = appUserRepository.findById(name)
                                           .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        appUser.setIntroduction(introduction.getIntroduction());
        appUserRepository.save(appUser);
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

        Date startDate = Date.from(startLocal.atStartOfDay(ZoneId.systemDefault())
                                             .toInstant());
        Date endDate = Date.from(endLocal.atTime(LocalTime.MAX)
                                         .atZone(ZoneId.systemDefault())
                                         .toInstant());

        List<CategoryCountDto> counts = userClassScheduleRepository.findTopCategoriesByUserForPeriod(userUuid, startDate, endDate);
        if (counts == null || counts.isEmpty()) {
            return null;
        }
        return counts.get(0);
    }

    private List<YogaClassScheduleDto> convertToDto(List<Object[]> rawResults) {
        return rawResults.stream()
                         .map(row -> {
                             String classId = (String) row[0];
                             String className = (String) row[1];
                             // row[2]는 Timestamp 또는 Date일 수 있으므로 안전하게 처리
                             Date day;
                             if (row[2] instanceof Timestamp) {
                                 day = new Date(((Timestamp) row[2]).getTime());
                             } else if (row[2] instanceof java.sql.Date) {
                                 day = new Date(((java.sql.Date) row[2]).getTime());
                             } else if (row[2] instanceof Date) {
                                 day = (Date) row[2];
                             } else {
                                 throw new IllegalArgumentException("Unexpected date type: " + row[2].getClass());
                             }
                             Integer dayOfWeek = (Integer) row[3];
                             String thumbnailUrl = (String) row[4];
                             String address = (String) row[5];
                             Long attendance = ((Number) row[6]).longValue();
                             String categoriesStr = (String) row[7];

                             return new YogaClassScheduleDto(classId, className, day, dayOfWeek.longValue(),
                                 thumbnailUrl, address, attendance, categoriesStr);
                         })
                         .collect(Collectors.toList());
    }

    private List<YogaClassDto> convertOneDayClassToDto(List<Object[]> rawResults) {
        return rawResults.stream()
                         .map(row -> {
                             String classId = (String) row[0];
                             String className = (String) row[1];
                             String thumbnail = (String) row[2];
                             String masterId = (String) row[3];
                             String masterName = (String) row[4];
                             // row[5] is NULL
                             Number rating = (Number) row[6];
                             Long reviewCount = ((Number) row[7]).longValue();
                             String categoriesStr = (String) row[8];

                             return new YogaClassDto(classId, className, thumbnail, masterId, masterName,
                                 null, rating, reviewCount, categoriesStr);
                         })
                         .collect(Collectors.toList());
    }
}
