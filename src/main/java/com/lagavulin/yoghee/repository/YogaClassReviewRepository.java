package com.lagavulin.yoghee.repository;

import java.util.List;

import com.lagavulin.yoghee.entity.YogaClassReview;
import com.lagavulin.yoghee.model.dto.YogaReviewDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface YogaClassReviewRepository extends JpaRepository<YogaClassReview, String> {
    @Query(value= """
        SELECT new com.lagavulin.yoghee.model.dto.YogaReviewDto(
            r.reviewId, r.userUuid, r.thumbnail, r.content, r.rating, r.createdAt)
        FROM YogaClassReview r
        JOIN YogaClass c ON r.classId = c.classId
        WHERE r.thumbnail IS NOT NULL
        AND c.type = :type
        ORDER BY r.createdAt DESC
    """)
    List<YogaReviewDto> findYogaClassReviewByCreatedAt(String type, Pageable pageable);
}
