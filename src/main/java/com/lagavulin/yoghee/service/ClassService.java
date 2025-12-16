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
import com.lagavulin.yoghee.entity.UserFavorite;
import com.lagavulin.yoghee.entity.YogaClass;
import com.lagavulin.yoghee.model.dto.CategoryClassDto;
import com.lagavulin.yoghee.model.dto.YogaClassDto;
import com.lagavulin.yoghee.model.dto.YogaReviewDto;
import com.lagavulin.yoghee.model.enums.ClassSortType;
import com.lagavulin.yoghee.model.enums.TargetType;
import com.lagavulin.yoghee.repository.ImageRepository;
import com.lagavulin.yoghee.repository.UserCategoryRepository;
import com.lagavulin.yoghee.repository.UserFavoriteRepository;
import com.lagavulin.yoghee.repository.YogaClassRepository;
import com.lagavulin.yoghee.repository.YogaClassReviewRepository;
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
}
