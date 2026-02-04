package com.lagavulin.yoghee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.lagavulin.yoghee.entity.Image;
import com.lagavulin.yoghee.entity.UserCategory;
import com.lagavulin.yoghee.entity.UserClassSchedule;
import com.lagavulin.yoghee.entity.UserFavorite;
import com.lagavulin.yoghee.entity.YogaClass;
import com.lagavulin.yoghee.entity.YogaClassSchedule;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.AttendanceDto;
import com.lagavulin.yoghee.model.dto.CategoryClassDto;
import com.lagavulin.yoghee.model.dto.YogaClassDto;
import com.lagavulin.yoghee.model.dto.YogaReviewDto;
import com.lagavulin.yoghee.model.enums.AttendanceStatus;
import com.lagavulin.yoghee.model.enums.ClassSortType;
import com.lagavulin.yoghee.model.enums.TargetType;
import com.lagavulin.yoghee.repository.ImageRepository;
import com.lagavulin.yoghee.repository.UserCategoryRepository;
import com.lagavulin.yoghee.repository.UserClassScheduleRepository;
import com.lagavulin.yoghee.repository.UserFavoriteRepository;
import com.lagavulin.yoghee.repository.YogaClassRepository;
import com.lagavulin.yoghee.repository.YogaClassReviewRepository;
import com.lagavulin.yoghee.repository.YogaClassScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final YogaClassRepository yogaClassRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final YogaClassReviewRepository yogaClassReviewRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final ImageRepository imageRepository;
    private final UserClassScheduleRepository userClassScheduleRepository;
    private final YogaClassScheduleRepository yogaClassScheduleRepository;

    /**
     * 오늘 스케줄 조회 <br> /api/main/ - [로그인] data.todaySchedule
     */
    public List<YogaClassDto> getTodaySchedule(String userUuid) {
        return yogaClassRepository.findTodayClassesByUser(userUuid);
    }

    /**
     * 클릭수 순으로 N개 (최근 M일 이내), 부족한 경우 랜덤으로 N개 보충 <br> /api/main/ - [비로그인] data.customizedClass
     */
    public List<YogaClassDto> getClickedTopNClassLastMDays(String type, int n, int m) {
        LocalDate fromDate = LocalDate.now().minusDays(m);
        Set<String> classIds = new HashSet<>(yogaClassRepository.findTopClickedClasses(type, fromDate, PageRequest.of(0, n)));
        if (classIds.size() < n) {
            int remaining = n - classIds.size();
            classIds.addAll(yogaClassRepository.findRandomNClassByType(type, PageRequest.of(0, remaining)));
        }
        return yogaClassRepository.findWithReviewStatsByClassIds(classIds, null);
    }

    /**
     * 사용자 관심 카테고리 기반 클래스 N개 (랜덤) <br> /api/main/ - [로그인] data.customizedClass
     */
    public List<YogaClassDto> getUserCategoryNClass(String type, String userUuid, int n) {
        List<String> categoryIds = userCategoryRepository.findAllByUserUuid(userUuid)
                                                         .stream()
                                                         .map(UserCategory::getCategoryId)
                                                         .toList();

        Set<String> classIds = new HashSet<>(yogaClassRepository.findRandomByCategoryIds(type, categoryIds, PageRequest.of(0, n))
                                                                .stream()
                                                                .map(YogaClass::getClassId)
                                                                .toList());
        if (classIds.size() < n) {
            int remaining = n - classIds.size();
            classIds.addAll(yogaClassRepository.findRandomNClassByType(type, PageRequest.of(0, remaining)));
        }

        return yogaClassRepository.findWithReviewStatsByClassIds(classIds, userUuid);
    }

    /**
     * 최근 7일 이내 신규 회원이 가장 많이 등록한 클래스 N개, 부족한 경우 랜덤으로 N개 보충 <br> /api/main/ - [공통] data.recommendClass<br>
     */
    public List<YogaClassDto> getNewSignUpTopNClassSinceStartDate(String type, int n, String userUuid) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        Set<String> classIds = new HashSet<>(yogaClassRepository.findNewSignUpTopNClassSinceStartDate(type, oneWeekAgo, PageRequest.of(0, n)));
        return yogaClassRepository.findWithReviewStatsByClassIds(classIds, userUuid);
    }

    /**
     * 메인 노출 클래스 N개 (랜덤) - MAIN_DISPLAY = 'Y' <br> /api/main/ - [공통] data.recommendClass<br>
     */
    public List<YogaClassDto> getMainDisplayNClass(String type, int n) {
        return yogaClassRepository.findAllByMainDisplay(type, PageRequest.of(0, n));
    }

    /**
     * 이미지 있는 후기 최근순으로 N개 /api/main/ - [공통] data.newReview<br>
     */
    public List<YogaReviewDto> getRecentNClassReviewWithImage(String type, int n) {
        return yogaClassReviewRepository.findYogaClassReviewByCreatedAt(type, PageRequest.of(0, n));

    }

    public List<CategoryClassDto> getCategoryClasses(String type, String categoryId, ClassSortType classSortType, String userUuid) {
        List<CategoryClassDto> dtos = switch (classSortType) {
            case RECOMMEND -> yogaClassRepository.findMostJoinedClassByTypeAndCategoryId(type, categoryId, userUuid);
            case REVIEW -> yogaClassRepository.findHighestRatedClassByTypeAndCategoryId(type, categoryId, userUuid);
            case RECENT -> yogaClassRepository.findRecentClassByTypeAndCategoryId(type, categoryId, userUuid);
            case FAVORITE -> yogaClassRepository.findMostFavoritedClassByTypeAndCategoryId(type, categoryId, userUuid);
            case EXPENSIVE -> yogaClassRepository.findMostExpensiveClassByTypeAndCategoryId(type, categoryId, userUuid);
            case CHEAP -> yogaClassRepository.findCheapestClassByTypeAndCategoryId(type, categoryId, userUuid);
        };

        if (dtos == null || dtos.isEmpty()) {
            return dtos;
        }

        // 클래스 ID 추출
        List<String> classIds = dtos.stream().map(CategoryClassDto::getClassId).collect(Collectors.toList());

        // 이미지 조회 (한 번에 여러 타겟 id로 조회)
        List<Image> images = imageRepository.findByTypeAndTargetIdInOrderByTargetIdAscOrderNoAsc(TargetType.CLASS, classIds);

        // 클래스Id -> List<url> 매핑
        Map<String, List<String>> imageMap = new HashMap<>();
        for (Image img : images) {
            List<String> list = imageMap.computeIfAbsent(img.getTargetId(), k -> new java.util.ArrayList<>());
            // 최대 5개까지만 수집
            if (list.size() < 5) {
                list.add(img.getUrl());
            }
        }

        // DTO에 images 채우기 (이미지 없으면 기존 썸네일을 단일 이미지로 사용)
        for (CategoryClassDto dto : dtos) {
            List<String> imgs = imageMap.get(dto.getClassId());
            if (imgs == null || imgs.isEmpty()) {
                // CategoryClassDto의 생성자는 thumbnail(단일 이미지)로 받아 List로 변환하도록 되어 있음
                // DTO에는 이미 thumbnail로 초기화 되어 있을 수 있으므로 그대로 두거나 빈 리스트로 설정
                dto.setImages(List.of());
            } else {
                dto.setImages(imgs);
            }
        }

        return dtos;
    }

    public void addFavoriteClass(String userUuid, String classId) {
        userFavoriteRepository.save(UserFavorite.builder()
                                                .id(classId)
                                                .type(TargetType.CLASS)
                                                .userUuid(userUuid)
                                                .createdAt(new Date())
                                                .build());
    }

    /**
     * 특정 스케줄의 참석자 목록 조회
     *
     * @param scheduleId 스케줄 ID
     * @param userUuid   사용자 UUID (지도자 권한 확인용)
     * @return 참석자 목록
     */
    public List<AttendanceDto> getClassScheduleAttendance(String scheduleId, String userUuid) {
        // 1. 스케줄 조회
        YogaClassSchedule schedule = yogaClassScheduleRepository.findById(scheduleId)
                                                                .orElseThrow(
                                                                    () -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND, "스케줄을 찾을 수 없습니다."));

        // 2. 클래스 조회
        YogaClass yogaClass = yogaClassRepository.findById(schedule.getClassId())
                                                 .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "클래스를 찾을 수 없습니다."));

        // 3. 지도자 권한 확인
        if (!yogaClass.getMasterId().equals(userUuid)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 클래스의 지도자만 참석자 목록을 조회할 수 있습니다.");
        }

        // 4. 참석자 목록 조회
        List<Object[]> rawResults = userClassScheduleRepository.findAttendancesByScheduleIdRaw(scheduleId);

        return rawResults.stream()
                         .map(row -> new AttendanceDto(
                             (String) row[0],           // attendanceId
                             (String) row[1],           // userUuid
                             (String) row[2],           // userName (nickname)
                             (Date) row[3],             // scheduleDate
                             row[4] != null ? row[4].toString() : null,  // startTime
                             row[5] != null ? row[5].toString() : null,  // endTime
                             (Integer) row[6],          // attendeeCount
                             (String) row[7]            // status
                         ))
                         .collect(Collectors.toList());
    }

    /**
     * 스케줄의 모든 참석자를 출석 처리
     *
     * @param scheduleId 스케줄 ID
     * @param userUuid   사용자 UUID (지도자 권한 확인용)
     * @return 출석 처리된 참석자 수
     */
    public int markAllAttendance(String scheduleId, String userUuid) {
        // 1. 지도자 권한 확인
        validateMasterPermission(scheduleId, userUuid);

        // 2. REGISTERED 상태의 참석자들을 ATTENDED로 일괄 변경
        return userClassScheduleRepository.updateStatusByScheduleId(
            scheduleId,
            AttendanceStatus.REGISTERED,
            AttendanceStatus.ATTENDED
        );
    }

    /**
     * 특정 참석자를 출석 처리
     *
     * @param attendanceId 참석 ID
     * @param userUuid     사용자 UUID (지도자 권한 확인용)
     */
    public void markAttendance(String attendanceId, String userUuid) {
        // 1. 참석 정보 조회
        UserClassSchedule attendance = userClassScheduleRepository.findById(attendanceId)
                                                                  .orElseThrow(
                                                                      () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "참석 정보를 찾을 수 없습니다."));

        // 2. 지도자 권한 확인
        validateMasterPermission(attendance.getScheduleId(), userUuid);

        // 3. 출석 상태로 변경
        attendance.setStatus(AttendanceStatus.ATTENDED);
        userClassScheduleRepository.save(attendance);
    }

    /**
     * 지도자 권한 확인 (공통 메서드)
     *
     * @param scheduleId 스케줄 ID
     * @param userUuid   사용자 UUID
     */
    private void validateMasterPermission(String scheduleId, String userUuid) {
        YogaClassSchedule schedule = yogaClassScheduleRepository.findById(scheduleId)
                                                                .orElseThrow(
                                                                    () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "스케줄을 찾을 수 없습니다."));

        YogaClass yogaClass = yogaClassRepository.findById(schedule.getClassId())
                                                 .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "클래스를 찾을 수 없습니다."));

        if (!yogaClass.getMasterId().equals(userUuid)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 클래스의 지도자만 출석 처리를 할 수 있습니다.");
        }
    }
}
