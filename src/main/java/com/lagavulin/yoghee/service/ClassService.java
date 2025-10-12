package com.lagavulin.yoghee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lagavulin.yoghee.entity.UserCategory;
import com.lagavulin.yoghee.entity.YogaClass;
import com.lagavulin.yoghee.model.dto.ClassClickCountDto;
import com.lagavulin.yoghee.model.dto.TodayClassDto;
import com.lagavulin.yoghee.model.dto.YogaClassDto;
import com.lagavulin.yoghee.model.dto.YogaReviewDto;
import com.lagavulin.yoghee.model.enums.ClassSortType;
import com.lagavulin.yoghee.repository.CategoryRepository;
import com.lagavulin.yoghee.repository.UserCategoryRepository;
import com.lagavulin.yoghee.repository.YogaClassClickRepository;
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
    private final CategoryRepository categoryRepository;
    private final YogaClassClickRepository yogaClassClickRepository;
    private final YogaClassReviewRepository yogaClassReviewRepository;

    /**
     * 오늘 스케줄 조회 <br>
     * /api/main/ - [로그인] data.todaySchedule
     */
    public List<TodayClassDto> getTodaySchedule(String userUuid) {
        return yogaClassRepository.findTodayClassesByUser(userUuid);
    }

    /**
     * 클릭수 순으로 N개 (최근 M일 이내), 부족한 경우 랜덤으로 N개 보충 <br>
     * /api/main/ - [비로그인] data.customizedClass
     */
    public List<YogaClassDto> getClickedTopNClassLastMDays(String type, int n, int m){
        LocalDate fromDate = LocalDate.now().minusDays(m);
        Set<String> classIds = new HashSet<>(yogaClassClickRepository.findTopClickedClasses(type, fromDate, PageRequest.of(0, n))
                                                                        .stream()
                                                                        .map(ClassClickCountDto::getClassId)
                                                                        .toList());
        if(classIds.size() < n){
            int remaining = n - classIds.size();
            classIds.addAll(yogaClassRepository.findRandomNClassByType(type, PageRequest.of(0, remaining)));
        }
        return yogaClassRepository.findWithReviewStatsByClassIds(classIds);
    }
    /**
     * 사용자 관심 카테고리 기반 클래스 N개 (랜덤) <br>
     * /api/main/ - [로그인] data.customizedClass
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
        if(classIds.size() < n){
            int remaining = n - classIds.size();
            classIds.addAll(yogaClassRepository.findRandomNClassByType(type, PageRequest.of(0, remaining)));
        }

        return yogaClassRepository.findWithReviewStatsByClassIds(classIds);
    }

    /**
     * 최근 7일 이내 신규 회원이 가장 많이 등록한 클래스 N개, 부족한 경우 랜덤으로 N개 보충 <br>
     * /api/main/ - [공통] data.recommendClass<br>
     */
    public List<YogaClassDto> getNewSignUpTopNClassSinceStartDate(String type, int n){
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        Set<String> classIds = new HashSet<>(yogaClassRepository.findNewSignUpTopNClassSinceStartDate(type, oneWeekAgo, PageRequest.of(0, n)));
        if(classIds.size() < n){
            int remaining = n - classIds.size();
            classIds.addAll(yogaClassRepository.findRandomNClassByType(type, PageRequest.of(0, remaining)));
        }
        return yogaClassRepository.findWithReviewStatsByClassIds(classIds);
    }

    /**
     * 메인 노출 클래스 N개 (랜덤) - MAIN_DISPLAY = 'Y' <br>
     * /api/main/ - [공통] data.recommendClass<br>
     */
    public List<YogaClassDto> getRecommendNClass(String type, int n) {
        return yogaClassRepository.findAllByMainDisplay(type, PageRequest.of(0, n))
                                  .stream()
                                  .map(YogaClass::toDto)
                                  .toList();
    }

    /**
     * 이미지 있는 후기 최근순으로 N개
     * /api/main/ - [공통] data.newReview<br>
     */
    public List<YogaReviewDto> getRecentNClassReviewWithImage(String type, int n) {
        return yogaClassReviewRepository.findYogaClassReviewByCreatedAt(type, PageRequest.of(0, n));

    }

    public List<YogaClassDto> getCategoryClasses(String type, String categoryId, ClassSortType classSortType) {
        return switch (classSortType) {
            case RECOMMEND -> yogaClassRepository.findMostJoinedClassByTypeAndCategoryId(type, categoryId);
            case REVIEW -> yogaClassRepository.findHighestRatedClassByTypeAndCategoryId(type, categoryId);
            case RECENT -> yogaClassRepository.findRecentClassByTypeAndCategoryId(type, categoryId);
        };
    }
}
