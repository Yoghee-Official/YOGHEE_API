package com.lagavulin.yoghee.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lagavulin.yoghee.entity.UserCategory;
import com.lagavulin.yoghee.entity.UserFavorite;
import com.lagavulin.yoghee.entity.YogaCenter;
import com.lagavulin.yoghee.model.dto.YogaCenterDto;
import com.lagavulin.yoghee.repository.UserCategoryRepository;
import com.lagavulin.yoghee.repository.UserFavoriteRepository;
import com.lagavulin.yoghee.repository.YogaCenterRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CenterService {
    private final YogaCenterRepository yogaCenterRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserFavoriteRepository userFavoriteRepository;

    public List<YogaCenterDto> getNewSignUpTopNCenterSinceStartDate(String type, int n) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        Set<String> centerIds = new HashSet<>(yogaCenterRepository.findNewSignUpTopNClassSinceStartDate(type, oneWeekAgo, PageRequest.of(0, n)));
        return yogaCenterRepository.findYogaCenterWithFavoriteCount(centerIds);
    }

    public List<YogaCenterDto> getUserCategoryNCenter(String userUuid, int n){
        List<String> categoryIds = userCategoryRepository.findAllByUserUuid(userUuid)
                                                         .stream()
                                                         .map(UserCategory::getCategoryId)
                                                         .toList();

        Set<String> centerIds = new HashSet<>(yogaCenterRepository.findRandomByCategoryIds(categoryIds, PageRequest.of(0, n))
                                                                .stream()
                                                                .map(YogaCenter::getCenterId)
                                                                .toList());
        if(centerIds.size() < n){
            int remaining = n - centerIds.size();
            centerIds.addAll(yogaCenterRepository.findRandomNCenter(PageRequest.of(0, remaining)));
        }

        return yogaCenterRepository.findWithReviewStatsByCenterIds(centerIds);
    }

    public List<YogaCenterDto> getClickedTopNCenterLastMDays(int n, int day) {
        LocalDate fromDate = LocalDate.now().minusDays(day);
        Set<String> centerIds = new HashSet<>(yogaCenterRepository.findTopClickedClasses(fromDate, PageRequest.of(0, n)));
        if(centerIds.size() < n){
            int remaining = n - centerIds.size();
            centerIds.addAll(yogaCenterRepository.findRandomNCenter(PageRequest.of(0, remaining)));
        }
        return yogaCenterRepository.findWithReviewStatsByCenterIds(centerIds);
    }

    public void addFavoriteClass(String userUuid, String centerId) {
        userFavoriteRepository.save(UserFavorite.builder()
                                                .id(centerId)
                                                .type("CENTER")
                                                .userUuid(userUuid)
                                                .createdAt(new Date())
                                                .build());
    }
}