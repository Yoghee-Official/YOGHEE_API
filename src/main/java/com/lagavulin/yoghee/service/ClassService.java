package com.lagavulin.yoghee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.lagavulin.yoghee.entity.*;
import com.lagavulin.yoghee.exception.BusinessException;
import com.lagavulin.yoghee.exception.ErrorCode;
import com.lagavulin.yoghee.model.dto.AttendanceDto;
import com.lagavulin.yoghee.model.dto.CategoryClassDto;
import com.lagavulin.yoghee.model.dto.NewClassDto;
import com.lagavulin.yoghee.model.dto.NewScheduleDto;
import com.lagavulin.yoghee.model.dto.YogaClassDto;
import com.lagavulin.yoghee.model.dto.YogaReviewDto;
import com.lagavulin.yoghee.model.enums.AttendanceStatus;
import com.lagavulin.yoghee.model.enums.ClassSortType;
import com.lagavulin.yoghee.model.enums.TargetType;
import com.lagavulin.yoghee.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    private final CategoryRepository categoryRepository;
    private final FeatureRepository featureRepository;
    private final YogaCenterAddressRepository yogaCenterAddressRepository;
    private final ScheduleCategoryRepository scheduleCategoryRepository;

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

    @Transactional
    public void createOrUpdateOneDayClass(String userUuid, NewClassDto newClassDto) {
        if (newClassDto.getClassId() != null) {
            // 수정 로직
            updateOneDayClass(userUuid, newClassDto);
        } else {
            // 생성 로직
            createOneDayClass(userUuid, newClassDto);
        }
    }

    /**
     * 하루수련 클래스 생성
     */
    private void createOneDayClass(String userUuid, NewClassDto newClassDto) {
        // 0. addressId로 주소 조회
        String fullAddress = null;
        if (newClassDto.getAddressId() != null) {
            fullAddress = yogaCenterAddressRepository.findById(newClassDto.getAddressId())
                                                     .map(addr -> addr.getFullAddress())
                                                     .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND, "주소를 찾을 수 없습니다."));
        }

        // 1. YogaClass 생성
        YogaClass yogaClass = YogaClass.builder()
                                       .name(newClassDto.getName())
                                       .type("O") // 하루수련
                                       .description(newClassDto.getDescription())
                                       .addressId(newClassDto.getAddressId())
                                       .address(fullAddress)  // fullAddress 설정
                                       .price(newClassDto.getPrice().intValue())
                                       .masterId(userUuid)
                                       .mainDisplay("N")
                                       .createdAt(new Date())
                                       .categories(new HashSet<>())
                                       .features(new ArrayList<>())
                                       .build();

        // 2. 첫 번째 썸네일 이미지 설정 (있는 경우)
        if (newClassDto.getImages() != null && !newClassDto.getImages().isEmpty()) {
            yogaClass.setThumbnail(newClassDto.getImages().get(0));
        }

        YogaClass savedClass = yogaClassRepository.save(yogaClass);
        log.info("Created YogaClass: {}", savedClass.getClassId());

        // 3. 이미지 저장
        if (newClassDto.getImages() != null && !newClassDto.getImages().isEmpty()) {
            saveClassImages(savedClass.getClassId(), newClassDto.getImages());
        }

        // 4. 특징 저장
        if (newClassDto.getFeaturesId() != null && !newClassDto.getFeaturesId().isEmpty()) {
            saveClassFeatures(savedClass, newClassDto.getFeaturesId());
        }

        // 5. 스케줄 저장 (스케줄별 카테고리도 함께 저장)
        if (newClassDto.getSchedules() != null && !newClassDto.getSchedules().isEmpty()) {
            saveClassSchedules(savedClass.getClassId(), newClassDto.getSchedules());
        }
    }

    /**
     * 하루수련 클래스 수정
     */
    private void updateOneDayClass(String userUuid, NewClassDto newClassDto) {
        // 1. 기존 클래스 조회
        YogaClass existingClass = yogaClassRepository.findById(newClassDto.getClassId())
                                                     .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "클래스를 찾을 수 없습니다."));

        // 2. 권한 확인
        if (!existingClass.getMasterId().equals(userUuid)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "해당 클래스를 수정할 권한이 없습니다.");
        }

        // 2-1. addressId로 주소 조회 (주소가 변경된 경우)
        String fullAddress = existingClass.getAddress(); // 기본값은 기존 주소
        if (newClassDto.getAddressId() != null && !newClassDto.getAddressId().equals(existingClass.getAddressId())) {
            fullAddress = yogaCenterAddressRepository.findById(newClassDto.getAddressId())
                                                     .map(addr -> addr.getFullAddress())
                                                     .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "주소를 찾을 수 없습니다."));
        }

        // 3. 클래스 정보 업데이트
        YogaClass updatedClass = YogaClass.builder()
                                          .classId(existingClass.getClassId())
                                          .name(newClassDto.getName())
                                          .type(existingClass.getType())
                                          .description(newClassDto.getDescription())
                                          .addressId(newClassDto.getAddressId())
                                          .address(fullAddress)  // fullAddress 설정
                                          .price(newClassDto.getPrice().intValue())
                                          .masterId(existingClass.getMasterId())
                                          .mainDisplay(existingClass.getMainDisplay())
                                          .thumbnail(newClassDto.getImages() != null && !newClassDto.getImages().isEmpty()
                                              ? newClassDto.getImages().get(0)
                                              : existingClass.getThumbnail())
                                          .createdAt(existingClass.getCreatedAt())
                                          .categories(new HashSet<>())
                                          .features(new ArrayList<>())
                                          .build();

        YogaClass savedClass = yogaClassRepository.save(updatedClass);
        log.info("Updated YogaClass: {}", savedClass.getClassId());

        // 4. 기존 이미지 삭제 후 새 이미지 저장
        imageRepository.deleteByTypeAndTargetId(TargetType.CLASS, savedClass.getClassId());
        if (newClassDto.getImages() != null && !newClassDto.getImages().isEmpty()) {
            saveClassImages(savedClass.getClassId(), newClassDto.getImages());
        }

        // 5. 특징 업데이트 (기존 삭제는 orphanRemoval로 자동 처리)
        if (newClassDto.getFeaturesId() != null && !newClassDto.getFeaturesId().isEmpty()) {
            saveClassFeatures(savedClass, newClassDto.getFeaturesId());
        }

        // 6. 스케줄 업데이트 (기존 것은 유지, 새로운 것만 추가, 스케줄별 카테고리도 함께 처리)
        if (newClassDto.getSchedules() != null && !newClassDto.getSchedules().isEmpty()) {
            saveClassSchedules(savedClass.getClassId(), newClassDto.getSchedules());
        }
    }

    /**
     * 클래스 이미지 저장
     */
    private void saveClassImages(String classId, List<String> imageUrls) {
        for (int i = 0; i < imageUrls.size(); i++) {
            Image image = Image.builder()
                               .type(TargetType.CLASS)
                               .targetId(classId)
                               .url(imageUrls.get(i))
                               .orderNo(i + 1)
                               .build();
            imageRepository.save(image);
        }
        log.info("Saved {} images for class {}", imageUrls.size(), classId);
    }

    /**
     * 클래스 특징 저장
     */
    private void saveClassFeatures(YogaClass yogaClass, List<String> featureIds) {
        for (String featureIdStr : featureIds) {
            try {
                Long featureId = Long.parseLong(featureIdStr);
                Feature feature = featureRepository.findById(featureId)
                                                   .orElseThrow(
                                                       () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "특징을 찾을 수 없습니다: " + featureId));

                ClassFeature classFeature = ClassFeature.builder()
                                                        .yogaClass(yogaClass)
                                                        .feature(feature)
                                                        .build();
                yogaClass.getFeatures().add(classFeature);
            } catch (NumberFormatException e) {
                log.warn("Invalid feature ID format: {}", featureIdStr);
            }
        }
        log.info("Saved {} features for class {}", featureIds.size(), yogaClass.getClassId());
    }

    /**
     * 클래스 스케줄 저장
     */
    private void saveClassSchedules(String classId, List<NewScheduleDto> schedules) {
        for (NewScheduleDto scheduleDto : schedules) {
            // 스케줄 수정인 경우 (scheduleId가 있는 경우)
            if (scheduleDto.getScheduleId() != null) {
                updateSchedule(scheduleDto);
                continue;
            }

            // 각 날짜에 대해 스케줄 생성
            if (scheduleDto.getDates() != null) {
                for (Date date : scheduleDto.getDates()) {
                    YogaClassSchedule schedule = YogaClassSchedule.builder()
                                                                  .classId(classId)
                                                                  .specificDate(date)
                                                                  .dayOfWeek(0) // 하루수련은 요일 무관
                                                                  .startTime(scheduleDto.getStartTime())
                                                                  .endTime(scheduleDto.getEndTime())
                                                                  .minCapacity(scheduleDto.getMinCapacity())
                                                                  .maxCapacity(scheduleDto.getMaxCapacity())
                                                                  .content(scheduleDto.getName())
                                                                  .build();
                    YogaClassSchedule savedSchedule = yogaClassScheduleRepository.save(schedule);

                    // 스케줄 카테고리 저장
                    if (scheduleDto.getCategoryIds() != null && !scheduleDto.getCategoryIds().isEmpty()) {
                        saveScheduleCategories(savedSchedule.getScheduleId(), scheduleDto.getCategoryIds());
                    }
                }
            }
        }
        log.info("Saved schedules for class {}", classId);
    }

    /**
     * 스케줄 수정
     */
    private void updateSchedule(NewScheduleDto scheduleDto) {
        YogaClassSchedule existingSchedule = yogaClassScheduleRepository.findById(scheduleDto.getScheduleId())
                                                                        .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND,
                                                                            "스케줄을 찾을 수 없습니다."));

        // 날짜가 하나만 있다고 가정하고 첫 번째 날짜로 업데이트
        Date updateDate = scheduleDto.getDates() != null && !scheduleDto.getDates().isEmpty()
            ? scheduleDto.getDates().get(0)
            : existingSchedule.getSpecificDate();

        YogaClassSchedule updatedSchedule = YogaClassSchedule.builder()
                                                             .scheduleId(existingSchedule.getScheduleId())
                                                             .classId(existingSchedule.getClassId())
                                                             .specificDate(updateDate)
                                                             .dayOfWeek(existingSchedule.getDayOfWeek())
                                                             .startTime(scheduleDto.getStartTime())
                                                             .endTime(scheduleDto.getEndTime())
                                                             .content(scheduleDto.getName())
                                                             .minCapacity(scheduleDto.getMinCapacity())
                                                             .maxCapacity(scheduleDto.getMaxCapacity())
                                                             .build();

        yogaClassScheduleRepository.save(updatedSchedule);

        // 기존 스케줄 카테고리 삭제 후 새로 저장
        if (scheduleDto.getCategoryIds() != null && !scheduleDto.getCategoryIds().isEmpty()) {
            scheduleCategoryRepository.deleteByScheduleId(existingSchedule.getScheduleId());
            saveScheduleCategories(existingSchedule.getScheduleId(), scheduleDto.getCategoryIds());
        }

        log.info("Updated schedule: {}", updatedSchedule.getScheduleId());
    }

    /**
     * 스케줄 카테고리 저장
     */
    private void saveScheduleCategories(String scheduleId, List<String> categoryIds) {
        for (String categoryId : categoryIds) {
            ScheduleCategory scheduleCategory = ScheduleCategory.builder()
                                                                .scheduleId(scheduleId)
                                                                .categoryId(categoryId)
                                                                .build();
            scheduleCategoryRepository.save(scheduleCategory);
        }
        log.info("Saved {} categories for schedule {}", categoryIds.size(), scheduleId);
    }
}
